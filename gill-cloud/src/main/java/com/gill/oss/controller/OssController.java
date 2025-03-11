package com.gill.oss.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.sts20150401.models.AssumeRoleResponseBody.AssumeRoleResponseBodyCredentials;
import com.gill.oss.config.OssConfig;
import com.gill.oss.config.OssProperty;
import com.gill.oss.service.impl.OssService;
import com.gill.web.annotation.IgnoreAuth;
import com.gill.web.api.Response;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OssController
 *
 * @author gill
 * @version 2024/02/21
 **/
@RequestMapping("oss")
@RestController
@Slf4j
public class OssController {

    private static final String CONTENT_DISPOSITION = "Content-Disposition";

    private static final String FORMATTER = "attachment; filename=\"%s\"";

    @Autowired
    private OssProperty ossProperty;

    @Autowired
    private OssService ossService;

    /**
     * 下载文件
     *
     * @param fileName    文件命
     * @param credentials 临时凭证
     * @return 文件流
     */
    @GetMapping("/download/{fileName}")
    public Response<InputStreamResource> dowanload(@PathVariable(name = "fileName") String fileName,
        @RequestBody AssumeRoleResponseBodyCredentials credentials) {
        OSS ossClient = new OSSClientBuilder().build(System.getenv(OssConfig.OSS_ENDPOINT),
            credentials.accessKeyId, credentials.accessKeySecret, credentials.securityToken);
        OSSObject file = ossClient.getObject(ossProperty.getBucket(), fileName);
        ObjectMetadata metadata = file.getObjectMetadata();
        return Response.success(new InputStreamResource(file.getObjectContent()))
            .addHeader(CONTENT_DISPOSITION, String.format(FORMATTER, fileName))
            .contentType(MediaType.parseMediaType(metadata.getContentType()))
            .build(false);
    }

    /**
     * 获取资源前缀
     *
     * @return 资源前缀
     */
    @IgnoreAuth
    @GetMapping("/resource/prefix")
    public Response<String> getResourcePrefix() {
        String prefix =
            "https://" + ossProperty.getBucket() + "." + System.getenv(OssConfig.OSS_ENDPOINT) + "/"
                + ossProperty.getPublicResourcePath();
        return Response.success(prefix).build();
    }

    /**
     * 获取默认头像列表
     *
     * @return 头像文件列表
     */
    @GetMapping("/avatar/default")
    public Response<List<String>> getDefaultAvatarList() {
        return Response.success(ossService.getDefaultAvatarList()).build();
    }

    /**
     * 获取25个随机头像
     *
     * @return 头像列表
     */
    @GetMapping("/avatar/random")
    public Response<List<String>> getRandomDefaultAvatarList() {
        return Response.success(ossService.getRandomAvatarList()).build();
    }
}
