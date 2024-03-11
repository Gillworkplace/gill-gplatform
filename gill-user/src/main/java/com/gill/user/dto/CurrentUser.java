package com.gill.user.dto;

import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CurrentUser
 *
 * @author gill
 * @version 2024/03/11
 **/
@Getter
@Setter
@NoArgsConstructor
public class CurrentUser extends UserInfo{

    private Set<String> permissions;

    public CurrentUser(UserInfo user, Set<String> permissions) {
        super(user);
        this.permissions = permissions;
    }
}
