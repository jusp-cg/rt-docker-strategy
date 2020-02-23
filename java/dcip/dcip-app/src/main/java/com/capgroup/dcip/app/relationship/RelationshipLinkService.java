package com.capgroup.dcip.app.relationship;

import com.capgroup.dcip.app.common.LinkModel;

import java.util.List;
import java.util.Map;

/**
 * Service for retrieving Relationships as Links
 */
public interface RelationshipLinkService {
	Iterable<LinkModel<Long>> findLinks(long consumerId);

	Map<Long, List<LinkModel<Long>>> findAllLinks(List<Long> consumerIds);
}
