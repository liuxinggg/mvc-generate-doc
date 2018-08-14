package org.spring.util;

import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class JavaTypeConvert {

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

    /**
     * 获取常见Java Type的string 类型
     * @param type typeClass
     * @return typeString
     */
    public static String getTypeString(Class type) {

        if(ArrayUtils.contains(intClass, type)) {
            return "int";
        }
        if(ArrayUtils.contains(decimalClass, type)) {
            return "decimal";
        }
        if(ArrayUtils.contains(dateClass, type)) {
            return "date";
        }
        if (String.class.equals(type)) {
            return "string";
        }
        return null;
    }

}
