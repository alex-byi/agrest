package com.nhl.link.rest.runtime.encoder;

import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.cayenne.DataObject;

import com.nhl.link.rest.EntityProperty;
import com.nhl.link.rest.ResourceEntity;
import com.nhl.link.rest.encoder.Encoder;
import com.nhl.link.rest.encoder.GenericEncoder;
import com.nhl.link.rest.encoder.ISODateEncoder;
import com.nhl.link.rest.encoder.ISODateTimeEncoder;
import com.nhl.link.rest.encoder.ISOTimeEncoder;
import com.nhl.link.rest.encoder.ObjectIdEncoder;
import com.nhl.link.rest.meta.LrAttribute;
import com.nhl.link.rest.meta.LrPersistentAttribute;
import com.nhl.link.rest.property.BeanPropertyReader;
import com.nhl.link.rest.property.PersistentObjectIdPropertyReader;
import com.nhl.link.rest.property.PropertyBuilder;

public class AttributeEncoderFactory implements IAttributeEncoderFactory {

	static final String UTIL_DATE = Date.class.getName();
	static final String SQL_DATE = java.sql.Date.class.getName();
	static final String SQL_TIME = Time.class.getName();
	static final String SQL_TIMESTAMP = Timestamp.class.getName();

	// these are explicit overrides for named attributes
	private Map<String, EntityProperty> attributePropertiesByPath;
	private Map<String, EntityProperty> idPropertiesByEntity;

	public AttributeEncoderFactory() {
		this.attributePropertiesByPath = new ConcurrentHashMap<>();
		this.idPropertiesByEntity = new ConcurrentHashMap<>();
	}

	@Override
	public EntityProperty getAttributeProperty(ResourceEntity<?> entity, LrAttribute attribute) {
		String key = entity.getLrEntity().getName() + "." + attribute.getName();

		EntityProperty property = attributePropertiesByPath.get(key);
		if (property == null) {
			property = buildAttributeProperty(entity, attribute);
			attributePropertiesByPath.put(key, property);
		}

		return property;
	}

	@Override
	public EntityProperty getIdProperty(ResourceEntity<?> entity) {

		String key = entity.getLrEntity().getName();

		EntityProperty property = idPropertiesByEntity.get(key);
		if (property == null) {
			property = buildIdProperty(entity);
			idPropertiesByEntity.put(key, property);
		}

		return property;
	}

	protected EntityProperty buildAttributeProperty(ResourceEntity<?> entity, LrAttribute attribute) {

		boolean persistent = attribute instanceof LrPersistentAttribute;

		int jdbcType = persistent ? ((LrPersistentAttribute) attribute).getJdbcType() : Integer.MIN_VALUE;

		Encoder encoder = buildEncoder(attribute.getJavaType(), jdbcType);
		if (persistent && DataObject.class.isAssignableFrom(entity.getType())) {
			return PropertyBuilder.dataObjectProperty().encodedWith(encoder);
		} else {
			return PropertyBuilder.property().encodedWith(encoder);
		}
	}

	protected EntityProperty buildIdProperty(ResourceEntity<?> entity) {

		LrAttribute id = entity.getLrEntity().getSingleId();

		if (id instanceof LrPersistentAttribute) {

			// Cayenne object - PK is an ObjectId (even if it is also a
			// meaningful object property)

			LrPersistentAttribute persistentId = (LrPersistentAttribute) id;
			Encoder valueEncoder = buildEncoder(persistentId.getJavaType(), persistentId.getJdbcType());
			return PropertyBuilder.property(PersistentObjectIdPropertyReader.reader()).encodedWith(
					new ObjectIdEncoder(valueEncoder));

		} else {

			// POJO - PK is an object property

			return PropertyBuilder.property(BeanPropertyReader.reader(id.getName()));
		}
	}

	/**
	 * @since 1.12
	 */
	protected Encoder buildEncoder(String javaType, int jdbcType) {

		if (UTIL_DATE.equals(javaType)) {
			if (jdbcType == Types.DATE) {
				return ISODateEncoder.encoder();
			}
			if (jdbcType == Types.TIME) {
				return ISOTimeEncoder.encoder();
			} else {
				// JDBC TIMESTAMP or something entirely unrecognized
				return ISODateTimeEncoder.encoder();
			}
		}
		// less common cases of mapping to java.sql.* types...
		else if (SQL_TIMESTAMP.equals(javaType)) {
			return ISODateTimeEncoder.encoder();
		} else if (SQL_DATE.equals(javaType)) {
			return ISODateEncoder.encoder();
		} else if (SQL_TIME.equals(javaType)) {
			return ISOTimeEncoder.encoder();
		}

		return GenericEncoder.encoder();
	}

}
