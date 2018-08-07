package org.spring.entity;

import com.sun.javadoc.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

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

    private static Class[] intClass = {
            Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE,
            Byte.class, Short.class, Integer.class, Long.class,
            BigInteger.class};

    private static Class[] decimalClass = {
            Float.TYPE, Double.TYPE,
            Float.class, Double.class,
            BigDecimal.class};
    private static Class[] dateClass = {
            Date.class, java.sql.Date.class,
            LocalDate.class, LocalTime.class,
            LocalDateTime.class};

    public void setType(Class type) {
        if(ArrayUtils.contains(intClass, type)) {
            this.type = "int";
            return;
        }
        if(ArrayUtils.contains(intClass, type)) {
            this.type = "decimal";
            return;
        }
        if(ArrayUtils.contains(dateClass, type)) {
            this.type = "date";
            return;
        }
        if (String.class.equals(type)) {
            this.type = "string";
            return;
        }
        this.type = null;
    }
}