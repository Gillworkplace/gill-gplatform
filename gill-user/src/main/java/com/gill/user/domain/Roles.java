package com.gill.user.domain;

import com.gill.api.model.Role;
import com.gill.user.entity.RoleEntity;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Roles
 *
 * @author gill
 * @version 2024/02/08
 **/
@Getter
@Setter
public class Roles {

    private List<RoleEntity> roles = Collections.emptyList();

    private List<Relation> roleRelations = Collections.emptyList();

    private List<Relation> rolePermissions = Collections.emptyList();
}
