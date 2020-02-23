package com.capgroup.dcip.app.relationship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CanvasRelationModel {
    Map<Long, List<Long>> relations;
}