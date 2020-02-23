package com.capgroup.dcip.infrastructure;

import com.capgroup.dcip.domain.common.LocalDateTimeRange;
import com.capgroup.dcip.domain.entity.EntityType;
import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.domain.models.Model;
import com.capgroup.dcip.infrastructure.repository.ModelRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.math.BigInteger;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = InfrastructureTestConfig.class, loader = AnnotationConfigContextLoader.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
@Import(ModelRepository.class)
public class ModelRepositoryIT extends AbstractTemporalEntityRepository<Model> {
    @Autowired
    private ModelRepository modelRepository;

    public ModelRepositoryIT() {
        super(Model.class);
    }

    @Test
    public void testSave() {
        Model testEntity = performTestSaves("TestModel", "TestDesc");
        testEntity.setName("TestNewModel");
        Model entity = modelRepository.save(testEntity);
        assertEquals("TestNewModel", entity.getName());
        assertEquals(TemporalEntity.Status.ACTIVE, entity.getStatus());
    }

    @Test
    public void findOneById() {
        Model testEntity = performTestSaves("FindingModel", "ModelDesc");
        Optional<Model> entity = modelRepository.findById(testEntity.getId());
        assertTrue(entity.isPresent());
        Model item = entity.get();
        assertEquals(testEntity.getId(), item.getId());
        assertEquals(testEntity.getName(), item.getName());
        assertEquals(testEntity.getDescription(), item.getDescription());
    }

    @Test
    public void deleteOne() {
        Model testEntity = performTestSaves("DeleteModel", "ModelDel");
        modelRepository.delete(testEntity);
        Optional<Model> entity = modelRepository.findById(testEntity.getId());
        assertFalse(entity.isPresent());
    }

    @Test
    public void getByIds() {
        Model firstEntity = performTestSaves("GetByIdsModel", "ModelDel");
        Model secEntity = performTestSaves("SecModel", "SecDel");
        Model thirdEntity = performTestSaves("ThirdModel", "ThirdDel");

        List<Long> entityIds = new ArrayList<>();
        entityIds.add(firstEntity.getId());
        entityIds.add(secEntity.getId());
        entityIds.add(thirdEntity.getId());
        Iterable<Model> modelList = this.modelRepository.findByIdIn(entityIds);
        List entities = new ArrayList<Model>();
        modelList.forEach(x -> entities.add(x));
        assertEquals(3, entities.size());
    }

    private Model performTestSaves(String modelName, String modelDescription) {
        Model entity = new Model();
        entity.setName(modelName);
        entity.setDescription(modelDescription);
        entity.setStatus(TemporalEntity.Status.ACTIVE);
        EntityType testEntity = new EntityType(5, "test", "desc");
        entity.setEntityType(testEntity);
        entity.setVersionId(UUID.randomUUID());
        entity.setVersionNo(1L);
        entity.setValidPeriod(new LocalDateTimeRange());
        return modelRepository.save(entity);
    }
}
