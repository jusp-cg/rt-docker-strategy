package com.capgroup.dcip.domain.annotation;

import com.capgroup.dcip.domain.common.PropertyType;
import lombok.Data;

import javax.persistence.*;

/**
 * Meta data for an annotation property
 */
@Entity
@Data
public class AnnotationPropertyType {
    @Id
    @Column(name = "id")
    long id;

    @ManyToOne
    @JoinColumn(name = "property_type_id")
    PropertyType propertyType;

    @ManyToOne
    @JoinColumn(name = "annotation_type_id")
    AnnotationType annotationType;
}
