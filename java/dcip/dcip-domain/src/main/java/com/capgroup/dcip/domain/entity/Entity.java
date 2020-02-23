package com.capgroup.dcip.domain.entity;

import com.capgroup.dcip.domain.event.Event;
import com.capgroup.dcip.domain.event.EventParticipant;
import com.capgroup.dcip.domain.identity.Identifiable;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Represents an object that can be persisted in the database. This class
 * provides the following features:
 * <li>1</li> Identity - a common id across all entities in the system
 * <li>2</li> Equality -
 * <li>3</li> Event - access to the event (and the profile/user that created the
 * event)
 * <li>4</li> Meta-data - EntityType
 * <li>5</li> Optimistic concurrency checking through the property 'versionNo'
 */
@javax.persistence.Entity
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@FilterDef(name = "disaggregationVirtualView", parameters = @ParamDef(name = "investmentUnitId", type = "long"),
        defaultCondition = "EXISTS(SELECT 1 FROM disaggregation_entity_view dev WHERE dev.entity_Id = id and dev" +
                ".investment_unit_id = :investmentUnitId and dev.version_id = version_id)")
//@FilterDef(name = "disaggregationVirtualView", parameters = @ParamDef(name = "investmentUnitId", type = "long"),
//        defaultCondition = "EXISTS(SELECT 1)")
@FilterDef(name = "latest", defaultCondition = " valid_end_date = max_end_date()")
@FilterDef(name = "applicationRoleVirtualView",
        parameters = @ParamDef(name = "applicationRole", type = "long"),
        defaultCondition = " ((SELECT et.testRoleFlag FROM dbo.EntityType et where et.id = entityTypeId) = 0 OR " +
                "COALESCE((SELECT p.ApplicationRoleId FROM dbo.ProfileView p JOIN dbo.Event e on e.profileId = p.id WHERE e" +
                ".id = eventId)," +
                ":applicationRole) = :applicationRole)")
@Filter(name = "disaggregationVirtualView")
//@Filter(name = "applicationRoleVirtualView")
public abstract class Entity implements EventParticipant, Serializable, Identifiable<Long> {

    /**
     * Generated Serial Version Id
     */
    private static final long serialVersionUID = -5682053660161552422L;

    @Id
    @Setter
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Entity_Sequence")
    @GenericGenerator(name = "Entity_Sequence", strategy = "enhanced-sequence", parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "entity_sequence"),
            @org.hibernate.annotations.Parameter(name = "optimizer", value = "pooled-lo"),
            @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "25")})
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_type_id")
    @Setter
    private EntityType entityType;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @Setter
    private Event event;

    @Version
    @Column(nullable = false)
    @Setter
    private Long versionNo;

    @Transient
    private Integer hash;

    protected Entity(long id) {
        this.id = id;
    }

    protected Entity(long id, EntityType entityType, Event event, long versionNo) {
        this(id);
        this.entityType = entityType;
        this.event = event;
        this.versionNo = versionNo;
    }

    @Override
    public int hashCode() {
        // the id can change as an entity is made persistence - but the
        // hashcode should never change or collections will break
        if (hash != null)
            return hash;

        // if the common hash has not been set then choose a constant hashcode else use the
        // id
        hash = id == null ? 32 : Long.hashCode(id);

        return hash;
    }

    @Override
    public boolean equals(Object rhs) {
        if (this == rhs)
            return true;
        if (rhs == null || id == null || !getClass().isInstance(rhs))
            return false;

        Entity other = (Entity) rhs;

        return id.equals(other.id);
    }
}
