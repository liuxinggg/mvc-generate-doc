package org.spring.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 接口文档实体
 */
@Getter
@Setter
@ToString
public class ApiDoc {
    /** 接口描述 */
    private String description;
    /** 请求URL */
    private String[] requestUrls;
    /** 请求方式 */
    private String[] requestMethods;
    /** 请求参数列表 */
    private List<RequestParameter> params;
    /** 返回示例 */
    private String responseDemo;
    /** 返回参数列表 */
    private List<ResponseData> responseDatas;
    /** 备注信息 */
    private String remark;
}
