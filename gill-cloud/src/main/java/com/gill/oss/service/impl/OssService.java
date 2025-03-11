package com.gill.oss.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.gill.oss.config.OssProperty;
import com.gill.oss.service.IOssService;
import java.util.Collections;
import java.util.List;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * OssService
 *
 * @author gill
 * @version 2024/03/27
 **/
@Component
@DubboService(interfaceClass = com.gill.api.service.oss.IOssService.class)
public class OssService implements IOssService, com.gill.api.service.oss.IOssService {

    @Autowired
    private OSS ossClient;

    @Autowired
    private OssProperty ossProperty;

    /**
     * 获取默认头像列表
     *
     * @return 头像列表
     */
    @Override
    public List<String> getDefaultAvatarList() {
        ObjectListing avatar = ossClient.listObjects(
            new ListObjectsRequest(ossProperty.getBucket()).withPrefix(
                ossProperty.getPublicResourcePath() + "/avatar").withMaxKeys(300));
        List<OSSObjectSummary> objs = avatar.getObjectSummaries();
        return objs.stream()
            .map(OSSObjectSummary::getKey)
            .map(key -> key.replace(ossProperty.getPublicResourcePath(), ""))
            .filter(StrUtil::isNotBlank)
            .toList();
    }

    /**
     * 获取默认头像列表
     *
     * @return 头像列表
     */
    @Override
    public List<String> getRandomAvatarList() {
        ObjectListing avatar = ossClient.listObjects(
            new ListObjectsRequest(ossProperty.getBucket()).withPrefix(
                ossProperty.getPublicResourcePath() + "/avatar").withMaxKeys(300));
        List<OSSObjectSummary> objs = avatar.getObjectSummaries();
        Collections.shuffle(objs);
        return objs.stream()
            .map(OSSObjectSummary::getKey)
            .map(key -> key.replace(ossProperty.getPublicResourcePath(), ""))
            .filter(StrUtil::isNotBlank)
            .limit(25)
            .toList();
    }
}
