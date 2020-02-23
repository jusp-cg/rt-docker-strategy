package com.capgroup.dcip.app.data;

import com.capgroup.dcip.domain.common.Schedule;
import lombok.Data;

import java.util.List;

/**
 * Data transfer object for the domain type 'Series' 
 */
@Data
public class SeriesModel {
	private long id;
	private String name;
	private String description;
	private List<Schedule> schedules;
}
