package com.capgroup.dcip.app.reference.company;

import lombok.Data;

@Data
public class CompanyModel {

    private long id;
    private String name;
    private String shortName;
    private CompanyType companyType;
    private boolean isError;

    public static CompanyModel CreateError(long id) {
        CompanyModel result = new CompanyModel();
        result.id = id;
        result.isError = true;
        result.name = "#ERROR#";
        result.shortName = "#ERROR#";
        return result;
    }

    public static CompanyModel CreateUnknown(long id) {
        CompanyModel result = new CompanyModel();
        result.id = id;
        result.isError = true;
        result.name = "#UNKNOWN#";
        result.shortName = "#UNKNOWN#";
        return result;
    }
}
