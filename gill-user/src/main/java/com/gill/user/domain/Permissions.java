package com.gill.user.domain;

import com.gill.user.entity.PermissionEntity;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Resources
 *
 * @author gill
 * @version 2024/02/08
 **/
@Getter
@Setter
public class Permissions {

    private List<PermissionEntity> permissions = Collections.emptyList();

    private List<Relation> relations = Collections.emptyList();
}
