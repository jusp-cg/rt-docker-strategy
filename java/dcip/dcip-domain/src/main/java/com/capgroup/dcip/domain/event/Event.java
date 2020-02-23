package com.capgroup.dcip.domain.event;

import com.capgroup.dcip.domain.identity.InvestmentUnit;
import com.capgroup.dcip.domain.identity.Profile;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.stream.Stream;

/***
 * Records an event that modified data. The event includes the following pieces
 * of information:
 * <li>1</li> The user who created the event ({@link Event#createdBy})
 * <li>2</li> When the event was created ({@link #createdBy})
 * <li>3</li> Whose profile the event was created for ({@link #profile})
 */
@javax.persistence.Entity
@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Immutable
public class Event implements Auditable, Serializable {
    /**
     * Generated Serial Version Id
     */
    private static final long serialVersionUID = 6048575575635594824L;

    /**
     * Which datasource updated the data
     */
    Long dataSourceId;

    /**
     * Primary key
     */
    @Id
    // @Type(type = "uuid-char")
    private UUID id;

    /**
     * The profile of the user who created the event
     */
    @JoinColumn(name = "ProfileId")
    @ManyToOne(cascade = CascadeType.PERSIST)
    @Setter
    private Profile profile;

    /**
     * The timestamp of the event
     */
    @Column(nullable = false)
    private LocalDateTime createdTimestamp = LocalDateTime.now(ZoneOffset.UTC);

    /**
     * The user who created the event
     */
    @Column(nullable = false)
    @CreatedBy
    private String createdBy;

    @Column
    private UUID correlationId;

    /**
     * The type of the event
     */
    @Column(nullable = false)
    private String eventType = "Undefined";

    /**
     * The application that created the event
     */
    @Column(nullable = false)
    private String createdByProgramName = "DCIP";

    /**
     * The investment unit associated with the profile->user at the point in time
     * that the event was created
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "InvestmentUnitId")
    private InvestmentUnit investmentUnit;

    public Event(UUID id, String eventType, Profile profile, String createdBy,
                 Long dataSourceId, UUID correlationId, Stream<? extends EventParticipant> participants) {
        this(id,eventType, profile, createdBy,
                 dataSourceId, correlationId,  participants,
                LocalDateTime.now(ZoneOffset.UTC));
    }


    public Event(UUID id, String eventType, Profile profile, String createdBy,
                 Long dataSourceId, UUID correlationId, Stream<? extends EventParticipant> participants,
                 LocalDateTime createdTimestamp) {
        this.id = id;
        this.eventType = eventType;
        this.profile = profile;
        participants.forEach(x -> {
            x.setEvent(this);
        });
        this.dataSourceId = dataSourceId;
        this.correlationId = correlationId;
        this.createdBy = createdBy;
        this.investmentUnit = profile.getUser().getInvestmentUnit();
        this.createdTimestamp = createdTimestamp;
    }

    public Event(UUID id, String eventType, Profile profile, String createdBy,
                 Long dataSourceId, UUID correlationId) {
        this(id, eventType, profile, createdBy, dataSourceId, correlationId, Stream.empty(),
                LocalDateTime.now(ZoneOffset.UTC));
    }

    public Event(UUID eventId, Profile profile) {
        this(eventId, null, profile, null, null, null);
    }
}
