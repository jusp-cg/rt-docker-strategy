import com.capgroup.dcip.document_storage.DocumentMetadataModel;
import com.capgroup.dcip.document_storage.DocumentStorageApiImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Named;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

@SpringBootTest()
@ContextConfiguration(classes = DocumentTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Import(DocumentStorageApiImpl.class)
public class DocumentStorageTest {
    @Autowired
    DocumentStorageApiImpl documentStorageApi;

    @MockBean
    RestTemplate restTemplate;

    @Test
    public void testGetDocument() throws IOException {
        byte[] arr = new byte[1];
        arr[0] = 1;
        Resource resource = new ByteArrayResource(arr);
        given(this.restTemplate.exchange("/documents/{container}/{key}",
                HttpMethod.GET, null, Resource.class,
                "testContainer", "testKey")).willReturn(new ResponseEntity<>(resource, HttpStatus.OK));
        Resource docResource = this.documentStorageApi.getDocument("testContainer", "testKey");
        assertEquals(resource.contentLength(), docResource.contentLength());
        assertEquals(((ByteArrayResource) resource).getByteArray()[0], ((ByteArrayResource) docResource).getByteArray()[0]);
    }

    @Test
    public void testCreateDocument() {
        DocumentMetadataModel model = new DocumentMetadataModel();
        model.setKey("TestKey");
        model.setContainer("TestContainer");
        model.setContentType("TestContent");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        byte[] arr = new byte[1];
        arr[0] = 1;
        Resource resource = new ByteArrayResource(arr);
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
        builder.path("/documents/{container}/{key}");

        Map<String, String> properties = new HashMap<>();
        properties.put("NewFirst", "TestFirst");
        properties.put("Seconded", "TestSec");

        properties.entrySet().forEach(x ->
                builder.queryParam(x.getKey(), x.getValue()));
        String uri = builder.buildAndExpand("testContainer", "testKey").encode().toUriString();

        // create the body
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", resource);

        given(this.restTemplate.postForObject(uri, new HttpEntity<>(parts, headers), DocumentMetadataModel.class)).willReturn(model);
        DocumentMetadataModel testModel = this.documentStorageApi.createDocument(resource, "testContainer", "testKey", properties);

        assertEquals(model.getKey(), testModel.getKey());
        assertEquals(model.getContainer(), testModel.getContainer());
        assertEquals(model.getContentType(), testModel.getContentType());
    }

    @Test
    public void getDocument() throws IOException {
        byte[] arr = new byte[1];
        arr[0] = 1;
        Resource resource = new ByteArrayResource(arr);
        String container = "newContainer";
        String key = "newKey";
        String versionId = "ABCDEF";

        given(this.restTemplate.exchange("/documents/{container}/" + key + "?versionId={versionId}",
                HttpMethod.GET, null, Resource.class,
                container, versionId)).willReturn(new ResponseEntity<>(resource, HttpStatus.OK));
        Resource docResource = this.documentStorageApi.getDocument(container, key, versionId);

        assertEquals(resource.contentLength(), docResource.contentLength());
        assertEquals(((ByteArrayResource) resource).getByteArray()[0], ((ByteArrayResource) docResource).getByteArray()[0]);
    }

    @Test
    public void getDocumentMetadata() {
        boolean deletedFlag = true;
        String container = "FifthCont";
        String key = "leastKey";

        List<DocumentMetadataModel> models = new ArrayList<>();
        DocumentMetadataModel firstModel = new DocumentMetadataModel();
        DocumentMetadataModel secModel = new DocumentMetadataModel();

        firstModel.setContainer(container);
        firstModel.setKey(key);
        firstModel.setVersionId("Vers");
        secModel.setContainer(container);
        secModel.setKey(key);
        secModel.setVersionId("Fulls");

        models.add(firstModel);
        models.add(secModel);

        given(this.restTemplate.exchange("/metadata/{container}/" + key,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<DocumentMetadataModel>>() {
                },
                container)).willReturn(new ResponseEntity<>(models, HttpStatus.OK));

        List<DocumentMetadataModel> modelList = (List<DocumentMetadataModel>) this.documentStorageApi.getDocumentMetadata(container, key, deletedFlag);
        assertEquals(modelList.size(), models.size());
        assertEquals(modelList.get(0).getKey(), models.get(0).getKey());
        assertEquals(modelList.get(1).getContainer(), models.get(1).getContainer());
    }

    @Test
    public void getVersionMetadata() {
        boolean deletedFlag = true;
        String container = "FifthCont";
        String key = "leastKey";
        String version = "testVersion";

        DocumentMetadataModel firstModel = new DocumentMetadataModel();
        firstModel.setContainer(container);
        firstModel.setKey(key);
        firstModel.setVersionId("Vers");

        given(this.restTemplate.exchange("/metadata/{container}/" + key + "?versionId={versionId}",
                HttpMethod.GET, null, DocumentMetadataModel.class,
                container, version)).willReturn(new ResponseEntity<>(firstModel, HttpStatus.OK));

        DocumentMetadataModel model = this.documentStorageApi.getVersionMetadata(container, key, version, deletedFlag);
        assertEquals(model.getKey(), firstModel.getKey());
        assertEquals(model.getContainer(), firstModel.getContainer());
        assertEquals(model.getVersionId(), firstModel.getVersionId());
    }

    @Test
    public void searchByPath() {
        String container = "testContainer";
        String partialPath = "testPartial";
        boolean deletedFlag = true;
        List<DocumentMetadataModel> models = new ArrayList<>();
        DocumentMetadataModel firstModel = new DocumentMetadataModel();
        DocumentMetadataModel secModel = new DocumentMetadataModel();

        firstModel.setContainer(container);
        firstModel.setKey(partialPath);
        firstModel.setVersionId("Vers");
        secModel.setContainer(container);
        secModel.setKey(partialPath);
        secModel.setVersionId("Fulls");
        models.add(firstModel);
        models.add(secModel);

        given(this.restTemplate.exchange("/search/path/{container}?deletedFlag=" +
                        "={deletedFlag}&partialPath={partialPath}",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<DocumentMetadataModel>>() {
                },
                container, deletedFlag, "Partial")).willReturn(new ResponseEntity<>(models, HttpStatus.OK));
        List<DocumentMetadataModel> modelList = (List<DocumentMetadataModel>) this.documentStorageApi.searchByPath(container, "Partial", deletedFlag);
        assertEquals(modelList.size(), models.size());
        assertEquals(modelList.get(0).getKey(), models.get(0).getKey());
        assertEquals(modelList.get(1).getContainer(), models.get(1).getContainer());
    }

    @Test
    public void testDeleteDocument() {
        String container = "theCont";
        String key = "testKey";
        boolean physicalDelete = false;

        List<DocumentMetadataModel> models = new ArrayList<>();
        DocumentMetadataModel firstModel = new DocumentMetadataModel();
        DocumentMetadataModel secModel = new DocumentMetadataModel();
        firstModel.setContainer(container);
        firstModel.setKey(key);
        firstModel.setVersionId("Vers");
        secModel.setContainer(container);
        secModel.setKey(key);
        secModel.setVersionId("Fulls");
        models.add(firstModel);
        models.add(secModel);


        given(this.restTemplate.exchange("/documents/{container}/" + key + "?physicalDelete" +
                        "={physicalDelete}",
                HttpMethod.DELETE, null, new ParameterizedTypeReference<List<DocumentMetadataModel>>() {
                },
                container, physicalDelete)).willReturn(new ResponseEntity<>(models, HttpStatus.OK));
        List<DocumentMetadataModel> modelList = (List<DocumentMetadataModel>) this.documentStorageApi.deleteDocument(container, key, physicalDelete);
        assertEquals(modelList.size(), models.size());
        assertEquals(modelList.get(0).getKey(), models.get(0).getKey());
        assertEquals(modelList.get(1).getContainer(), models.get(1).getContainer());
    }

    @Test
    public void testDeleteVersion() {
        String container = "theCont";
        String key = "testKey";
        boolean physicalDelete = true;
        String versionId = "testversion";

        DocumentMetadataModel firstModel = new DocumentMetadataModel();
        List<DocumentMetadataModel> models = new ArrayList<>();
        firstModel.setVersionId(versionId);
        firstModel.setContainer(container);
        firstModel.setKey(key);
        models.add(firstModel);

        given(restTemplate.exchange("/documents/{container}/" + key + "?versionId={versionId}&physicalDelete" +
                        "={physicalDelete}",
                HttpMethod.DELETE, null, new ParameterizedTypeReference<List<DocumentMetadataModel>>(){},
                container, versionId, physicalDelete)).willReturn(new ResponseEntity<>(models, HttpStatus.OK));

        Optional<DocumentMetadataModel> modelOptional = this.documentStorageApi.deleteVersion(container, key, versionId, physicalDelete);
        assertTrue(modelOptional.isPresent());
        assertEquals(container, modelOptional.get().getContainer());
        assertEquals(key, modelOptional.get().getKey());
        assertEquals(versionId, modelOptional.get().getVersionId());
    }
}
