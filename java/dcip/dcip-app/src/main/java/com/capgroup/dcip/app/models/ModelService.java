package com.capgroup.dcip.app.models;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ModelService {
    ModelDetails createModel(ModelDetails file);

    ModelDetails updateModel(Long modelId, ModelDetails details);

    ModelDetails deleteModel(long modelId);

    Resource downloadVersion(long modelId, String versionId) throws IOException;

    VersionResponse deleteVersion(long modelId, String versionId);

    Iterable<ModelDetails> findModels(Long canvasId);

    Iterable<VersionResponse> getVersionMetadata(long modelId);

    VersionResponse uploadVersion(long modelId, MultipartFile file, String comments) throws IOException;
}
