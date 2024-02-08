package com.gill.api.model;

import java.io.Serializable;

public class RoleRelationship implements Serializable {
    private String roleId;

    private String childId;

    private static final long serialVersionUID = 1L;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId == null ? null : roleId.trim();
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId == null ? null : childId.trim();
    }
}