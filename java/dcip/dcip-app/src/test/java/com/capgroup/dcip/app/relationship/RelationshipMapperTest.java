package com.capgroup.dcip.app.relationship;

import com.capgroup.dcip.app.MappersTestConfig;
import com.capgroup.dcip.domain.relationship.Relationship;
import com.capgroup.dcip.domain.relationship.RelationshipType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration(classes = MappersTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Import(RelationshipMapperImpl.class)
public class RelationshipMapperTest {

    @Autowired
    private RelationshipMapper relationshipMapper;

    private Relationship relationship;

    @Before
    public void prepareData() {
        RelationshipType relationshipType = new RelationshipType();
        relationshipType.setId(-1L);
        relationship = new Relationship(-1L, -1L, relationshipType);
    }

    @Test
    public void mapRelatonshipTest() {
        RelationshipModel model = relationshipMapper.map(relationship);

        assertNotNull("Model should be initialized", model);

        assertEquals("First entity Id should be equal", relationship.getEntityId1(), model.getFirstEntityId());

        assertEquals("Second entity Id should be equal", relationship.getEntityId2(), model.getSecondEntityId());

        assertEquals("Relationship Type Id should be equal", relationship.getRelationshipType().getId(), model.getRelationshipTypeId());
    }

    @Test
    public void mapModelTest() {
        RelationshipModel model = relationshipMapper.map(relationship);

        Relationship domain = relationshipMapper.map(model);

        assertNotNull("Relationship domain should be initialized", domain);

        assertEquals("First entity Id should be equal", model.getFirstEntityId(), domain.getEntityId1());

        assertEquals("Second entity Id should be equal", model.getSecondEntityId(), domain.getEntityId2());

        assertEquals("Relationship Type Id should be equal", model.getRelationshipTypeId(), domain.getRelationshipType().getId());
    }
}
