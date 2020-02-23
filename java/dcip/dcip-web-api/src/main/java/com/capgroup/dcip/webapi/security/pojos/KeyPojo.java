package com.capgroup.dcip.webapi.security.pojos;

import lombok.Data;

@Data
public class KeyPojo {
    private String e;
    private String kid;
    private String n;
}
