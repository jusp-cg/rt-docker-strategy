package com.capgroup.dcip.app.thesis.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThesisTreeModel {
	private ThesisPointModel thesisPoint;
	private List<ThesisTreeModel> children;
}
