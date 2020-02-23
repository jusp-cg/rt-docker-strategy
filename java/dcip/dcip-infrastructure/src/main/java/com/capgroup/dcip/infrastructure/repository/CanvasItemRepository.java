package com.capgroup.dcip.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.capgroup.dcip.domain.canvas.CanvasItem;

@Repository
public interface CanvasItemRepository extends TemporalEntityRepository<CanvasItem>{
	Optional<CanvasItem> findByEntityId(long entityId);
}
