package com.capgroup.dcip.app.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkModel<K> {
	private String rel;
	private long typeId;
	private K id;
	private String ref;
}
