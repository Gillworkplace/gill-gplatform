package com.gill.user.service;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.GifCaptcha;
import cn.hutool.captcha.ICaptcha;
import cn.hutool.captcha.LineCaptcha;
import com.gill.api.domain.UserProperties;
import com.gill.redis.core.Redis;
import com.gill.web.exception.WebException;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * CaptchaService
 *
 * @author gill
 * @version 2024/02/06
 **/
@Component
@Slf4j
public class CaptchaService {

    @Autowired
    private Redis redis;

    /**
     * 获取验证码
     *
     * @param randomCode 随机字符串
     * @return 验证码
     */
    public AbstractCaptcha generateCaptcha(String randomCode) {
        GifCaptcha captcha = CaptchaUtil.createGifCaptcha(95, 25);
        captcha.createCode();
        String captchaCode = captcha.getCode();
        redis.set(UserProperties.getRedisCaptchaKey(randomCode), captchaCode, 5L * 60 * 1000);
        return captcha;
    }

    /**
     * 校验随机码与验证码是否匹配
     *
     * @param randomCode  生成验证码的随机码
     * @param captchaCode 验证码
     */
    public void checkCaptchaCode(@Nonnull String randomCode, @Nonnull String captchaCode) {
        randomCode = randomCode.toLowerCase();
        String target = redis.get(UserProperties.getRedisCaptchaKey(randomCode));
        if (captchaCode.equals(target)) {
            return;
        }
        throw new WebException(HttpStatus.BAD_REQUEST, "验证码错误");
    }
}
