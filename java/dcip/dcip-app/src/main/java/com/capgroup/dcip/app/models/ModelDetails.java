package com.capgroup.dcip.app.models;

import com.capgroup.dcip.app.entity.TemporalEntityModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class ModelDetails extends TemporalEntityModel {
    private String name;
    private String description;
    private Long canvasId;
}