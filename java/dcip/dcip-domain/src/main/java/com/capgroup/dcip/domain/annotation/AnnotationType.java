package com.capgroup.dcip.domain.annotation;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Meta data for an annotation
 */
@Entity
public class AnnotationType {
    @Id
    @Column
    long id;

    @Column
    String name;

    @Column
    String description;

    @OneToMany(mappedBy = "annotationType")
    List<AnnotationPropertyType> propertyTypes;
}
