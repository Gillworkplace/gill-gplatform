package com.gill.user.domain;

import com.gill.api.model.Permission;
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

    private List<Permission> permissions = Collections.emptyList();

    private List<Relation> relations = Collections.emptyList();
}
