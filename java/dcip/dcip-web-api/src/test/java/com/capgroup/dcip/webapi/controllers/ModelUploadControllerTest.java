package com.capgroup.dcip.webapi.controllers;

import com.capgroup.dcip.app.models.ModelDetails;
import com.capgroup.dcip.app.models.ModelService;
import com.capgroup.dcip.app.models.VersionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ControllersTestConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebMvcTest(controllers = ModelUploadController.class, secure = false)
@Import(ModelUploadController.class)
public class ModelUploadControllerTest {
    @MockBean
    ModelService modelService;

    @Autowired
    MockMvc mockMvc;

    MockMultipartFile file;
    String path;

    @Before
    public void init() throws IOException{
        path = new File("").getAbsolutePath() + "/src/test/java/com/capgroup/dcip/webapi/controllers/controllerTest.txt";
        file = convertFileToMultipart(new File(path));
    }

    @Test
    public void testPostModel() throws Exception {
        ModelDetails details = new ModelDetails();
        details.setId(4L);
        details.setName("Test mode");
        details.setDescription("Modoc county");
        details.setCanvasId(5L);

        given(modelService.createModel(details)).willReturn(details);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/dcip/tool/models").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(details))).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4L));
    }

    @Test
    public void testPutModel() throws Exception {
        ModelDetails details = new ModelDetails();
        details.setName("Model test name");
        details.setDescription("This model will predict the next recession with 110% accuracy");
        details.setCanvasId(87L);

        given(modelService.updateModel(6L, details)).willReturn(details);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/dcip/tool/models/" + 6).contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(details))).andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("This model will predict the next recession with 110% accuracy"));
    }

    @Test
    public void testDeleteModel() throws Exception {
        long testModelId = 8L;
        ModelDetails details = new ModelDetails();
        details.setId(8L);

        given(modelService.deleteModel(testModelId)).willReturn(details);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/dcip/tool/models/" + testModelId)).andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(details.getId()));
    }

    @Test
    public void testGetCanvasModels() throws Exception {
        Long canvasId = 87L;
        Long profileId = 8L;
        List<ModelDetails> li = new ArrayList<>();

        ModelDetails details = new ModelDetails();
        details.setCanvasId(canvasId);
        details.setId(1L);
        ModelDetails secDetails = new ModelDetails();
        secDetails.setCanvasId(canvasId);
        secDetails.setId(2L);
        li.add(details);
        li.add(secDetails);

        given(modelService.findModels(canvasId)).willReturn(li);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/dcip/tool/models").contentType(MediaType.APPLICATION_JSON).param("canvasId", canvasId.toString())).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    public void testGetVersionMetadata() throws Exception {
        Long modelId = 10L;

        VersionResponse response = new VersionResponse();
        response.setVersionId("versionOne");
        response.setComment("Test comment");
        List<VersionResponse> responses = new ArrayList<>();
        responses.add(response);
        given(modelService.getVersionMetadata(modelId)).willReturn(responses);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/dcip/tool/models/" + modelId + "/versions")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comment").value("Test comment"))
                .andExpect(jsonPath("$[0].versionId").value("versionOne"));
    }

    @Test
    public void testPostVersion() throws Exception {
        Long canvasId = 15L;
        Long modelId = 24L;

        VersionResponse response = new VersionResponse();
        response.setComment("Test comm");

        given(modelService.uploadVersion(modelId, file, "Test comm")).willReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/dcip/tool/models/"  + modelId + "/versions").file(file).param("canvasId", canvasId.toString()).param("comments", "Test comm").contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Test comm"));
    }

    @Test
    public void testGetVersion() throws Exception {
        Long modelId = 45L;
        String versionId = "5GLHH8";
        File file = new File(this.path);
        Path path = Paths.get(this.path);
        byte[] data = Files.readAllBytes(path);

        Resource resource = new FileSystemResource(file);
        given(modelService.downloadVersion(modelId, versionId)).willReturn(resource);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/dcip/tool/models/" + modelId + "/versions/" + versionId)).andExpect(status().isOk())
                .andExpect(content().bytes(data));
    }

    @Test
    public void testDeleteVersion() throws Exception {
        Long modelId = 34L;
        VersionResponse response = new VersionResponse();
        String versionId = "5GLFF4";
        response.setVersionId(versionId);
        given(modelService.deleteVersion(modelId, versionId)).willReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/dcip/tool/models/" + modelId + "/versions/" + versionId)).andExpect(status().isOk());
    }

    private MockMultipartFile convertFileToMultipart(File newFile) throws IOException {
        return new MockMultipartFile("file", newFile.getName(), "text/plain", new FileInputStream(newFile));
    }
}

