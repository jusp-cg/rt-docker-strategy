package com.capgroup.dcip.infrastructure.repository.thesis;

import com.capgroup.dcip.domain.common.LocalDateTimeRange;
import com.capgroup.dcip.domain.entity.EntityType;
import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.domain.event.Event;
import com.capgroup.dcip.domain.identity.InvestmentUnit;
import com.capgroup.dcip.domain.identity.Profile;
import com.capgroup.dcip.domain.identity.Role;
import com.capgroup.dcip.domain.identity.User;
import com.capgroup.dcip.domain.thesis.ThesisPoint;
import com.capgroup.dcip.infrastructure.repository.TemporalRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is a repository fragment - adds the findAllVersions implementation of the ThesisPointRepository
 */
@Repository
public class ThesisPointRepositoryImpl implements TemporalRepository<ThesisPoint, Long> {

    JdbcTemplate jdbcTemplate;

    @Autowired
    public ThesisPointRepositoryImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Iterable<ThesisPoint> findAllVersions(Long id, long investmentUnitId) {
        List<ThesisPointProjection> projections = jdbcTemplate.query(
                "select e.version_id as versionId" +
                        ",e.event_id as eventId" +
                        ",iu.id as investmentUnitId" +
                        ",u.id as userId" +
                        ",r.id as roleId" +
                        ",p.id as profileId" +
                        ",e.id as thesisPointId" +
                        ",e.entity_type_id as entityTypeId" +
                        ",e.version_no as versionNo" +
                        ",u.initials" +
                        ",r.name as role" +
                        ",ev.created_timestamp as modifiedTimestamp" +
                        ",e.valid_start_date as validStartDate" +
                        ",e.valid_end_date as validEndDate" +
                        ",e.status" +
                        ",tp.text" +
                        ",tp.original_thesis_id as originalThesisId" +
                        ",iu.name as investmentUnitName" +
                        " from thesis_point tp join entity e " +
                        "on tp.version_id = e.version_id " +
                        "join event ev on e.event_id = ev.id " +
                        "join profile_view p on ev.profile_id = p.id " +
                        "join role_view r on p.role_id = r.id " +
                        "join user_view u on p.user_id = u.id " +
                        "join investment_unit_view iu on u.investment_unit_id = iu.id " +
                        "join disaggregation_entity_view d on d.version_id = e.version_id and d.entity_id = e.id " +
                        "where d.investment_unit_id = ? and e.id = ?",
                new Object[]{investmentUnitId, id},
                new BeanPropertyRowMapper<>(ThesisPointProjection.class));

        MappingContext context = new MappingContext();

        return projections.stream().map(projection -> map(projection, context)).collect(Collectors.toList());
    }

    ThesisPoint map(ThesisPointProjection projection, MappingContext context) {
        ThesisPoint result = new ThesisPoint();
        result.setId(projection.getThesisPointId());
        result.setOriginalThesisId(projection.getOriginalThesisId());
        result.setText(projection.getText());
        result.setVersionId(projection.getVersionId());
        result.setStatus(projection.getStatus());
        result.setValidPeriod(new LocalDateTimeRange(projection.getValidStartDate(), projection.getValidEndDate()));
        result.setEntityType(context.entityTypeMap.computeIfAbsent(projection.getEntityTypeId(),
                k -> new EntityType(k)));
        result.setEvent(findOrCreateEvent(projection, context));
        result.setVersionNo(projection.getVersionNo());

        return result;
    }

    Event findOrCreateEvent(ThesisPointProjection projection, MappingContext context) {
        return context.eventMap.computeIfAbsent(projection.getEventId(), id ->
                new Event(projection.getEventId(),
                        null,
                        findOrCreateProfile(projection, context),
                        null, null, null, Stream.empty(),
                        projection.getModifiedTimestamp())
        );
    }

    Profile findOrCreateProfile(ThesisPointProjection projection, MappingContext context) {
        return context.profileMap.computeIfAbsent(projection.getProfileId(), p ->
                new Profile(projection.getProfileId(), findOrCreateUser(projection, context),
                        findOrCreateRole(projection, context)));
    }

    User findOrCreateUser(ThesisPointProjection projection, MappingContext context) {
        return context.getUserMap().computeIfAbsent(projection.getUserId(), id -> new User(projection.getUserId(),
                projection.getInitials(), null,
                findOrCreateInvestmentUnit(projection, context)));
    }

    Role findOrCreateRole(ThesisPointProjection projection, MappingContext context) {
        return context.getRoleMap().computeIfAbsent(projection.getRoleId(), id -> new Role(projection.getRoleId(),
                projection.getRole(), null));
    }

    InvestmentUnit findOrCreateInvestmentUnit(ThesisPointProjection projection, MappingContext context) {
        return context.getInvestmentUnitMap().computeIfAbsent(projection.getInvestmentUnitId(),
                id -> new InvestmentUnit(id, projection.getInvestmentUnitName()));
    }

    @Data
    static class ThesisPointProjection {
        private UUID versionId;
        private UUID eventId;
        private long investmentUnitId;
        private long userId;
        private long roleId;
        private long profileId;
        private long thesisPointId;
        private long entityTypeId;
        private long versionNo;
        private String initials;
        private String role;
        private LocalDateTime modifiedTimestamp;
        private LocalDateTime validStartDate;
        private LocalDateTime validEndDate;
        private TemporalEntity.Status status;
        private String text;
        private long originalThesisId;
        private String investmentUnitName;
    }

    @Data
    static class MappingContext {
        private Map<Long, EntityType> entityTypeMap = new HashMap<>();
        private Map<UUID, Event> eventMap = new HashMap<>();
        private Map<Long, Profile> profileMap = new HashMap<>();
        private Map<Long, User> userMap = new HashMap<>();
        private Map<Long, Role> roleMap = new HashMap<>();
        private Map<Long, InvestmentUnit> investmentUnitMap = new HashMap<>();
    }
}
