package com.capgroup.dcip.app.relationship;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.domain.relationship.Relationship;
import com.capgroup.dcip.domain.relationship.RelationshipType;
import com.capgroup.dcip.infrastructure.repository.RelationshipRepository;
import com.capgroup.dcip.infrastructure.repository.RelationshipTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@Service
public class RelationshipServiceImpl implements RelationshipService {

    private RelationshipRepository repository;
    private RelationshipTypeRepository relationshipTypeRepository;
    private RelationshipMapper mapper;
    private EntityManager entityManager;

    @Autowired
    public RelationshipServiceImpl(RelationshipRepository repository, RelationshipTypeRepository relationshipTypeRepository,
                                   RelationshipMapper mapper, EntityManager entityManager) {
        this.repository = repository;
        this.relationshipTypeRepository = relationshipTypeRepository;
        this.mapper = mapper;
        this.entityManager = entityManager;
    }

    @Transactional
    @Override
    public RelationshipModel createRelation(RelationshipModel relationshipModel) {
        Relationship relationship = mapper.map(relationshipModel);
        Relationship result = repository.save(relationship);
        entityManager.flush();

        return mapper.map(result);
    }

    @Transactional
    @Override
    public void removeRelation(RelationshipModel relationshipModel) {
        Optional<Relationship> optionalRelationship = repository.findById(relationshipModel.getId());
        if (optionalRelationship.isPresent()) {
            repository.delete(optionalRelationship.get());
            entityManager.flush();
        }
    }

    @Override
    public void removeRelationByProducerId(Long producerId) {
        repository.findRelationsByProducerIdAndStatus(producerId, TemporalEntity.Status.ACTIVE).forEach(relationship -> {
            repository.delete(relationship);
        });
    }

    public Iterable<RelationshipModel> findRelationsByConsumerId(Long id) {
        return mapper.map(repository.findRelationsByConsumerIdAndStatus(id, TemporalEntity.Status.ACTIVE));
    }

    public RelationshipType findRelationshipTypeByRoleType(Long roleEntityType1, Long roleEntityType2) {
        return relationshipTypeRepository.findRelationshipTypeByRoleType(roleEntityType1, roleEntityType2);
    }
}
