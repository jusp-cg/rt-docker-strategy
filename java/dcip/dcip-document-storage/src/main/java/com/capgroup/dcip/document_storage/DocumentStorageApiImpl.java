package com.capgroup.dcip.document_storage;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class DocumentStorageApiImpl implements DocumentStorageApi {

    private RestTemplate restTemplate;

    private DocumentStorageApiImpl(@Named("documentStorageRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Resource getDocument(String container, String key) {
        return this.restTemplate.exchange("/documents/{container}/{key}",
                HttpMethod.GET, null, Resource.class,
                container, key).getBody();
    }

    @Override
    public DocumentMetadataModel createDocument(Resource resource, String container, String key,
                                                Map<String, String> properties) {
        // create the query
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
        builder.path("/documents/{container}/{key}");
        properties.entrySet().forEach(x ->
                builder.queryParam(x.getKey(), x.getValue()));
        String uri = builder.buildAndExpand(container, key).encode().toUriString();

        // set the headers to be multi-part
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // create the body
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", resource);

        return restTemplate.postForObject(uri, new HttpEntity<>(parts, headers), DocumentMetadataModel.class);
    }

    @Override
    public Resource getDocument(String container, String key, String versionId) {
        return this.restTemplate.exchange("/documents/{container}/" + key + "?versionId={versionId}",
                HttpMethod.GET, null, Resource.class,
                container, versionId).getBody();
    }

    @Override
    public Iterable<DocumentMetadataModel> getDocumentMetadata(String container, String key, boolean deletedFlag) {
        return this.restTemplate.exchange("/metadata/{container}/" + key,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<DocumentMetadataModel>>() {
                },
                container).getBody();
    }

    @Override
    public DocumentMetadataModel getVersionMetadata(String container, String key, String versionId,
                                                    boolean deletedFlag) {
        return this.restTemplate.exchange("/metadata/{container}/" + key + "?versionId={versionId}",
                HttpMethod.GET, null, DocumentMetadataModel.class,
                container, versionId).getBody();
    }

//    @Override
//    public Iterable<DocumentMetadataModel> searchByParams(String container, Map<String, String> params,
//                                                          boolean deletedFlag) {
//        // TODO
//        return null;
//    }

    @Override
    public Iterable<DocumentMetadataModel> searchByPath(String container, String partialPath, boolean deletedFlag) {
        return restTemplate.exchange("/search/path/{container}?deletedFlag=" +
                        "={deletedFlag}&partialPath={partialPath}",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<DocumentMetadataModel>>() {
                },
                container, deletedFlag, partialPath).getBody();
    }

    @Override
    public Iterable<DocumentMetadataModel> deleteDocument(String container, String key,
                                                          boolean physicalDelete) {
        return restTemplate.exchange("/documents/{container}/" + key + "?physicalDelete" +
                        "={physicalDelete}",
                HttpMethod.DELETE, null, new ParameterizedTypeReference<List<DocumentMetadataModel>>() {
                },
                container, physicalDelete).getBody();
    }

    @Override
    public Optional<DocumentMetadataModel> deleteVersion(String container, String key, String versionId,
                                                         boolean physicalDelete) {
        return restTemplate.exchange("/documents/{container}/" + key + "?versionId={versionId}&physicalDelete" +
                        "={physicalDelete}",
                HttpMethod.DELETE, null, new ParameterizedTypeReference<List<DocumentMetadataModel>>(){},
                container, versionId, physicalDelete).getBody().stream().findFirst();
    }
}
