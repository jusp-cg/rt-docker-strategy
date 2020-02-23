package com.capgroup.dcip.app.canvas;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.context.RequestContextService;
import com.capgroup.dcip.app.virtual_view.EnableAllVirtualViews;
import com.capgroup.dcip.domain.canvas.Canvas;
import com.capgroup.dcip.domain.canvas.WorkbenchResource;
import com.capgroup.dcip.domain.canvas.WorkbenchResource.WorkbenchResourceId;
import com.capgroup.dcip.infrastructure.repository.CanvasRepository;
import com.capgroup.dcip.infrastructure.repository.WorkbenchResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@EnableAllVirtualViews
public class CanvasServiceImpl implements CanvasService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CanvasServiceImpl.class);

    private CanvasRepository canvasRepository;
    private CanvasMapper canvasMapper;
    private EntityManager entityManager;
    private WorkbenchResourceRepository workbenchResourceRepository;
    private WorkbenchResourceMapper workbenchResourceMapper;
    private RequestContextService contextService;

    @Autowired
    public CanvasServiceImpl(CanvasRepository canvasRepository, CanvasMapper canvasMapper, EntityManager entityManager,
                             WorkbenchResourceRepository workbenchResourceRepository,
							 WorkbenchResourceMapper workbenchResourceMapper,
                             RequestContextService contextService) {
        this.canvasRepository = canvasRepository;
        this.canvasMapper = canvasMapper;
        this.workbenchResourceRepository = workbenchResourceRepository;
        this.entityManager = entityManager;
        this.workbenchResourceMapper = workbenchResourceMapper;
        this.contextService = contextService;
    }

    @Override
    @Transactional
    public CanvasModel createCanvas(CanvasModel model) {

        if (canvasRepository.existsByNameIgnoreCaseAndEventProfileId(model.getName(), contextService.currentProfile().getId())) {
            throw new IllegalArgumentException("The title is already in use");
        }

        Canvas canvas = canvasMapper.map(model);
        Canvas result = canvasRepository.save(canvas);

        // flush the result to the DB
        entityManager.flush();

        return canvasMapper.map(result);
    }

    @Override
    @Transactional
    public CanvasModel updateCanvas(long id, CanvasModel model) {
        Canvas canvas = canvasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Canvas", Long.toString(id)));

        canvasMapper.updateCanvas(model, canvas);
        Canvas result = canvasRepository.save(canvas);

        // flush the result to the DB
        entityManager.flush();

        return canvasMapper.map(result);

    }

    @Override
    @Transactional(readOnly = true)
    public CanvasModel findCanvasById(long id) {
        Canvas result = canvasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Canvas", Long.toString(id)));

        return canvasMapper.map(result);
    }

    @Override
    @Transactional
    public void deleteCanvas(long id) {
        Canvas canvas = canvasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Canvas", Long.toString(id)));
        canvasRepository.delete(canvas);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<CanvasModel> findAll(String match, Long profileId) {
        return canvasMapper.mapToCanvasModels(findCanvasesFor(match, profileId));
    }

    protected Iterable<Canvas> findCanvasesFor(String match, Long profileId) {
        long start = 0;
        if (LOGGER.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }

        Iterable<Canvas> canvases = canvasRepository.findAllBy(profileId, match);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Time to query in canvases for match:{}, profile:{} took {}ms", match,
                    profileId, System.currentTimeMillis() - start);
        }

        return canvases;
    }

    @Override
    @Transactional
    public Iterable<WorkbenchResourceModel> updateResourcesForCanvas(long canvasId,
                                                                     Iterable<WorkbenchResourceModel> models) {
        Canvas canvas = canvasRepository.findById(canvasId)
                .orElseThrow(() -> new ResourceNotFoundException("Canvas", Long.toString(canvasId)));

        // get the items that need to be added that haven't already been added
        List<WorkbenchResource> existingResources = canvas.getWorkbenchResources();
        StreamSupport.stream(models.spliterator(), false)
                .filter(x -> existingResources.stream().noneMatch(y -> y.getId() == x.getId()))
                .forEach(workbenchResourceModel -> {
                    WorkbenchResource workbenchResource = workbenchResourceRepository
                            .findById(workbenchResourceModel.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("WorkbenchResource",
                                    Long.toString(workbenchResourceModel.getId())));
                    canvas.addWorkbenchResource(workbenchResource);
                });

        // flush the result to the DB
        entityManager.flush();

        return workbenchResourceMapper.mapToModel(canvas.getWorkbenchResources());
    }

    @Override
    @Transactional
    public Iterable<WorkbenchResourceModel> setResourcesForCanvas(long canvasId,
                                                                  Iterable<WorkbenchResourceModel> models) {
        Canvas canvas = canvasRepository.findById(canvasId)
                .orElseThrow(() -> new ResourceNotFoundException("Canvas", Long.toString(canvasId)));
        List<WorkbenchResource> existingResources = canvas.getWorkbenchResources();

        // findAll the items to delete
        List<WorkbenchResource> itemsToRemove = existingResources.stream()
                .filter(x -> StreamSupport.stream(models.spliterator(), false).noneMatch(y -> y.getId() == x.getId()))
                .collect(Collectors.toList());
        itemsToRemove.forEach(canvas::removeWorkbenchResource);

        // get the items that need to be added
        List<WorkbenchResource> remainingResources = canvas.getWorkbenchResources();
        StreamSupport.stream(models.spliterator(), false)
                .filter(x -> remainingResources.stream().noneMatch(y -> y.getId() == x.getId()))
                .forEach(workbenchResourceModel -> {
                    WorkbenchResource workbenchResource = workbenchResourceRepository
                            .findById(workbenchResourceModel.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("WorkbenchResource",
                                    Long.toString(workbenchResourceModel.getId())));
                    canvas.addWorkbenchResource(workbenchResource);
                });

        // flush the result to the DB
        entityManager.flush();

        return workbenchResourceMapper.mapToModel(canvas.getWorkbenchResources());
    }

    @Transactional
    public CanvasModel addCanvasItem(long canvasId, WorkbenchResourceId workbenchResourceId, long entityId) {
        Canvas canvas = canvasRepository.findById(canvasId)
                .orElseThrow(() -> new ResourceNotFoundException("Canvas", Long.toString(canvasId)));

        WorkbenchResource workbenchResource = findWorkbenchResource(canvas, workbenchResourceId);

        canvas.addCanvasItem(workbenchResource, entityId);

        return canvasMapper.map(canvas);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CanvasModel> findByCanvasItemEntityId(WorkbenchResourceId workbenchResourceId, long entityId) {
        return StreamSupport
                .stream(canvasRepository.findByCanvasItemsEntityId(entityId, workbenchResourceId.getId()).spliterator(),
                        false)
                .findAny().map(canvasMapper::map);
    }

    @Override
    @Transactional
    public CanvasModel deleteCanvasItem(WorkbenchResourceId workbenchResourceId, long entityId) {
        Canvas canvas = StreamSupport
                .stream(canvasRepository.findByCanvasItemsEntityId(entityId, workbenchResourceId.getId()).spliterator(),
                        false)
                .findAny()
                .orElseThrow(() -> new RuntimeException("A canvas does not exist with an entity of id, " + entityId));

        WorkbenchResource workbenchResource = findWorkbenchResource(canvas, workbenchResourceId);

        canvas.deleteCanvasItem(workbenchResource, entityId);

        return canvasMapper.map(canvas);
    }

    private WorkbenchResource findWorkbenchResource(Canvas canvas, WorkbenchResourceId workbenchResourceId) {
        return canvas.getWorkbenchResources().stream().filter(x -> x.getId() == workbenchResourceId.getId()).findAny()
                .orElseThrow(() -> new RuntimeException(
                        "Workbench Resource, " + workbenchResourceId + ", has not been added to the canvas"));
    }

    @Override
    @Transactional(readOnly = true)
    public long canvasItemsCount(long canvasId, long workbenchResourceId) {
        Canvas canvas = canvasRepository.findById(canvasId)
                .orElseThrow(() -> new ResourceNotFoundException("Canvas", Long.toString(canvasId)));

        return StreamSupport.stream(canvas.canvasItemsForWorkbenchResource(workbenchResourceId).spliterator(), false)
                .count();
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<CanvasItemModel> canvasItemsForCanvasAndWorkbenchResource(long canvasId, long workbenchResourceId) {
        Canvas canvas = canvasRepository.findById(canvasId)
                .orElseThrow(() -> new ResourceNotFoundException("Canvas", Long.toString(canvasId)));

        return canvasMapper.mapToCanvasItemModels(canvas.canvasItemsForWorkbenchResource(workbenchResourceId));
    }

    @Override
    @Transactional
    public Iterable<CanvasItemModel> removeWorkbenchResourceForCanvas(long canvasId, long workbenchResourceId) {
        return this.workbenchResourceRepository.findById(workbenchResourceId)
                .map(workbenchResource -> canvasRepository.findById(canvasId).map(
                        canvas -> canvasMapper.mapToCanvasItemModels(canvas.removeWorkbenchResource(workbenchResource)))
                        .orElse(Collections.emptyList()))
                .orElse(Collections.emptyList());
    }
}
