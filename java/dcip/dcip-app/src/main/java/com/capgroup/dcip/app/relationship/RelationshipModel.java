package com.capgroup.dcip.app.relationship;

import com.capgroup.dcip.app.entity.TemporalEntityModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=true)
public class RelationshipModel extends TemporalEntityModel {

    private Long firstEntityId;

    private Long secondEntityId;

    private Long relationshipTypeId;

    private String name;
}