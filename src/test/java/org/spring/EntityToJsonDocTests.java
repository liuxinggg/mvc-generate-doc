package org.spring;

import org.junit.Test;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EntityToJsonDocTests {


    @Test
    public void test() throws Exception {
//        ApiDoc apiDoc = new ApiDoc();
//        RequestParameter parameter = new RequestParameter();
//        ArrayList<RequestParameter> objects = new ArrayList<>();
//        objects.add(parameter);
//        apiDoc.setParams(objects);
//
//        ObjectMapper mapper = new ObjectMapper();
//
//        mapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
//            @Override
//            public void serialize(Object arg0, JsonGenerator arg1, SerializerProvider arg2) throws IOException, JsonProcessingException {
//                arg1.writeString("");
//            }
//        });
//        String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString("");
//        System.out.println(s);


//        Map<String, Object> map = new HashMap<>();
//        Class<ApiDoc> clazz = ApiDoc.class;
//        Map<String, Object> fun = fun(clazz);
//        System.out.println(JSONObject.fromObject(fun));
    }

    public Map<String, Object> fun(Class clazz) throws IntrospectionException {

        Map<String, Object> map = null;
        try {
            map = new HashMap<>();

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {

                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), clazz);
                Method readMethod = propertyDescriptor.getReadMethod();
                if(readMethod == null) {
                    continue;
                }
                Class<?> type = propertyDescriptor.getPropertyType();
                if(type.isArray()) {
                    Class<?> componentType = type.getComponentType();


                    if(clazz.getPackage().equals(componentType.getPackage())){
                        ArrayList<Map<String, Object>> list = new ArrayList<>(1);
                        list.add(fun(componentType));
                        map.put(propertyDescriptor.getName(), list);
                        continue;
                    }

                }else if(Collection.class.isAssignableFrom(type)){
                    Type genericType = field.getGenericType();
                    if(genericType instanceof ParameterizedType) {
                        //是泛型参数的类型
                        ParameterizedType pt = (ParameterizedType) genericType;
                        Class<?> componentType = (Class<?>)pt.getActualTypeArguments()[0];
                        if(clazz.getPackage().equals(componentType.getPackage())) {
                            ArrayList<Map<String, Object>> list = new ArrayList<>(1);
                            list.add(fun(componentType));
                            map.put(propertyDescriptor.getName(), list);
                            continue;
                        }
                    }
                }
                map.put(propertyDescriptor.getName(), "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
