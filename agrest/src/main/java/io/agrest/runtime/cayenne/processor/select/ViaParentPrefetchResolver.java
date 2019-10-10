package io.agrest.runtime.cayenne.processor.select;

import io.agrest.NestedResourceEntity;
import io.agrest.ResourceEntity;
import io.agrest.RootResourceEntity;
import io.agrest.meta.cayenne.DataObjectPropertyReader;
import io.agrest.property.PropertyReader;
import io.agrest.resolver.BaseNestedDataResolver;
import io.agrest.runtime.processor.select.SelectContext;
import org.apache.cayenne.DataObject;

import java.util.Collections;

/**
 * A resolver that doesn't run its own queries, but instead amends parent node query with prefetch spec, so that the
 * objects can be read efficiently from the parent objects. Also allows to explicitly set the prefetch semantics.
 *
 * @since 3.4
 */
public class ViaParentPrefetchResolver extends BaseNestedDataResolver<DataObject> {

    private int prefetchSemantics;

    public ViaParentPrefetchResolver(int prefetchSemantics) {
        this.prefetchSemantics = prefetchSemantics;
    }

    @Override
    protected void doOnParentQueryAssembled(NestedResourceEntity<DataObject> entity, SelectContext<?> context) {
        addPrefetch(entity, prefetchSemantics);
    }

    @Override
    protected Iterable<DataObject> doOnParentDataResolved(
            NestedResourceEntity<DataObject> entity,
            Iterable<?> parentData,
            SelectContext<?> context) {

        // all the data was fetched at the parent level... so return an empty list

        // Current limitation - if used for non-leaf node, all of its children must also use ViaParentPrefetchResolver.
        // Otherwise there will be a child data loss.
        // TODO: We need to efficiently emulate Iterable<ThisEntity> over parent data...

        return Collections.emptyList();
    }

    protected void addPrefetch(NestedResourceEntity<?> entity, int prefetchSemantics) {
        addPrefetch(entity, null, prefetchSemantics);
    }

    protected void addPrefetch(NestedResourceEntity<?> entity, String outgoingPath, int prefetchSemantics) {

        // add prefetch to the first available (grand-)parent query

        String incomingPath = entity.getIncoming().getName();
        String path = outgoingPath != null ? incomingPath + "." + outgoingPath : incomingPath;

        ResourceEntity<?> parent = entity.getParent();
        if (parent.getSelect() != null) {
            parent.getSelect().addPrefetch(path).setSemantics(prefetchSemantics);
            return;
        }

        if (parent instanceof RootResourceEntity) {
            throw new IllegalStateException(
                    "Can't add prefetch to root entity that has no SelectQuery of its own. Path: " + path);
        }

        addPrefetch(((NestedResourceEntity) parent), path, prefetchSemantics);
    }

    @Override
    public PropertyReader reader(NestedResourceEntity<DataObject> entity) {
        // While this entity is a DataObject, it doesn't mean the parent is (the reader will read from the parent).
        // However if we got that far, and "addPrefetch" didn't fail, the parent should be a DataObject...

        // TODO: what about multi-step prefetches? How do we locate parent then?

        return DataObjectPropertyReader.reader();
    }
}
