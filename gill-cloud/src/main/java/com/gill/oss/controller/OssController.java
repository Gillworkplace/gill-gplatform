package com.gill.oss.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.sts20150401.models.AssumeRoleResponseBody.AssumeRoleResponseBodyCredentials;
import com.gill.oss.config.OssProperty;
import com.gill.web.api.Response;
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
    public static final String FORMATTER = "attachment; filename=\"%s\"";

    @Autowired
    private OssProperty ossProperty;

    @GetMapping("download/{fileName}")
    public Response<InputStreamResource> dowanload(@PathVariable(name = "fileName") String fileName,
        @RequestBody AssumeRoleResponseBodyCredentials credentials) {
        OSS ossClient = new OSSClientBuilder().build(ossProperty.getEndpoint(),
            credentials.accessKeyId, credentials.accessKeySecret, credentials.securityToken);
        OSSObject file = ossClient.getObject(ossProperty.getBucket(), fileName);
        ObjectMetadata metadata = file.getObjectMetadata();
        return Response.success(new InputStreamResource(file.getObjectContent()))
            .addHeader(CONTENT_DISPOSITION, String.format(FORMATTER, fileName))
            .contentType(MediaType.parseMediaType(metadata.getContentType()))
            .build(false);
    }

}
