package com.gill.oss.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.gill.api.service.oss.IOssService;
import com.gill.oss.config.OssProperty;
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
@DubboService
public class OssService implements IOssService {

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
        ObjectListing avatar = ossClient.listObjects(ossProperty.getBucket(),
            ossProperty.getPublicResourcePath() + "/avatar");
        List<OSSObjectSummary> objs = avatar.getObjectSummaries();
        return objs.stream()
            .map(OSSObjectSummary::getKey)
            .map(key -> key.replace(ossProperty.getPublicResourcePath() + "/", ""))
            .toList();
    }
}
