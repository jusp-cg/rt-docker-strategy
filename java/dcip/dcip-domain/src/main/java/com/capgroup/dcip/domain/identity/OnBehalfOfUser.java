package com.capgroup.dcip.domain.identity;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Getter
@Table(name = "OnBehalfOfUserView")
public class OnBehalfOfUser extends TemporalEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    User user;
    @ManyToOne(optional = false)
    @JoinColumn(name = "onBehalfOfUserId")
    User onBehalfOfUser;
}
