package com.capgroup.dcip.app.models;

import com.capgroup.dcip.app.ServiceTestConfig;
import com.capgroup.dcip.app.canvas.CanvasItemModel;
import com.capgroup.dcip.app.canvas.CanvasModel;
import com.capgroup.dcip.app.canvas.CanvasService;
import com.capgroup.dcip.document_storage.DocumentMetadataModel;
import com.capgroup.dcip.document_storage.DocumentStorageApi;
import com.capgroup.dcip.domain.canvas.WorkbenchResource;
import com.capgroup.dcip.domain.entity.EntityType;
import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.domain.models.Model;
import com.capgroup.dcip.infrastructure.repository.ModelRepository;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;

@SpringBootTest()
@ContextConfiguration(classes = ServiceTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Import(ModelServiceImpl.class)
public class ModelServiceImplTest {
    @MockBean
    EntityManager entityManager;
    @MockBean
    ModelRepository modelRepository;
    @MockBean
    ModelMapperImpl modelMapper;
    @MockBean
    CanvasService canvasService;
    @Autowired
    ModelServiceImpl service;
    @MockBean
    DocumentMapperImpl documentMapper;
    @MockBean
    DocumentStorageApi documentStorageApi;

    MultipartFile file;

    String path;


    @Before
    public void init() throws IOException{
        path = new File("").getAbsolutePath() + "/src/test/resources/testfile.txt";
        file = convertFileToMultipart(new File(path));
    }

    @Test
    public void testCreateModel() {
        ModelDetails details = new ModelDetails();
        details.setName("DetailName");
        details.setDescription("Test out the details");
        details.setCreatedTimestamp(LocalDateTime.now());
        details.setCanvasId(36L);
        details.setId(8L);
        details.setRole("role");

        Model entity = new Model();
        entity.setName("DetailName");
        entity.setDescription("Test out the details");
        entity.setId(8L);

        CanvasModel model = new CanvasModel();
        model.setId(36L);

        given(this.modelMapper.map(details)).willReturn(entity);
        given(this.modelRepository.save(entity)).willReturn(entity);
        given(this.canvasService.addCanvasItem(36L, WorkbenchResource.WorkbenchResourceId.MODEL, 36L)).willReturn(model);
        given(this.modelMapper.map(entity, details.getCanvasId())).willReturn(details);

        ModelDetails savedDetails = service.createModel(details);
        assertEquals(details.getName(), savedDetails.getName());
        assertEquals(details.getDescription(), savedDetails.getDescription());
        assertEquals(details.getCreatedTimestamp(), savedDetails.getCreatedTimestamp());
        assertEquals(details.getCanvasId(), savedDetails.getCanvasId());
        assertEquals(details.getId(), savedDetails.getId());
    }

    @Test
    public void testUpdateModel() {
        ModelDetails details = new ModelDetails();
        details.setName("DetailName");
        details.setDescription("Test out the details");
        details.setCreatedTimestamp(LocalDateTime.now());
        details.setCanvasId(36L);
        details.setRole("role");

        Model entity = new Model();
        entity.setName("DetailName");
        entity.setDescription("Test out the details");
        entity.setId(3L);

        CanvasModel model = new CanvasModel();
        model.setId(36L);

        given(this.modelRepository.findById(3L)).willReturn(Optional.of(entity));
        given(this.modelRepository.save(entity)).willReturn(entity);
        given(this.canvasService.findByCanvasItemEntityId(WorkbenchResource.WorkbenchResourceId.MODEL, entity.getId())).willReturn(Optional.of(model));
        given(this.modelMapper.map(entity)).willReturn(details);

        ModelDetails savedDetails = service.updateModel(3L, details);
        assertEquals(details.getName(), savedDetails.getName());
        assertEquals(details.getDescription(), savedDetails.getDescription());
        assertEquals(details.getCreatedTimestamp(), savedDetails.getCreatedTimestamp());
        assertEquals(details.getCanvasId(), savedDetails.getCanvasId());
        assertEquals(details.getId(), savedDetails.getId());
    }

    @Test
    public void testDeleteModels() {
        ModelDetails details = new ModelDetails();
        details.setName("DetailName");
        details.setDescription("Test out the details");
        details.setCreatedTimestamp(LocalDateTime.now());
        details.setCanvasId(36L);
        details.setProfileId(47L);
        details.setRole("role");
        details.setId(50L);

        Model entity = new Model();
        entity.setName("DetailName");
        entity.setDescription("Test out the details");
        entity.setId(50L);

        CanvasModel model = new CanvasModel();
        model.setId(36L);

        given(canvasService.deleteCanvasItem(WorkbenchResource.WorkbenchResourceId.MODEL, 50L)).willReturn(null);
        given(this.documentStorageApi.deleteDocument(this.service.container, entity.getKey(), false)).willReturn(null);
        given(this.modelRepository.findById(50L)).willReturn(Optional.of(entity));
        given(this.modelRepository.save(entity)).willReturn(entity);

        service.deleteModel(details.getId());
    }

    @Test
    public void testGetModels() {
        ModelDetails details = new ModelDetails();
        details.setName("DetailName");
        details.setDescription("Test out the details");
        details.setCreatedTimestamp(LocalDateTime.now());
        details.setCanvasId(36L);
        details.setProfileId(47L);
        details.setId(1L);
        details.setRole("role");

        ModelDetails secDetails = new ModelDetails();
        secDetails.setName("DetailName");
        secDetails.setDescription("Test out the details");
        secDetails.setCreatedTimestamp(LocalDateTime.now());
        secDetails.setCanvasId(36L);
        secDetails.setProfileId(47L);
        secDetails.setId(2L);
        secDetails.setRole("role");

        CanvasItemModel modelOne = new CanvasItemModel();
        CanvasItemModel modelTwo = new CanvasItemModel();
        modelOne.setCanvasId(36L);
        modelOne.setProfileId(47L);
        modelOne.setEntityId(1L);
        modelTwo.setCanvasId(36L);
        modelTwo.setProfileId(47L);
        modelTwo.setEntityId(2L);
        List<CanvasItemModel> models = new ArrayList<>();
        models.add(modelOne);
        models.add(modelTwo);

        List<Model> entities = new ArrayList<>();
        Model firstEntity = new Model();
        Model secEntity = new Model();
        firstEntity.setDescription(details.getDescription());
        firstEntity.setName(details.getName());
        firstEntity.setId(1L);
        secEntity.setId(2L);
        secEntity.setName(secDetails.getName());
        secEntity.setDescription(secDetails.getDescription());
        entities.add(firstEntity);
        entities.add(secEntity);
        List<Long> modelIds = new ArrayList<>();
        entities.stream().forEach((entity) -> modelIds.add(entity.getId()));

        given(this.canvasService.canvasItemsForCanvasAndWorkbenchResource(36L,  WorkbenchResource.WorkbenchResourceId.MODEL.getId())).willReturn(models);
        given(this.modelRepository.findByIdIn(modelIds)).willReturn(entities);
        given(this.modelMapper.map(firstEntity)).willReturn(details);
        given(this.modelMapper.map(secEntity)).willReturn(secDetails);

        Iterable<ModelDetails> detailsIter = service.findModels(36L);
        List<ModelDetails> detailsList = new ArrayList<>();
        detailsIter.forEach((model) -> detailsList.add(model));
        assertEquals(2, detailsList.size());
        assertEquals(details.getId(), detailsList.get(0).getId());
        assertEquals(secDetails.getId(), detailsList.get(1).getId());
    }

    @Test
    public void testDownloadVersion() throws IOException{
        Model entity = new Model();
        entity.setName("TestingDetails");
        entity.setDescription("Test this");
        entity.setId(5L);
        entity.setStatus(TemporalEntity.Status.ACTIVE);
        entity.setEntityType(new EntityType(1, "name", "description"));
        String versionId = "test version";
        Optional<Model> modelOpt = Optional.of(entity);
        Resource resource = new FileSystemResource(path);

        given(this.modelRepository.findById(5L)).willReturn(modelOpt);
        given(this.documentStorageApi.getDocument(this.service.container, entity.getKey(), versionId)).willReturn(resource);

        Resource response = service.downloadVersion(5L, versionId);
        assertEquals(file.getOriginalFilename(), response.getFilename());
        assertEquals(new String(file.getBytes()), new String(IOUtils.toByteArray(response.getInputStream())));
    }

    @Test
    public void testDeleteVersion() {
        Model entity = new Model();
        entity.setName("TestingDetails");
        entity.setDescription("Test this");
        entity.setId(5L);
        String versionId = "test version";
        Optional<Model> modelOpt = Optional.of(entity);
        VersionResponse versionBody = new VersionResponse();
        versionBody.setVersionId(versionId);
        versionBody.setContentType("plain-text");

        DocumentMetadataModel docModel = new DocumentMetadataModel();
        docModel.setVersionId(versionId);
        docModel.setContentType("plain-text");
        Optional<DocumentMetadataModel> docOpt = Optional.of(docModel);

        given(this.modelRepository.findById(5L)).willReturn(modelOpt);
        given(this.documentStorageApi.deleteVersion(this.service.container, entity.getKey(), versionId, false)).willReturn(docOpt);
        given(this.documentMapper.map(docModel)).willReturn(versionBody);

        VersionResponse resp = service.deleteVersion(5L, versionId);
        assertEquals("test version", resp.getVersionId());
        assertEquals("plain-text", resp.getContentType());
    }

    @Test
    public void testGetVersionMetadata() {
        Long modelId = 8L;
        Model entity = new Model();
        entity.setName("TestingDetails");
        entity.setDescription("Test this");
        entity.setId(modelId);

        List<DocumentMetadataModel> modelList = new ArrayList<>();
        DocumentMetadataModel metadataModel = new DocumentMetadataModel();
        DocumentMetadataModel secMetadataModel = new DocumentMetadataModel();
        metadataModel.setVersionId("test version");
        metadataModel.setContentType("buck");
        secMetadataModel.setVersionId("second test version");
        secMetadataModel.setContentType("segundo");
        modelList.add(metadataModel);
        modelList.add(secMetadataModel);

        VersionResponse response = new VersionResponse();
        VersionResponse secResponse = new VersionResponse();
        response.setContentType("buck");
        response.setVersionId("test version");
        secResponse.setVersionId("second test version");
        secResponse.setContentType("segundo");

        List<VersionResponse> responseList = new ArrayList<>();
        responseList.add(response);
        responseList.add(secResponse);

        given(this.modelRepository.findById(modelId)).willReturn(Optional.of(entity));
        given(this.documentStorageApi.getDocumentMetadata(this.service.container, entity.getKey(),false)).willReturn(modelList);
        given(this.documentMapper.map(metadataModel)).willReturn(response);
        given(this.documentMapper.map(secMetadataModel)).willReturn(secResponse);

        Iterable<VersionResponse> responses = service.getVersionMetadata(modelId);
        List<VersionResponse> responseTests = new ArrayList<>();
        responses.forEach((resp) -> responseTests.add(resp));
        assertEquals(responseTests.size(), responseList.size());
        assertEquals(responseTests.get(0).getContentType(), responseList.get(0).getContentType());
        assertEquals(responseTests.get(1).getVersionId(), responseList.get(1).getVersionId());
    }

    private MultipartFile convertFileToMultipart(File newFile) throws IOException {
        return new MockMultipartFile("file", newFile.getName(), "text/plain", new FileInputStream(newFile));
    }
}

