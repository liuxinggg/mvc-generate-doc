package org.spring.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.javadoc.*;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ParameterizedType;
import com.sun.tools.javadoc.ClassDocImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.spring.config.CoreConfig;
import org.spring.entity.ApiDoc;
import org.spring.entity.RequestParameter;
import org.spring.entity.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import sun.reflect.generics.repository.ClassRepository;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

/**
 * 解析java doc
 */
@Component
public class ParseDoc {

    private CoreConfig coreConfig;
    @Autowired
    public void setCoreConfig(CoreConfig coreConfig) {
        this.coreConfig = coreConfig;
    }

    public List<ApiDoc> parseJavaDocToApiDoc(ClassDoc classDoc) throws Exception {

        //解析类的class对象
        Class<?> clazz = MyClassLoader.LoaderClass(coreConfig.getClassPath(), classDoc.toString());

        //获取class上的请求路径和请求请求方式
        String[] classMappings;
        RequestMethod[] classRequestMethod = null;
        RequestMapping classMappingAnno = AnnotatedElementUtils
                .getMergedAnnotation(clazz, RequestMapping.class);
        if(classMappingAnno != null) {
            classMappings = classMappingAnno.value();
            classRequestMethod = classMappingAnno.method();
        }else{
            classMappings = new String[]{""};
        }

        List<ApiDoc> apiDocList = new ArrayList<>();

        MethodDoc[] methodDocs = classDoc.methods();
        for (MethodDoc methodDoc : methodDocs) {
            String methodName = methodDoc.name();
            Parameter[] parameterDocs = methodDoc.parameters();
            Class[] parameterClass = new Class[parameterDocs.length];
            for (int i = 0; i < parameterDocs.length; i++) {
                parameterClass[i] = MyClassLoader.LoaderClass(coreConfig.getClassPath(), parameterDocs[i].type().toString());
            }
            Method method = clazz.getMethod(methodName, parameterClass);

            RequestMapping methodMappingAnno = AnnotatedElementUtils
                    .getMergedAnnotation(method, RequestMapping.class);
            if(methodMappingAnno == null) {
                //没有找到RequestMapping
                continue;
            }

            ApiDoc apiDoc = new ApiDoc();
            //获取method上的请求路径
            String[] methodMappings = methodMappingAnno.value();
            String[] requestUrls = new String[classMappings.length * methodMappings.length];
            int i = 0;
            for (String classMapping : classMappings) {
                for (String methodMapping : methodMappings) {
                    requestUrls[i++] = PathUtils.combine(classMapping, methodMapping);
                }
            }
            apiDoc.setRequestUrls(requestUrls);

            //获取method上的请求方式
            RequestMethod[] methodRequestMethods = methodMappingAnno.method();
            RequestMethod[] requestMethods = ArrayUtils.addAll(classRequestMethod, methodRequestMethods);
            String[] methodStrs = new String[requestMethods.length];
            for (int j = 0; j < requestMethods.length; j++) {
                methodStrs[j] = requestMethods[j].toString();
            }
            apiDoc.setRequestMethods(methodStrs);

            //获取描述信息
            String commentText = methodDoc.getRawCommentText();
            if(StringUtils.isNoneBlank(commentText)) {
                String[] lines = commentText.split("\\n");
                StringBuilder sb = new StringBuilder();
                for (String line : lines) {
                    if(StringUtils.isNotBlank(line) && !line.trim().startsWith("@")) {
                        sb.append(line).append("\n");
                    }
                }
                apiDoc.setDescription(sb.toString().trim());
            }

            //获取参数信息
            List<RequestParameter> params = new ArrayList<>();
            for (ParamTag paramTag : methodDoc.paramTags()) {
                RequestParameter requestParameter = new RequestParameter();
                requestParameter.setName(paramTag.parameterName());
                requestParameter.setDescription(paramTag.parameterComment());
                for (int j = 0; j < parameterDocs.length; j++) {
                    if(StringUtils.equals(paramTag.parameterName(), parameterDocs[j].name())) {
                        requestParameter.setType(parameterClass[j]);
                        MethodParameter methodParameter = new MethodParameter(method, j);
                        RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
                        PathVariable pathVariable = methodParameter.getParameterAnnotation(PathVariable.class);
                        if(requestParam != null) {
                            String name = requestParam.name();
                            if(StringUtils.isNotBlank(name)){
                                requestParameter.setName(name);
                            }
                            requestParameter.setRequired(requestParam.required());
                            String defaultValue = requestParam.defaultValue();
                            if(!ValueConstants.DEFAULT_NONE.equals(defaultValue)) {
                                //设置了默认值
                                requestParameter.setRequired(false);
                                requestParameter.setDefaultValue(defaultValue);
                            }
                        }
                        if(pathVariable != null) {
                            requestParameter.setRequired(true);
                        }
                        break;
                    }
                }
                params.add(requestParameter);
            }
            apiDoc.setParams(params);

            //获取相应结果对象信息
            Class<?> returnType = method.getReturnType();
            Type genericReturnType = method.getGenericReturnType();
            Type[] actualTypeArguments = null;
            if(genericReturnType instanceof java.lang.reflect.ParameterizedType) {
                java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) genericReturnType;
                actualTypeArguments =  pt.getActualTypeArguments();
            }
            Map<String, Object> data = fun(returnType, actualTypeArguments);
            ObjectMapper objectMapper = new ObjectMapper();
            String responseDemo = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
            apiDoc.setResponseDemo(responseDemo);

            apiDocList.add(apiDoc);
        }
        return apiDocList;
    }

    private Map<String, Object> fun(Class clazz, Type ... genericTypes) throws Exception {

        RootDoc docClass = JavaDocReader.getDocClass(coreConfig.getClassPath(), coreConfig.getCodeRootPath(), clazz.getName());
        ClassDoc[] classes = docClass.classes();
        ClassDoc classDoc = classes[0];

        Method getGenericInfo = clazz.getClass().getDeclaredMethod("getGenericInfo");
        getGenericInfo.setAccessible(true);
        ClassRepository genericInfo = (ClassRepository)getGenericInfo.invoke(clazz);
        Map<String, Class> genericMap = new HashMap<>();
        if(genericInfo != null) {
            TypeVariable<?>[] typeParameters = genericInfo.getTypeParameters();
            for (int i = 0; i < typeParameters.length; i++) {
                genericMap.put(typeParameters[i].getName(), (Class) genericTypes[i]);
            }
        }

        Map<Field, ClassDoc> fieldClassDocMap = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            fieldClassDocMap.put(field, classDoc);
        }
        List<Class<?>> allSuperclasses = ClassUtils.getAllSuperclasses(clazz);
        for (Class<?> superclass : allSuperclasses) {
            if(superclass.getPackage().equals(clazz.getPackage())) {
                Field[] superFields = superclass.getDeclaredFields();
                fields = ArrayUtils.addAll(fields, superFields);
                RootDoc superDocClass = JavaDocReader.getDocClass(coreConfig.getClassPath(), coreConfig.getCodeRootPath(), superclass.getName());
                ClassDoc[] superClasses = superDocClass.classes();
                ClassDoc superClassDoc = superClasses[0];
                for (Field superField : superFields) {
                    fieldClassDocMap.put(superField, superClassDoc);
                }
            }
        }

        Map<String, Object> map = new HashMap<>(fields.length);
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
                java.lang.reflect.Type genericType = field.getGenericType();
                if(genericType instanceof java.lang.reflect.ParameterizedType) {
                    //是泛型参数的类型
                    java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) genericType;
                    Type type1 = pt.getActualTypeArguments()[0];
                    Class<?> componentType;
                    if(type1 instanceof Class) {
                        componentType = (Class)type1;
                    }else {
                        String typeName = type1.getTypeName();
                        componentType = genericMap.get(typeName);
                    }
                    if(componentType != null && clazz.getPackage().equals(componentType.getPackage())) {
                        ArrayList<Map<String, Object>> list = new ArrayList<>(1);
                        list.add(fun(componentType));
                        map.put(propertyDescriptor.getName(), list);
                        continue;
                    }
                }
            }
            ClassDocImpl classDocImpl = (ClassDocImpl)fieldClassDocMap.get(field);
            FieldDoc fieldDoc = classDocImpl.findField(propertyDescriptor.getName());
            map.put(propertyDescriptor.getName(), fieldDoc.getRawCommentText().trim());
        }
        return map;
    }
}
