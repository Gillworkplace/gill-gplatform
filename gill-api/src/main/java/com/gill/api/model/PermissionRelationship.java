package com.gill.api.model;

import java.io.Serializable;

public class PermissionRelationship implements Serializable {
    private String ancestorId;

    private String descendantId;

    private Integer adjoin;

    private static final long serialVersionUID = 1L;

    public String getAncestorId() {
        return ancestorId;
    }

    public void setAncestorId(String ancestorId) {
        this.ancestorId = ancestorId == null ? null : ancestorId.trim();
    }

    public String getDescendantId() {
        return descendantId;
    }

    public void setDescendantId(String descendantId) {
        this.descendantId = descendantId == null ? null : descendantId.trim();
    }

    public Integer getAdjoin() {
        return adjoin;
    }

    public void setAdjoin(Integer adjoin) {
        this.adjoin = adjoin;
    }
}