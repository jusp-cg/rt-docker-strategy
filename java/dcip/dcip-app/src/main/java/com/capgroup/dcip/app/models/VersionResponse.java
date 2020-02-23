package com.capgroup.dcip.app.models;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class VersionResponse {
    private String comment;
    private ZonedDateTime createdTimestamp;
    private ZonedDateTime modifiedTimestamp;
    private String createdBy;
    private String modifiedBy;
    private String contentType;
    private String versionId;
    private String fileName;
    private TemporalEntity.Status status;
    private Long contentLength;
}
