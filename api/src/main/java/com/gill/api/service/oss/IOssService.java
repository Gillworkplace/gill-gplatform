package com.gill.api.service.oss;

import java.util.List;

/**
 * IOssService
 *
 * @author gill
 * @version 2024/03/27
 **/
public interface IOssService {

    /**
     * 获取默认头像列表
     *
     * @return 头像列表
     */
    List<String> getDefaultAvatarList();
}
