package org.spring.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.spring.util.JavaTypeConvert;

/**
 * 响应参数说明
 */
@Getter
@Setter
@ToString
public class ResponseData {
    /** 参数名 */
    private String name;
    /** 参数类型 */
    private String type;
    /** 参数说明 */
    private String description;

    public void setType(Class type) {
        this.type = JavaTypeConvert.getTypeString(type);
    }
}
