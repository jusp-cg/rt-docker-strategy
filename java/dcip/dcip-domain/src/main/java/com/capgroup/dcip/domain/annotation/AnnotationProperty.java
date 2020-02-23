package com.capgroup.dcip.domain.annotation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.capgroup.dcip.domain.common.Property;
import com.capgroup.dcip.domain.common.PropertyType;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.util.ConverterUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a key/value pair property of an annotation
 */
@Entity(name = "annotation_property_view")
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call annotation_property_insert(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call annotation_property_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call annotation_property_delete(?, ?)}")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AnnotationProperty extends TemporalEntity implements Property {
	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = -1494571644885897834L;
	
	@ManyToOne
	@JoinColumn(name = "annotation_property_type_id", updatable = false, insertable = false)
	AnnotationPropertyType annotationPropertyType;

	@Column(name = "annotation_property_type_id")
	long annotationPropertyTypeId;

	@Column
	String value;

	@ManyToOne
	@JoinColumn(name = "annotation_id")
	Annotation annotation;

	public AnnotationProperty(AnnotationPropertyType propertyType, Annotation annotation) {
		this(propertyType, annotation, null);
	}

	public AnnotationProperty(Annotation annotation, long annotationPropertyTypeId) {
		this(annotation);
		this.annotationPropertyTypeId = annotationPropertyTypeId;
	}

	public AnnotationProperty(Annotation annotation) {
		this.annotation = annotation;
	}

	public AnnotationProperty(Annotation annotation, String value) {
		this(annotation);
		this.value = value;
	}

	public AnnotationProperty(AnnotationPropertyType propertyType, Annotation annotation, String value) {
		this(annotation, value);
		this.annotationPropertyType = propertyType;
	}

	@Override
	public <T> T getValueAs(Class<T> type) {
		return ConverterUtils.convertTo(value, type);
	};

	@Override
	public  void setValueAs(Object obj) {
		value = ConverterUtils.convertTo(obj, String.class);
	}

    @Override
    public PropertyType getPropertyType() {
        return annotationPropertyType.getPropertyType();
    }
}
