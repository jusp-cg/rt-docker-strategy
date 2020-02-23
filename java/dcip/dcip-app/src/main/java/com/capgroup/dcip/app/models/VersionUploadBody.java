package com.capgroup.dcip.app.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@EqualsAndHashCode()
@AllArgsConstructor
public class VersionUploadBody {
    MultipartFile file;
    String comments;
    String documentKey;
    Long modelId;

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public MultipartFile getFile() {
        return file;
    }
}
