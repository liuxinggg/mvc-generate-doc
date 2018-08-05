package org.spring.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 配置
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "generate")
public class CoreConfig {
    /** 源码路径 */
    private String codePath;
    /** 源码classpath */
    private String classPath;
    /** 生成文档路径 */
    private String saveDocPath;
}
