package com.gill.user.mapper;

import com.gill.api.model.UserBan;
import org.apache.ibatis.annotations.Mapper;

/**
 * UserBan
 *
 * @author gill
 * @version 2024/02/07
 **/
@Mapper
public interface UserBanMapper {

    /**
     * 检查当前时间用户是否被禁用
     *
     * @param userId 用户ID
     * @return 被ban信息
     */
    UserBan firstUserBan(int userId);
}
