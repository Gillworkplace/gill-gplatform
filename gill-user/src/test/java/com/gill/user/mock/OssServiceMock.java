package com.gill.user.mock;

import com.gill.api.service.oss.IOssService;
import java.util.List;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

/**
 * OssServiceMock
 *
 * @author zhangzhiyan
 * @since 2025-03-05
 */
@Component
@DubboService
public class OssServiceMock implements IOssService {

    @Override
    public List<String> getDefaultAvatarList() {
        return List.of("/a.png");
    }

    @Override
    public List<String> getRandomAvatarList() {
        return List.of("/a.png");
    }
}
