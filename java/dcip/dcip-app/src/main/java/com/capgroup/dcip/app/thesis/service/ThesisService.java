package com.capgroup.dcip.app.thesis.service;

import com.capgroup.dcip.app.thesis.model.ThesisEdgeModel;
import com.capgroup.dcip.app.thesis.model.ThesisModel;
import com.capgroup.dcip.app.thesis.model.ThesisPointModel;
import com.capgroup.dcip.app.thesis.model.ThesisTreeModel;
import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.domain.thesis.Thesis.ThesisFilter;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

/**
 * Methods for finding/updating/manipulating a Theses
 */
@Service
public interface ThesisService {
    ThesisModel findByCanvasId(long canvasId);

    ThesisModel findById(long id);

    ThesisModel createThesis(ThesisModel model);

    Iterable<ThesisPointModel> findThesisPointsByThesis(long id);

    Iterable<ThesisPointModel> findAllThesisPoints(Long profileId, EnumSet<TemporalEntity.Status> statusFilter);

    Iterable<ThesisTreeModel> findThesisTree(long thesisId);

    void deleteThesis(long id);

    ThesisPointModel findThesisPointByThesis(long thesisId, long thesisPointId);

    ThesisEdgeModel createOrAddThesisPoint(long thesisId, ThesisPointModel thesisPoint);

    ThesisEdgeModel createOrAddThesisPoint(long thesisId, long parentThesisPointId, ThesisPointModel thesisPoint);

    Iterable<ThesisEdgeModel> findThesisEdges(Long thesisId, Long thesisPointId, EnumSet<ThesisFilter> filter);

    ThesisEdgeModel findThesisEdge(long thesisId, long thesisEdgeId);

    ThesisPointModel findThesisPoint(long thesisPointId);

    Iterable<ThesisEdgeModel> deleteThesisEdge(long thesisId, long thesisEdgeId);

    Iterable<ThesisEdgeModel> deleteThesisPoint(long thesisId, long childThesisPointId);

    ThesisPointModel updateThesisPoint(long thesisId, long thesisPointId, ThesisPointModel thesisPoint);

    Iterable<ThesisEdgeModel> deleteThesisEdge(long thesisId, long childThesisPointId, Long parentThesisPointId);

    Iterable<ThesisPointModel> findVersionsOfThesisPoint(long id);
}
