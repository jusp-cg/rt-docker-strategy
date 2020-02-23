package com.capgroup.dcip.app.canvas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.util.SerializationUtils;

import com.capgroup.dcip.app.MappersTestConfig;
import com.capgroup.dcip.app.reference.company.CompanyService;
import com.capgroup.dcip.domain.canvas.Canvas;
import com.capgroup.dcip.domain.common.LocalDateTimeRange;
import com.capgroup.dcip.domain.entity.EntityType;
import com.capgroup.dcip.domain.event.Event;
import com.capgroup.dcip.domain.identity.InvestmentUnit;
import com.capgroup.dcip.domain.identity.Profile;
import com.capgroup.dcip.domain.identity.User;

@ContextConfiguration(classes = MappersTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Import({CanvasMapperImpl.class, WorkbenchResourceMapperImpl.class})
public class CanvasMapperTest {

	@Autowired
	private CanvasMapper canvasMapper;
	
	@MockBean
	private CompanyService companyService;

	@Test
	public void toCanvasModelTest() {
		Canvas canvas = createTestCanvas();
		CanvasModel model = canvasMapper.map(canvas);

		assertEquals(canvas.getEntityType().getId(), model.getEntityTypeId());
		assertEquals((long)canvas.getId(), (long)model.getId());
		assertEquals(canvas.getDescription(), model.getDescription());
		assertEquals(canvas.getName(), model.getName());
		assertEquals((long)canvas.getEvent().getProfile().getId(), model.getProfileId());
		assertEquals(canvas.getValidPeriod(), model.getValidPeriod());
	}

	@Test
	public void fromCanvasModelTest() {
		CanvasModel model = createTestCanvasModel();
		Canvas canvas = canvasMapper.map(model);

		// validate that the id/versionId/validPeriod/description are not mapped
		assertNull(canvas.getId());
		assertNull(canvas.getValidPeriod());
		assertNull(canvas.getVersionId());
		assertNull(canvas.getEvent());

		// validate that the name and description are mapped
		assertEquals(model.getDescription(), canvas.getDescription());
		assertEquals(model.getName(), canvas.getName());
	}

	@Test
	public void updateCanvasTest() {
		// assert that only the description and name are unchanged.
		Canvas canvas = createTestCanvas();
		Canvas canvasClone = (Canvas)SerializationUtils.deserialize(SerializationUtils.serialize(canvas));
		
		CanvasModel model = createTestCanvasModel();

		canvasMapper.updateCanvas(model, canvas);

		// validate that the following fields are unchanged
		assertEquals(canvasClone.getEntityType(), canvas.getEntityType());
		assertEquals(canvasClone.getEvent(), canvas.getEvent());
		assertEquals(canvasClone.getId(), canvas.getId());
		assertEquals(canvasClone.getValidPeriod(), canvas.getValidPeriod());
		assertEquals(canvasClone.getVersionId(), canvas.getVersionId());
		
		// validate the name and description are mapped
		assertEquals(model.getDescription(), canvas.getDescription());
		assertEquals(model.getName(), canvas.getName());
		assertNotEquals(canvasClone.getDescription(), canvas.getDescription());
		assertNotEquals(canvasClone.getName(), canvas.getName());

	}

	private Canvas createTestCanvas() {
		EntityType entityType = new EntityType(1, "name", "description");
		Profile profile = new Profile(4, new User("", "", new InvestmentUnit()), null);
		Canvas canvas = new Canvas(2, "canvas name", "canvas description");
		canvas.setEntityType(entityType);
		canvas.setVersionId(UUID.randomUUID());
		canvas.setVersionNo(10L);
		canvas.setValidPeriod(new LocalDateTimeRange(LocalDateTime.of(1970,  1, 2, 3, 4), LocalDateTime.of(1945, 2,4,5,6)));
		new Event(UUID.randomUUID(), "CREATE_CANVAS", profile, "stmf", 1l, UUID.randomUUID(), Collections.singleton(canvas).stream());

		return canvas;
	}

	private CanvasModel createTestCanvasModel() {
		CanvasModel result = new CanvasModel("model name", "model description", null, null);
		result.setEntityTypeId(10);
		result.setId(22l);
		result.setProfileId(45);
		result.setValidPeriod(
				new LocalDateTimeRange(LocalDateTime.of(2010, 1, 1, 12, 0, 0), LocalDateTime.of(2020, 12, 31, 12, 1, 12)));
		return result;
	}
}
