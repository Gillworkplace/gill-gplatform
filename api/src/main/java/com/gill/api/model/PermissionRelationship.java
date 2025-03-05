package com.gill.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionRelationship {

    private String ancestorId;

    private String descendantId;

    private Integer adjoin;
}