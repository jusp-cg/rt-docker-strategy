package com.capgroup.dcip.app.models;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.canvas.CanvasService;
import com.capgroup.dcip.app.virtual_view.EnableAllVirtualViews;
import com.capgroup.dcip.document_storage.DocumentStorageApi;
import com.capgroup.dcip.domain.canvas.WorkbenchResource;
import com.capgroup.dcip.domain.models.Model;
import com.capgroup.dcip.infrastructure.repository.ModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * service for uploading/retrieving models
 */
@Service
@EnableAllVirtualViews
public class ModelServiceImpl implements ModelService {
    EntityManager entityManager;
    ModelRepository modelRepository;
    ModelMapper modelMapper;
    DocumentStorageApi documentStorageApi;
    DocumentMapper documentMapper;
    @Value("${model.container}")
    String container;
    private CanvasService canvasService;

    @Autowired
    public ModelServiceImpl(EntityManager manager, ModelRepository repository, ModelMapper modelMapper,
                            CanvasService canvasService,
                            DocumentStorageApi documentStorageApi,
                            DocumentMapper documentMapper) {
        this.entityManager = manager;
        this.modelRepository = repository;
        this.modelMapper = modelMapper;
        this.canvasService = canvasService;
        this.documentStorageApi = documentStorageApi;
        this.documentMapper = documentMapper;
    }

    @Override
    @Transactional
    public ModelDetails createModel(ModelDetails file) {
        Model entity = this.modelMapper.map(file);

        Model result = modelRepository.save(entity);
        entityManager.flush();

        this.canvasService.addCanvasItem(file.getCanvasId(), WorkbenchResource.WorkbenchResourceId.MODEL,
                result.getId());

        return modelMapper.map(result, file.getCanvasId());
    }

    @Override
    @Transactional
    public ModelDetails updateModel(Long id, ModelDetails details) {
        Model entity = this.modelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Model", Long.toString(id)));
        this.modelMapper.updateModel(details, entity);

        Model newEntity = this.modelRepository.save(entity);

        return this.modelMapper.map(newEntity);
    }

    @Override
    @Transactional
    public ModelDetails deleteModel(long modelId) {
        canvasService.deleteCanvasItem(WorkbenchResource.WorkbenchResourceId.MODEL, modelId);

        Model modelEntity = this.modelRepository.findById(modelId)
                .orElseThrow(() -> new ResourceNotFoundException("Model", Long.toString(modelId)));

        documentStorageApi.deleteDocument(container, modelEntity.getKey(),
                false);

        modelRepository.delete(modelEntity);

        entityManager.flush();

        return this.modelMapper.map(modelEntity);
    }

    @Override
    public Resource downloadVersion(long modelId, String versionId) {
        //TODO: throw an exception when not found
        return modelRepository.findById(modelId).map(model ->
                documentStorageApi.getDocument(container, model.getKey(), versionId)).orElseThrow(() -> new ResourceNotFoundException("Model", Long.toString(modelId)));
    }

    @Override
    @Transactional
    public VersionResponse deleteVersion(long modelId, String versionId) {
        return modelRepository.findById(modelId)
                .map(model -> documentStorageApi.deleteVersion(container,
                        model.getKey(), versionId, false).map(metaData ->
                                documentMapper.map(metaData)
                        ).orElse(null)
                ).orElse(null);
    }

    //predicate on canvas id and profile id
    @Override
    public Iterable<ModelDetails> findModels(Long canvasId) {
        // get the model ids for the canvas
        List<Long> ids = StreamSupport.stream(this.canvasService.canvasItemsForCanvasAndWorkbenchResource(canvasId,
                WorkbenchResource.WorkbenchResourceId.MODEL.getId()).spliterator(), false)
                .map(x -> x.getEntityId()).collect(Collectors.toList());

        return StreamSupport.stream(this.modelRepository.findByIdIn(ids).spliterator(), false)
                .map(modelMapper::map).collect(Collectors.toList());
    }

    @Override
    public Iterable<VersionResponse> getVersionMetadata(long modelId) {
        return modelRepository.findById(modelId)
                .map(model ->
                        StreamSupport.stream(documentStorageApi.getDocumentMetadata(container,
                                model.getKey(), false).spliterator(), false)
                                .map(documentMapper::map).collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("Model", Long.toString(modelId)));
    }

    @Override
    public VersionResponse uploadVersion(long modelId, MultipartFile file, String comments) throws IOException {

        return modelRepository.findById(modelId).map(model ->
        {
            try {
                return documentMapper.map(
                        documentStorageApi.createDocument(
                                new MultipartFileResource(file), container, model.getKey(),
                                new HashMap<String, String>() {
                                    {
                                        put(DocumentMapper.COMMENTS, comments);
                                        put(DocumentMapper.FILE_NAME, file.getOriginalFilename());
                                    }
                                }));
            } catch (Exception exc) {
                throw new RuntimeException(exc);
            }
        }).orElseThrow(() -> new ResourceNotFoundException("Model", Long.toString(modelId)));
    }

    private static class MultipartFileResource extends ByteArrayResource {

        private String filename;

        public MultipartFileResource(MultipartFile multipartFile) throws IOException {
            super(multipartFile.getBytes());
            this.filename = multipartFile.getOriginalFilename();
        }

        @Override
        public String getFilename() {
            return this.filename;
        }
    }
}
