package io.agrest;

import io.agrest.meta.AgEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Contains update data of a single object.
 *
 * @since 1.3
 */
public class EntityUpdate<T> {

    private Map<String, Object> values;
    private Map<String, Set<Object>> relatedIds;
    private Map<String, Object> id;
    private boolean explicitId;
    private Object mergedTo;
    private AgEntity<T> entity;

    public EntityUpdate(AgEntity<T> entity) {
        this.values = new HashMap<>();
        this.relatedIds = new HashMap<>();
        this.entity = entity;
    }

    /**
     * @since 1.19
     */
    public AgEntity<T> getEntity() {
        return entity;
    }

    public boolean hasChanges() {
        return !values.isEmpty() || !relatedIds.isEmpty();
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public Map<String, Set<Object>> getRelatedIds() {
        return relatedIds;
    }

    public void addRelatedId(String relationshipName, Object value) {
        relatedIds.computeIfAbsent(relationshipName, n -> new HashSet<>()).add(value);
    }

    /**
     * @since 1.8
     */
    public Map<String, Object> getId() {
        return id;
    }

    /**
     * @since 1.8
     */
    public Map<String, Object> getOrCreateId() {
        if (id == null) {
            id = new HashMap<>();
        }

        return id;
    }

    /**
     * @since 1.8
     */
    public void setExplicitId() {
        this.explicitId = true;
    }

    /**
     * @since 1.5
     */
    public boolean isExplicitId() {
        return explicitId;
    }

    /**
     * Returns an object that was used to merge this update to.
     *
     * @since 1.8
     */
    public Object getMergedTo() {
        return mergedTo;
    }

    /**
     * Sets an object that was used to merge this update to.
     *
     * @since 1.8
     */
    public void setMergedTo(Object mergedTo) {
        this.mergedTo = mergedTo;
    }

}
