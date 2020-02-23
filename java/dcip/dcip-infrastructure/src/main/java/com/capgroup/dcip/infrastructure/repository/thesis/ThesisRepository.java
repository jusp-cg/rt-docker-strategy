package com.capgroup.dcip.infrastructure.repository.thesis;

import java.util.Optional;

import com.capgroup.dcip.infrastructure.repository.TemporalEntityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.capgroup.dcip.domain.thesis.Thesis;

@Repository
public interface ThesisRepository extends TemporalEntityRepository<Thesis> {
	// when upgrade to hibernate 5.1 the below can be cleaned up 
	@Query("select u from Thesis u, CanvasItem ci where u.id = ci.entityId and ci.canvas.id = :canvasId")
	Optional<Thesis> findByCanvasId(@Param("canvasId") long canvasId);
}
