package com.capgroup.dcip.app.relationship;

import com.capgroup.dcip.app.common.LinkModel;
import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.domain.relationship.Relationship;
import com.capgroup.dcip.infrastructure.repository.RelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RelationshipLinkServiceImpl implements RelationshipLinkService {

    private RelationshipRepository relationshipRepository;
    private RelationshipLinkMapper relationshipLinkMapper;

    @Autowired
    public RelationshipLinkServiceImpl(RelationshipRepository repository,
                                       RelationshipLinkMapper relationshipLinkMapper) {
        this.relationshipRepository = repository;
        this.relationshipLinkMapper = relationshipLinkMapper;
    }

    @Override
    public Iterable<LinkModel<Long>> findLinks(long consumerId) {
        return relationshipLinkMapper.map(relationshipRepository.findRelationsByConsumerIdAndStatus(consumerId,
                TemporalEntity.Status.ACTIVE));
    }

    @Override
    public Map<Long, List<LinkModel<Long>>> findAllLinks(List<Long> consumerIds) {
        Iterable<Relationship> relationships =
                relationshipRepository.findAllRelationsByConsumerIdAndStatus(TemporalEntity.Status.ACTIVE,
                        consumerIds == null || consumerIds.isEmpty() ? null : consumerIds);

        return StreamSupport.stream(relationships.spliterator(), false).collect(Collectors.groupingBy(Relationship::getEntityId1,
                Collectors.mapping(relationshipLinkMapper::map, Collectors.toList())));
    }
}
