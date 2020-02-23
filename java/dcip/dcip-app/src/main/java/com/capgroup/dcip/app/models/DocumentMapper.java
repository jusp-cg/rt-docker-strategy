package com.capgroup.dcip.app.models;

import com.capgroup.dcip.document_storage.DocumentMetadataModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface DocumentMapper {
    String FILE_NAME = "filename";
    String COMMENTS = "comments";

    VersionResponse map(DocumentMetadataModel model);

    @AfterMapping
    default void afterMapping(DocumentMetadataModel model, @MappingTarget VersionResponse response) {
        if (model.getProperties() != null) {
            response.setFileName(model.getProperties().get(FILE_NAME));
            response.setComment(model.getProperties().get(COMMENTS));
        }
    }
}
