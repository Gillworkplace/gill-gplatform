package com.gill.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * RegisterParam
 *
 * @author gill
 * @version 2024/02/06
 **/
@Getter
@Setter
public class AdminRegisterParam extends RegisterParam {

    @NotNull(message = "角色不为空")
    private String role;
}
