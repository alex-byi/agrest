package io.agrest.sencha.runtime.processor.select;

import io.agrest.ResourceEntity;
import io.agrest.meta.AgEntity;
import io.agrest.runtime.entity.ICayenneExpMerger;
import io.agrest.runtime.entity.IExcludeMerger;
import io.agrest.runtime.entity.IIncludeMerger;
import io.agrest.runtime.entity.IMapByMerger;
import io.agrest.runtime.entity.ISizeMerger;
import io.agrest.runtime.entity.ISortMerger;
import io.agrest.runtime.meta.IMetadataService;
import io.agrest.runtime.processor.select.CreateResourceEntityStage;
import io.agrest.runtime.processor.select.SelectContext;
import io.agrest.sencha.SenchaRequest;
import io.agrest.sencha.runtime.entity.ISenchaFilterExpressionCompiler;
import org.apache.cayenne.di.Inject;
import org.apache.cayenne.exp.Expression;

public class SenchaCreateResourceEntityStage extends CreateResourceEntityStage {


    private ISenchaFilterExpressionCompiler senchaFilterProcessor;

    public SenchaCreateResourceEntityStage(
            @Inject IMetadataService metadataService,
            @Inject ICayenneExpMerger expConstructor,
            @Inject ISortMerger sortConstructor,
            @Inject IMapByMerger mapByConstructor,
            @Inject ISizeMerger sizeConstructor,
            @Inject IIncludeMerger includeConstructor,
            @Inject IExcludeMerger excludeConstructor,
            @Inject ISenchaFilterExpressionCompiler senchaFilterProcessor) {

        super(metadataService, expConstructor, sortConstructor, mapByConstructor,
                sizeConstructor, includeConstructor, excludeConstructor);

        this.senchaFilterProcessor = senchaFilterProcessor;
    }

    @Override
    protected <T> void doExecute(SelectContext<T> context) {
        super.doExecute(context);

        ResourceEntity<T> resourceEntity = context.getEntity();

        Expression e1 = parseFilter(resourceEntity.getAgEntity(), context);
        if (e1 != null) {
            resourceEntity.andQualifier(e1);
        }
    }

    protected <T> Expression parseFilter(AgEntity<?> entity, SelectContext<T> context) {
        SenchaRequest senchaRequest = SenchaRequest.get(context);
        return senchaFilterProcessor.process(entity, senchaRequest.getFilters());
    }
}
