package com.capgroup.dcip.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.capgroup.dcip.util.ConverterUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.capgroup.dcip.domain.canvas.Canvas;
import com.capgroup.dcip.infrastructure.repository.CanvasRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = InfrastructureTestConfig.class, loader = AnnotationConfigContextLoader.class)
@AutoConfigureTestDatabase(replace = Replace.AUTO_CONFIGURED)
@Import({ DefaultFormattingConversionService.class, ConverterUtils.class })
public class CanvasRepositoryIT extends AbstractTemporalEntityRepository<Canvas> {

	@Autowired
	private CanvasRepository canvasRepository;

	@Autowired
	TestEntityManager entityManager;

	public CanvasRepositoryIT() {
		super(Canvas.class);
	}

	@Test
	public void saveTest() throws Exception {
		
		// create the canvas
		Canvas canvas = new Canvas("CanvasName", "Canvas Description");

		// set the event/profile/etc.
		preSave("Create Canvas", 4L, canvas);

		canvasRepository.save(canvas);

		// ensure it is written to the DB
		entityManager.flush();

		entityManager.clear();
		
		assertThat(canvas.getId()).isNotEqualTo(0l);

		assertThat(canvasRepository.findById(canvas.getId()).get()).isEqualTo(canvas);
	}
}
