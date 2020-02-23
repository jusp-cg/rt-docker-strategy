package com.capgroup.dcip.app.thesis.model;

import com.capgroup.dcip.app.alert.service.AlertSubscriptionService;
import com.capgroup.dcip.app.common.LinkModel;
import com.capgroup.dcip.app.entity.TemporalEntityMapper;
import com.capgroup.dcip.app.relationship.RelationshipLinkService;
import com.capgroup.dcip.domain.thesis.Thesis;
import com.capgroup.dcip.domain.thesis.Thesis.ThesisFilter;
import com.capgroup.dcip.domain.thesis.ThesisEdge;
import com.capgroup.dcip.domain.thesis.ThesisPoint;
import com.capgroup.dcip.util.CollectionUtils;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Mapping between Thesis/ThesisModel, Thesis/ThesisTreeModel,
 * ThesisPoint/ThesisPointModel and ThesisEdge/ThesisEdgeModel
 */
@Mapper(config = TemporalEntityMapper.class)
public abstract class ThesisMapper {

    @Autowired
    private RelationshipLinkService relationshipLinkService;
    @Autowired
    private AlertSubscriptionService alertSubscriptionService;

    public ThesisModel map(Thesis thesis, long canvasId) {
        ThesisModel result = map(thesis);
        result.setCanvasId(canvasId);
        return result;
    }

    protected abstract ThesisModel map(Thesis thesis);

    public Iterable<ThesisTreeModel> mapTree(Thesis thesis) {
        return map(thesis, (ThesisPoint) null);
    }

    /**
     * Maps the thesis points (including the links)
     */
    public ThesisPointModel map(ThesisPoint point) {
        return map(point, true);
    }

    /**
     * Used to control whether the links are mapped
     */
    public abstract ThesisPointModel map(ThesisPoint point, @Context Boolean mapLinks);

    /**
     * Add the relationships and alerts as content to a ThesisPointModel
     */
    @AfterMapping
    protected void map(ThesisPoint point, @MappingTarget ThesisPointModel result, @Context Boolean mapLinks) {
        if (mapLinks == null || mapLinks) {
            List<LinkModel<Long>> links =
                    CollectionUtils.asList(alertSubscriptionService.findAllAsLinks(null, point.getId()));
            links.addAll(CollectionUtils.asList(relationshipLinkService.findLinks(point.getId())));
            result.setContent(links);
        }
    }

    /**
     * Optimised mapping of collections - maps all the links at once rather than one at a time
     */
    public Iterable<ThesisPointModel> mapThesisPoints(Stream<ThesisPoint> thesisPoints) {
        List<ThesisPointModel> models = StreamSupport.stream(thesisPoints.spliterator(), false).map(x -> map(x,
                false)).collect(Collectors.toList());

        List<Long> thesisPointIds = models.stream().map(x -> x.getId()).collect(Collectors.toList());

        // get the subscripions
        Map<Long, List<LinkModel<Long>>> subscriptionLinks =
                alertSubscriptionService.findAllAsLinks(thesisPointIds);

        // get the relationships
        Map<Long, List<LinkModel<Long>>> relationshipLinks = relationshipLinkService.findAllLinks(thesisPointIds);

        // join the subscriptions/relationships collections
        Map<Long, List<LinkModel<Long>>> allLinks =
                Stream.concat(subscriptionLinks.entrySet().stream(),
                        relationshipLinks.entrySet().stream()).collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.reducing(new ArrayList<>(),
                                x -> x.getValue(),
                                (l, r) -> Stream.concat(l.stream(), r.stream()).collect(Collectors.toList()))));

        // set the content on the models
        allLinks.entrySet().forEach(entry->{
            models.stream().filter(x->x.getId().equals(entry.getKey())).findFirst().ifPresent(model->{
                model.setContent(entry.getValue());
            });
        });

        return models;
    }

    /**
     * Optimised mapping of collections - maps all the links at once rather than one at a time
     */
    public Iterable<ThesisPointModel> mapThesisPoints(Iterable<ThesisPoint> thesisPoints) {
        return mapThesisPoints(StreamSupport.stream(thesisPoints.spliterator(), false));
    }

    @Mapping(target = "thesisId", source = "thesis.id")
    public abstract ThesisEdgeModel map(ThesisEdge thesisEdge);

    protected Iterable<ThesisTreeModel> map(Thesis thesis, ThesisPoint parent) {
        return map(thesis,
                thesis.thesisEdges(parent, EnumSet.of(ThesisFilter.PARENT)).map(x -> x.getChildThesisPoint()));
    }

    protected List<ThesisTreeModel> map(Thesis thesis, Stream<ThesisPoint> thesisPoints) {
        return thesisPoints
                .map(x -> new ThesisTreeModel(map(x), map(thesis,
                        thesis.thesisEdges(x, EnumSet.of(ThesisFilter.PARENT)).map(y -> y.getChildThesisPoint()))))
                .collect(Collectors.toList());
    }

    public abstract Iterable<ThesisEdgeModel> mapThesisEdges(Iterable<ThesisEdge> thesisEdges);
}
