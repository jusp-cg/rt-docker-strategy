package com.capgroup.dcip.app.thesis.model;

import com.capgroup.dcip.app.common.LinkModel;
import com.capgroup.dcip.app.entity.TemporalEntityModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * DTO for a ThesisPoint. The 'content' property is the relationships/alerts associated with a ThesisPoint
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ThesisPointModel extends TemporalEntityModel {
    private String text;
    private long originalThesisId;
    private List<LinkModel<Long>> content;
}
