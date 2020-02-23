package com.capgroup.dcip.app.canvas_summary;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.canvas.CanvasService;
import com.capgroup.dcip.app.relationship.RelationshipService;
import com.capgroup.dcip.app.virtual_view.EnableAllVirtualViews;
import com.capgroup.dcip.domain.canvas.WorkbenchResource.WorkbenchResourceId;
import com.capgroup.dcip.domain.canvas_summary.CanvasSummary;
import com.capgroup.dcip.infrastructure.repository.CanvasSummaryRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.EnumSet;

@Service
@EnableAllVirtualViews
public class CanvasSummaryServiceImpl implements CanvasSummaryService {

    private CanvasSummaryRepository canvasSummaryRepository;
    private CanvasSummaryMapper canvasSummaryMapper;
    private EntityManager entityManager;
    private CanvasService canvasService;
    private RelationshipService relationshipService;

    @Autowired
    public CanvasSummaryServiceImpl(CanvasSummaryRepository canvasSummaryRepository,
                                    CanvasSummaryMapper canvasSummaryMapper, EntityManager entityManager,
                                    CanvasService canvasService, RelationshipService relationshipService) {
        this.canvasSummaryRepository = canvasSummaryRepository;
        this.canvasSummaryMapper = canvasSummaryMapper;
        this.entityManager = entityManager;
        this.canvasService = canvasService;
        this.relationshipService = relationshipService;
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<CanvasSummaryModel> findAll(Long canvasId, Long profileId,
                                                EnumSet<CanvasSummaryViewOption> options) {
        CanvasSummaryRepository.ExpressionBuilder builder = new CanvasSummaryRepository.ExpressionBuilder();
        BooleanExpression expression = builder.hasCanvasItem(canvasId).and(builder.hasProfile(profileId));
        Iterable<CanvasSummary> results = Collections.emptyList();

//        if (options.containsAll(EnumSet.allOf(CanvasSummaryViewOption.class)))
//            results = canvasSummaryRepository.findAll(expression);
//        else if (options.contains(CanvasSummaryViewOption.TAGLINE))
//            results = canvasSummaryRepository.findAll(expression,
//                    Projections.constructor(CanvasSummary.class, QCanvasSummary.canvasSummary.id, QCanvasSummary
//                    .canvasSummary.entityType, QCanvasSummary.canvasSummary.event,
//                    		QCanvasSummary.canvasSummary.versionNo, QCanvasSummary.canvasSummary.versionId,
//                    		QCanvasSummary.canvasSummary.validPeriod, QCanvasSummary.canvasSummary.tagline,
//                    		QCanvasSummary.canvasSummary.status));
//        else if (options.contains(CanvasSummaryViewOption.ACTION))
//            results = canvasSummaryRepository.findAll(expression,
//                    Projections.constructor(CanvasSummary.class, QCanvasSummary.canvasSummary.id, QCanvasSummary
//                    .canvasSummary.entityType, QCanvasSummary.canvasSummary.event,
//                    		QCanvasSummary.canvasSummary.versionNo, QCanvasSummary.canvasSummary.versionId,
//                    		QCanvasSummary.canvasSummary.validPeriod, QCanvasSummary.canvasSummary.status,
//                    		QCanvasSummary.canvasSummary.action));

        results = canvasSummaryRepository.findAll(expression);
        return canvasSummaryMapper.map(results, canvasId);
    }

    @Override
    @Transactional(readOnly = true)
    public CanvasSummaryModel findById(long id) {
        return canvasSummaryRepository.findById(id).map(canvasSummary -> {
            return canvasSummaryMapper.map(canvasSummary);
        }).orElseThrow(() -> new ResourceNotFoundException("CanvasSummary", String.valueOf(id)));
    }

    @Override
    @Transactional
    public CanvasSummaryModel create(CanvasSummaryModel canvasSummaryModel) {
        CanvasSummary canvasSummary = canvasSummaryMapper.map(canvasSummaryModel);

        canvasSummary = canvasSummaryRepository.save(canvasSummary);
        entityManager.flush();

        // associate the canvasSummary with the Canvas - create a CanvasItem
        canvasService.addCanvasItem(canvasSummaryModel.getCanvasId(), WorkbenchResourceId.CANVASSUMMARY,
                canvasSummary.getId());

        return canvasSummaryMapper.map(canvasSummary, canvasSummaryModel.getCanvasId());
    }

    @Override
    @Transactional
    public CanvasSummaryModel update(long id, CanvasSummaryModel model) {
        return canvasSummaryRepository.findById(id).map(canvasSummary -> {
            canvasSummaryMapper.update(model, canvasSummary);
            return canvasSummaryMapper.map(canvasSummary);
        }).orElse(null);
    }

    @Override
    @Transactional
    public CanvasSummaryModel delete(long id) {
        relationshipService.removeRelationByProducerId(id);
        return canvasSummaryRepository.findById(id).map(canvasSummary -> {
            // delete the canvas item
            canvasService.deleteCanvasItem(WorkbenchResourceId.CANVASSUMMARY, id);

            // delete the canvasSummary
            canvasSummaryRepository.delete(canvasSummary);

            return canvasSummaryMapper.map(canvasSummary);
        }).orElse(null);
    }
}
