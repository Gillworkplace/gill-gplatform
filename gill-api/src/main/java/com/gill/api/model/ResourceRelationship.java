package com.gill.api.model;

import java.io.Serializable;

public class ResourceRelationship implements Serializable {
    private Integer ancestorId;

    private Integer descendantId;

    private Integer distance;

    private static final long serialVersionUID = 1L;

    public Integer getAncestorId() {
        return ancestorId;
    }

    public void setAncestorId(Integer ancestorId) {
        this.ancestorId = ancestorId;
    }

    public Integer getDescendantId() {
        return descendantId;
    }

    public void setDescendantId(Integer descendantId) {
        this.descendantId = descendantId;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }
}