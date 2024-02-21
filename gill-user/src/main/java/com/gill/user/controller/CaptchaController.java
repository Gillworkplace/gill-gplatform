package com.gill.user.controller;

import cn.hutool.captcha.AbstractCaptcha;
import com.gill.user.service.CaptchaService;
import com.gill.web.annotation.IgnoreAuth;
import com.gill.web.api.Response;
import java.io.ByteArrayInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码接口
 *
 * @author gill
 * @version 2024/02/07
 **/
@RestController
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    /**
     * 获取验证码
     *
     * @param randomCode 随机字符串
     * @return 验证码图片二进制流
     */
    @IgnoreAuth
    @GetMapping("/captcha")
    public Response<InputStreamResource> getCaptcha(@RequestParam("randomCode") String randomCode) {
        AbstractCaptcha captcha = captchaService.generateCaptcha(randomCode);
        byte[] bytes = captcha.getImageBytes();
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        InputStreamResource isr = new InputStreamResource(bis);
        return Response.success(isr).contentType(MediaType.IMAGE_GIF).build(false);
    }
}
