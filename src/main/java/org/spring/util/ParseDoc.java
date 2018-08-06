package org.spring.util;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.spring.config.CoreConfig;
import org.spring.entity.ApiDoc;
import org.spring.entity.RequestParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
                        if(requestParam != null) {
                            requestParameter.setRequired(requestParam.required());
                            String defaultValue = requestParam.defaultValue();
                            requestParameter.setDefaultValue(ValueConstants.DEFAULT_NONE.equals(defaultValue) ? null : defaultValue);
                        }
                    }
                }
                params.add(requestParameter);
            }
            apiDoc.setParams(params);

            apiDocList.add(apiDoc);
        }
        return apiDocList;
    }
}
