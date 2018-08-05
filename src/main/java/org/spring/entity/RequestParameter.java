package org.spring.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 请求参数说明
 */
@Getter
@Setter
@ToString
public class RequestParameter {
    /** 参数名 */
    private String name;
    /** 参数类型 */
    private String type;
    /** 参数说明 */
    private String description;
    /** 是否必填 */
    private boolean required;
    /** 默认值 */
    private String defaultValue;
}
