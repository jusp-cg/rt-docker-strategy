package com.capgroup.dcip.infrastructure;

import com.capgroup.dcip.domain.canvas.Canvas;
import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.domain.relationship.Relationship;
import com.capgroup.dcip.domain.relationship.RelationshipType;
import com.capgroup.dcip.infrastructure.repository.CanvasRepository;
import com.capgroup.dcip.infrastructure.repository.RelationshipRepository;
import com.capgroup.dcip.infrastructure.repository.RelationshipTypeRepository;
import com.capgroup.dcip.util.ConverterUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = InfrastructureTestConfig.class, loader = AnnotationConfigContextLoader.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
@Import({DefaultFormattingConversionService.class, ConverterUtils.class})
public class RelationshipRepositoryIT extends AbstractTemporalEntityRepository<Relationship> {

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private RelationshipTypeRepository relationshipTypeRepository;

    @Autowired
    private CanvasRepository canvasRepository;

    private Canvas canvas;

    private static final Long chartId = 618721804L;

    @Autowired
    TestEntityManager entityManager;

    public RelationshipRepositoryIT() {
        super(Relationship.class);
    }

    @Before
    public void prepareData() throws Exception {
        // create the canvas
        canvas = new Canvas("CanvasName", "Canvas Description");

        // set the event/profile/etc.
        preSave("Create Canvas", 4L, canvas);

        canvasRepository.save(canvas);

        // ensure it is written to the DB
        entityManager.flush();

        entityManager.clear();

        assertThat(canvas.getId()).isNotEqualTo(0L);
    }

    @Test
    public void linkCanvasWithChart() throws Exception {
        RelationshipType relationshipType = relationshipTypeRepository.findRelationshipTypeByRoleType(3L, 4L);

        assertNotNull("Relationship Type should be initialized", relationshipType);

        Relationship relationship = new Relationship(canvas.getId(), chartId, relationshipType);

        preSave("Create Relationship", 21L, relationship);

        Relationship savedRelationship = relationshipRepository.save(relationship);


        entityManager.flush();

        assertNotNull("Relationship should be initialized", savedRelationship);

        assertEquals("Consumer Id should be equal", savedRelationship.getEntityId1(), canvas.getId());

        assertEquals("Producer Id should be equal", savedRelationship.getEntityId2(), chartId);

        assertEquals("Relation type should be equal", savedRelationship.getRelationshipType().getId(),
                relationshipType.getId());

        Iterable<Relationship> relationshipList = relationshipRepository.findRelationsByConsumerIdAndStatus(canvas.getId(), TemporalEntity.Status.ACTIVE);

        assertNotNull("Relationship collection should be initialized", relationshipList);

        entityManager.clear();
    }
}
