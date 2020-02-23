package com.capgroup.dcip.app.thesis.service;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.canvas.CanvasModel;
import com.capgroup.dcip.app.canvas.CanvasService;
import com.capgroup.dcip.app.context.RequestContextService;
import com.capgroup.dcip.app.entity.TemporalEntityModel;
import com.capgroup.dcip.app.event.application.CreatedApplicationEvent;
import com.capgroup.dcip.app.event.application.DeletedApplicationEvent;
import com.capgroup.dcip.app.event.application.TargetedApplicationEvent;
import com.capgroup.dcip.app.thesis.model.*;
import com.capgroup.dcip.app.virtual_view.EnableAllVirtualViews;
import com.capgroup.dcip.domain.canvas.WorkbenchResource.WorkbenchResourceId;
import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.domain.thesis.Thesis;
import com.capgroup.dcip.domain.thesis.Thesis.ThesisFilter;
import com.capgroup.dcip.domain.thesis.ThesisEdge;
import com.capgroup.dcip.domain.thesis.ThesisPoint;
import com.capgroup.dcip.infrastructure.repository.thesis.ThesisEdgeRepository;
import com.capgroup.dcip.infrastructure.repository.thesis.ThesisPointRepository;
import com.capgroup.dcip.infrastructure.repository.thesis.ThesisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.capgroup.dcip.domain.entity.TemporalEntity.Status.MARKED_FOR_DELETE;

@Service
@EnableAllVirtualViews
@Slf4j
public class ThesisServiceImpl implements ThesisService {

    private ThesisRepository thesisRepository;
    private EntityManager entityManager;
    private ThesisMapper thesisMapper;
    private CanvasService canvasService;
    private ThesisPointRepository thesisPointRepository;
    private ApplicationEventPublisher applicationEventPublisher;
    private ThesisEdgeRepository thesisEdgeRepository;
    private RequestContextService requestContextService;

    @Autowired
    public ThesisServiceImpl(ThesisRepository repository, EntityManager entityManager, ThesisMapper thesisMapper,
                             CanvasService canvasService, ThesisPointRepository thesisPointRepository,
                             ApplicationEventPublisher applicationEventPublisher,
                             ThesisEdgeRepository thesisEdgeRepository,
                             RequestContextService requestContextService) {
        this.thesisRepository = repository;
        this.entityManager = entityManager;
        this.thesisMapper = thesisMapper;
        this.canvasService = canvasService;
        this.thesisPointRepository = thesisPointRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.thesisEdgeRepository = thesisEdgeRepository;
        this.requestContextService = requestContextService;
    }

    @Transactional(readOnly = true)
    @Override
    public ThesisModel findById(long id) {
        Thesis thesis = thesis(id);

        return thesisMapper.map(thesis,
                canvasService.findByCanvasItemEntityId(WorkbenchResourceId.THESIS, id).get().getId());
    }

    @Transactional(readOnly = true)
    @Override
    public ThesisModel findByCanvasId(long canvasId) {
        Optional<Thesis> thesisOptional = thesisRepository.findByCanvasId(canvasId);

        return thesisOptional.map(thesis ->
                canvasService.findByCanvasItemEntityId(WorkbenchResourceId.THESIS,
                        thesis.getId()).map(canvasModel -> thesisMapper.map(thesis, canvasModel.getId())).orElse(null)
        ).orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public ThesisPointModel findThesisPoint(long thesisPointId) {
        return thesisMapper.map(thesisPoint(thesisPointId));
    }

    @Transactional
    @Override
    public void deleteThesis(long id) {
        // delete the canvas item
        CanvasModel canvasModel = canvasService.deleteCanvasItem(WorkbenchResourceId.THESIS, id);

        thesisRepository.findById(id).ifPresent(thesis -> {
            // create the models to be published
            Iterable<ThesisEdgeModel> thesisEdgeModels =
                    thesisMapper.mapThesisEdges(thesis.thesisEdges().collect(Collectors.toList()));
            ThesisModel thesisModel = thesisMapper.map(thesis, canvasModel.getId());

            // publish the deleted thesis edges event
            thesisEdgeModels.forEach(x -> publish(new DeletedApplicationEvent<>(x)));
            // create/publishpublish the delete thesis event
            publish(new DeletedApplicationEvent<>(thesisModel));

            // delete the thesis
            thesisRepository.deleteById(id);

            // write the changes to the DB
            entityManager.flush();

            removeUnusedThesisPoints();
        });
    }

    void removeUnusedThesisPoints() {
        // find any unused thesis points (i.e. no longer referenced anywhere)
        // remove any unreferenced thesis points
        Iterable<ThesisPoint> unusedThesisPoints = thesisPointRepository.findUnusedThesisPoints();

        // for each unused thesis point determine if they should be marked for deletion or actually deleted
        unusedThesisPoints.forEach(unusedThesisPoint -> {
            publish(new DeletedApplicationEvent<>(thesisMapper.map(unusedThesisPoint)).onComplete(evnt ->
                    thesisPointRepository.delete(unusedThesisPoint)).onReject(evnt -> unusedThesisPoint.setStatus(MARKED_FOR_DELETE)));
        });
    }

    @Transactional
    @Override
    public Iterable<ThesisEdgeModel> deleteThesisPoint(long thesisId, long childThesisPointId) {
        Thesis thesis = thesis(thesisId);

        // remove the thesis point
        Iterable<ThesisEdge> thesisEdges = thesis.removeThesisPoint(childThesisPointId);

        // publish the deleted thesis edges event
        Iterable<ThesisEdgeModel> thesisEdgeModels = thesisMapper.mapThesisEdges(thesisEdges);
        thesisEdgeModels.forEach(x -> publish(new DeletedApplicationEvent<>(x)));

        entityManager.flush();

        // remove any unreferenced thesis points
        removeUnusedThesisPoints();

        return thesisEdgeModels;
    }

    @Transactional
    @Override
    public ThesisModel createThesis(ThesisModel model) {
        // a canvas can only have one thesis
        thesisRepository.findByCanvasId(model.getCanvasId()).ifPresent(existingThesis -> {
            throw new RuntimeException("A thesis already exists for the canvas with id, " + model.getCanvasId());
        });

        // create a thesis object
        Thesis thesis = new Thesis();

        // write to the database to get the thesis id
        thesis = thesisRepository.save(thesis);
        entityManager.flush();

        // associate the Thesis with the Canvas - create a CanvasItem
        canvasService.addCanvasItem(model.getCanvasId(), WorkbenchResourceId.THESIS, thesis.getId());

        // write the canvas item to the DB
        entityManager.flush();

        // convert the Thesis to a ThesisModel
        ThesisModel thesisModel = thesisMapper.map(thesis, model.getCanvasId());

        // publish the event
        publish(new CreatedApplicationEvent<>(thesisModel));

        return thesisModel;
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<ThesisPointModel> findThesisPointsByThesis(long thesisId) {
        Thesis thesis = thesis(thesisId);

        return thesisMapper.mapThesisPoints(thesis.thesisPoints());
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<ThesisTreeModel> findThesisTree(long thesisId) {
        Thesis thesis = thesis(thesisId);
        return thesisMapper.mapTree(thesis);
    }

    @Override
    @Transactional(readOnly = true)
    public ThesisPointModel findThesisPointByThesis(long thesisId, long thesisPointId) {
        Thesis thesis = thesis(thesisId);
        return thesis.thesisPoint(thesisPointId).map(thesisMapper::map)
                .orElseThrow(() -> new ResourceNotFoundException("ThesisPoint", Long.toString(thesisPointId)));
    }

    @Override
    @Transactional
    public ThesisEdgeModel createOrAddThesisPoint(long thesisId, ThesisPointModel thesisPoint) {
        Thesis thesis = thesis(thesisId);
        ThesisEdge result = null;
        ThesisPoint point = null;
        if (thesisPoint.getId() == null) {
            result = thesis.addThesisPoint(thesisPoint.getText());
        } else {
            point = thesisPoint(thesisPoint.getId());
            result = thesis.addThesisPoint(point);
        }

        entityManager.flush();

        ThesisEdgeModel thesisEdgeModel = thesisMapper.map(result);

        // publish events
        if (point != null)
            publish(new CreatedApplicationEvent<>(thesisMapper.map(point)));
        publish(new CreatedApplicationEvent<>(thesisEdgeModel));

        return thesisEdgeModel;
    }

    @Override
    @Transactional
    public ThesisEdgeModel createOrAddThesisPoint(long thesisId, long parentThesisPointId,
                                                  ThesisPointModel thesisPoint) {
        Thesis thesis = thesis(thesisId);
        ThesisEdge result;
        ThesisPoint parentThesisPoint = thesisPoint(thesis, parentThesisPointId);
        ThesisPoint point = null;

        if (thesisPoint.getId() == null) {
            result = thesis.addThesisPoint(thesisPoint.getText(), parentThesisPoint);
        } else {
            point = thesisPoint(thesisPoint.getId());
            result = thesis.addThesisPoint(point, parentThesisPoint);
        }

        entityManager.flush();

        ThesisEdgeModel thesisEdgeModel = thesisMapper.map(result);

        // publish events
        if (point != null)
            publish(new CreatedApplicationEvent<>(thesisMapper.map(point)));
        publish(new CreatedApplicationEvent<>(thesisEdgeModel));

        return thesisEdgeModel;
    }

    @Override
    @Transactional
    public ThesisPointModel updateThesisPoint(long thesisId, long thesisPointId, ThesisPointModel thesisPointModel) {
        ThesisPoint thesisPoint = thesisPoint(thesisId, thesisPointId);
        thesisPoint.setText(thesisPointModel.getText());

        ThesisPointModel result = thesisMapper.map(thesisPoint);

        // publish the event
        publish(new CreatedApplicationEvent<>(result));

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<ThesisEdgeModel> findThesisEdges(Long thesisId, Long thesisPointId, EnumSet<ThesisFilter> filter) {
        Stream<ThesisEdge> thesisEdges =
                thesisEdgeRepository.findAllBy(thesisId, thesisPointId).stream().filter(x -> x.matches(thesisPointId,
                        filter));

        return thesisMapper.mapThesisEdges(thesisEdges.collect(Collectors.toList()));
    }

    @Override
    @Transactional(readOnly = true)
    public ThesisEdgeModel findThesisEdge(long thesisId, long thesisEdgeId) {
        return thesisMapper.map(thesis(thesisId).thesisEdge(thesisEdgeId)
                .orElseThrow(() -> new ResourceNotFoundException("ThesisEdge", Long.toString(thesisEdgeId))));
    }

    @Override
    @Transactional
    public Iterable<ThesisEdgeModel> deleteThesisEdge(long thesisId, long thesisEdgeId) {
        Iterable<ThesisEdgeModel> results = thesisRepository.findById(thesisId)
                .map(thesis -> thesisMapper.mapThesisEdges(thesis.removeThesisEdge(thesisEdgeId)))
                .orElse(Collections.emptyList());

        // publish the deleted thesis edges event
        results.forEach(x -> publish(new DeletedApplicationEvent<>(x)));

        entityManager.flush();

        // remove any unreferenced thesis points
        removeUnusedThesisPoints();

        return results;

    }

    private Thesis thesis(long thesisId) {
        return thesisRepository.findById(thesisId)
                .orElseThrow(() -> new ResourceNotFoundException("Thesis", Long.toString(thesisId)));
    }

    private ThesisPoint thesisPoint(long thesisId, long thesisPointId) {
        return thesisPoint(thesis(thesisId), thesisPointId);
    }

    private ThesisPoint thesisPoint(Thesis thesis, long thesisPointId) {
        return thesis.thesisPoint(thesisPointId)
                .orElseThrow(() -> new ResourceNotFoundException("ThesisPoint", Long.toString(thesisPointId)));
    }

    private ThesisPoint thesisPoint(long thesisPointId) {
        return thesisPointRepository.findById(thesisPointId)
                .orElseThrow(() -> new ResourceNotFoundException("ThesisPoint", Long.toString(thesisPointId)));
    }

    @Override
    @Transactional
    public Iterable<ThesisEdgeModel> deleteThesisEdge(long thesisId, long childThesisPointId,
                                                      Long parentThesisPointId) {
        Iterable<ThesisEdgeModel> results = thesisRepository.findById(thesisId).map(
                thesis -> thesisMapper.mapThesisEdges(thesis.removeThesisEdge(childThesisPointId, parentThesisPointId)))
                .orElse(Collections.emptyList());

        // publish the deleted thesis edges event
        results.forEach(x -> publish(new DeletedApplicationEvent<>(x)));

        entityManager.flush();

        // remove any unreferenced thesis points
        removeUnusedThesisPoints();

        return results;
    }

    protected <T extends TemporalEntityModel> void publish(TargetedApplicationEvent<T> event) {
        if (log.isDebugEnabled()) {
            log.debug("Firing event of type:{} for entity of type:{} with id:{}",
                    event.getClass().getName(), event.getTarget().getClass().getName(),
                    event.getTarget().getId());
        }

        event.publish(applicationEventPublisher);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<ThesisPointModel> findAllThesisPoints(Long profileId, EnumSet<TemporalEntity.Status> statuses) {
        return thesisMapper.mapThesisPoints(thesisPointRepository.findAll(profileId, statuses));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<ThesisPointModel> findVersionsOfThesisPoint(long id) {
        return thesisMapper.mapThesisPoints(thesisPointRepository.findAllVersions(id,
                requestContextService.currentProfile().getUser().getInvestmentUnit().getId())
        );
    }
}
