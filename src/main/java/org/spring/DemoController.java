package org.spring;

import org.spring.entity.ApiDoc;
import org.spring.entity.RequestParameter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文档生成工具测试接口
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    /**
     * 获取系统时间
     */
    @RequestMapping("/getSystemTime")
    public ApiDoc getSystemTime() {
        return null;
    }

    /**
     * 获取系统时间
     *
     * @param userId 用户id
     */
    @GetMapping("/getUserInfo")
    public RequestParameter getUserInfo(Integer userId) {
        return null;
    }

}