package com.capgroup.dcip.app.identity;

import com.capgroup.dcip.app.common.LinkModel;
import com.capgroup.dcip.app.entity.TemporalEntityModel;
import lombok.Data;

import java.util.List;

@Data
public class UserModel extends TemporalEntityModel {
    private List<LinkModel<Long>> profiles;
    private LinkModel<Long> investmentUnit;
}
