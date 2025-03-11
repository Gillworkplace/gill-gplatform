package com.gill.oss.service;

import java.util.List;

/**
 * IOssService
 *
 * @author zhangzhiyan
 * @since 2025-03-11
 */
public interface IOssService {

    /**
     * 获取随机头像列表
     *
     * @return 头像列表
     */
    List<String> getRandomAvatarList();
}
