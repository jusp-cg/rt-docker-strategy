package com.capgroup.dcip.document_storage;

import org.springframework.core.io.Resource;

import java.util.Map;
import java.util.Optional;

public interface DocumentStorageApi {
    Resource getDocument(String container, String key);

    DocumentMetadataModel createDocument(Resource resource, String container, String key,
                                                Map<String, String> properties);


    Resource getDocument(String container, String key, String versioNo);

    Iterable<DocumentMetadataModel> getDocumentMetadata(String container, String key,
                                                        boolean deletedFlag);

    DocumentMetadataModel getVersionMetadata(String container, String key,
                                             String versionId,
                                             boolean deletedFlag);

    Iterable<DocumentMetadataModel> searchByPath(String container,
                                                 String partialPath,
                                                 boolean deletedflag);

    Optional<DocumentMetadataModel> deleteVersion(String container,
                                                  String key,
                                                  String versionId,
                                                  boolean physicalDelete);

    Iterable<DocumentMetadataModel> deleteDocument(String container,
                                                   String key,
                                                   boolean physicalDelete);

    //    Iterable<DocumentMetadataModel> searchByParams(String container,
    //                                                   Map<String, String> params,
    //                                                   boolean deletedFlag);

}
