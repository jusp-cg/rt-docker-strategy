package com.capgroup.dcip.domain.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import com.capgroup.dcip.domain.entity.Entity;
import com.capgroup.dcip.domain.entity.TemporalEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * All entities can be Annotated - Annotation is adding additional properties to an Entity
 */
@javax.persistence.Entity(name = "annotation_view")
@Data
@EqualsAndHashCode(callSuper = true)
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call annotation_insert(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call annotation_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call annotation_delete(?, ?)}")
public class Annotation extends TemporalEntity {

	/**
	 * Generated serial version no.
	 */
	private static final long serialVersionUID = -2292623256471031008L;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id", insertable = false, updatable = false)
	Entity entity;

	@Column
	long entityId;

	@Column
	String text;

	@OneToMany(mappedBy = "annotation", cascade=CascadeType.ALL)
	List<AnnotationProperty> properties;

	@ManyToOne
	@JoinColumn(name = "annotation_type_id", insertable = false, updatable = false)
	AnnotationType annotationType;

	@Column(name="annotation_type_id")
	long annotationTypeId;

	public Annotation() {
		properties = new ArrayList<AnnotationProperty>();
	}

	public Optional<AnnotationProperty> getProperty(long annotationPropertyTypeId) {
		return properties.stream().filter(x -> x.getAnnotationPropertyTypeId() == annotationPropertyTypeId).findFirst();
	}

	public Optional<AnnotationProperty> getProperty(AnnotationPropertyType propertyType) {
		return properties.stream().filter(x -> x.getPropertyType().equals(propertyType)).findFirst();
	}

	public void setProperty(long propertyTypeId, Object obj) {
		setOrAddProperty(propertyTypeId, obj);
	}

	public <T> Optional<T> getProperty(AnnotationPropertyType propertyType, Class<T> objectType) {
		return getProperty(propertyType).map(x -> x.getValueAs(objectType));
	}

	public <T> Optional<T> getProperty(long annotationPropertyTypeId, Class<T> objectType) {
		return getProperty(annotationPropertyTypeId).map(x -> x.getValueAs(objectType));
	}

	protected void setOrAddProperty(long annotationPropertyTypeId, Object object) {
		AnnotationProperty result = getProperty(annotationPropertyTypeId).orElseGet(() -> {
			AnnotationProperty property = new AnnotationProperty(this, annotationPropertyTypeId);
			properties.add(property);
			return property;
		});
		result.setValueAs(object);
	}
}
