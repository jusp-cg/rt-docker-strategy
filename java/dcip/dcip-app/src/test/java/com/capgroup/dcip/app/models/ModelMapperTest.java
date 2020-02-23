package com.capgroup.dcip.app.models;

import com.capgroup.dcip.app.MappersTestConfig;
import com.capgroup.dcip.app.canvas.CanvasModel;
import com.capgroup.dcip.app.canvas.CanvasService;
import com.capgroup.dcip.domain.canvas.WorkbenchResource;
import com.capgroup.dcip.domain.models.Model;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;


@ContextConfiguration(classes = MappersTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Import(ModelMapperImpl.class)
public class ModelMapperTest {
    @Autowired
    ModelMapper modelServiceMapper;
    @MockBean
    private CanvasService canvasService;

    @Test
    public void toModelDetailsReturnTest() {
        Model entity = new Model("test name", "test description");
        entity.setId(5L);
        CanvasModel model = new CanvasModel();
        model.setId(2L);

        Optional<CanvasModel> modelOptional = Optional.of(model);
        when(canvasService.findByCanvasItemEntityId(WorkbenchResource.WorkbenchResourceId.MODEL, entity.getId())).thenReturn(modelOptional);
        ModelDetails details = modelServiceMapper.map(entity);

        assertEquals(entity.getName(), details.getName());
        assertEquals(entity.getDescription(), details.getDescription());
        assertEquals(entity.getStatus(), details.getStatus());
        assertEquals(entity.getId(), details.getId());
    }

    @Test
    public void toModelReturnTest() {
        ModelDetails details = new ModelDetails();
        details.setName("DetailName");
        details.setDescription("Test out the details");
        details.setCreatedTimestamp(LocalDateTime.now());
        details.setCanvasId(36L);
        details.setRole("role");
        details.setProfileId(10L);
        details.setId(3L);
        Model entity = modelServiceMapper.map(details);

        assertEquals(entity.getName(), details.getName());
        assertEquals(entity.getDescription(), details.getDescription());
        assertEquals(entity.getStatus(), details.getStatus());
    }
}

