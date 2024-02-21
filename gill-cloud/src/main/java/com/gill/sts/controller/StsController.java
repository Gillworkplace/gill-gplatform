package com.gill.sts.controller;

import cn.hutool.json.JSONUtil;
import com.aliyun.sts20150401.Client;
import com.aliyun.sts20150401.models.AssumeRoleRequest;
import com.aliyun.sts20150401.models.AssumeRoleResponse;
import com.aliyun.sts20150401.models.AssumeRoleResponseBody;
import com.aliyun.sts20150401.models.AssumeRoleResponseBody.AssumeRoleResponseBodyCredentials;
import com.gill.web.annotation.IgnoreAuth;
import com.gill.web.api.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * StsController
 *
 * @author gill
 * @version 2024/02/21
 **/
@RequestMapping("/sts")
@RestController
@Slf4j
public class StsController {

    private static final String ALIYUN_STS_ROLE = "ALIYUN_STS_ROLE";

    private static final long TOKEN_EXPIRED = 1200L;

    @Autowired
    private Client stsClient;

    /**
     * 获取临时token
     *
     * @return 临时token
     */
    @IgnoreAuth
    @GetMapping("/token")
    public Response<AssumeRoleResponseBodyCredentials> getStsToken() {
        AssumeRoleRequest request = new AssumeRoleRequest().setDurationSeconds(TOKEN_EXPIRED)
            .setRoleArn(System.getenv(ALIYUN_STS_ROLE))
            .setRoleSessionName("test");
        try {
            AssumeRoleResponse response = stsClient.assumeRole(request);
            AssumeRoleResponseBody body = response.getBody();
            log.debug("sts token: {}", JSONUtil.toJsonStr(response));
            return Response.success(body.getCredentials()).build();
        } catch (Exception e) {
            log.error("get sts token error: {}", e.toString());
        }
        return Response.<AssumeRoleResponseBodyCredentials>failed(HttpStatus.INTERNAL_SERVER_ERROR,
            "get sts credential failed").build();
    }
}
