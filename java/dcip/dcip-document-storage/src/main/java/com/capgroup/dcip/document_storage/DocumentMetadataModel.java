package com.capgroup.dcip.document_storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentMetadataModel {
    private String container;
    private String key;
    private String versionId;
    private Status status;
    private ZonedDateTime modifiedTimestamp;
    private ZonedDateTime createdTimestamp;
    private String createdBy;
    private String modifiedBy;
    private Long contentLength; //size in bytes
    private String contentType;
    private Map<String, String> properties; //store comments tags, model name, model description
}
