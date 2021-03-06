package io.agrest.meta;

import io.agrest.resolver.NestedDataResolver;

import java.util.Objects;

/**
 * @since 1.12
 */
public class DefaultAgRelationship implements AgRelationship {

    private String name;
    private AgEntity<?> targetEntity;
    private boolean toMany;
    private NestedDataResolver<?> dataResolver;

    public DefaultAgRelationship(
            String name,
            AgEntity<?> targetEntity,
            boolean toMany,
            NestedDataResolver<?> dataResolver) {

        this.name = name;
        this.toMany = toMany;
        this.targetEntity = Objects.requireNonNull(targetEntity);
        this.dataResolver = Objects.requireNonNull(dataResolver);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AgEntity<?> getTargetEntity() {
        return targetEntity;
    }

    @Override
    public boolean isToMany() {
        return toMany;
    }

    @Override
    public NestedDataResolver<?> getResolver() {
        return dataResolver;
    }
}
