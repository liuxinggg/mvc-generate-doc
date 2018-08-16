<#list docs as doc>
**描述：**
- ${doc.description}

**请求URL：**
<#list doc.requestUrls as url>
- ` ${url} `
</#list>

**请求方式：**
<#if (doc.params?size > 0)>
- <#list doc.requestMethods as method>` ${method} `</#list>
<#else>
- 不限
</#if>

**参数：**
<#if (doc.params?size > 0)>
|参数名|必选|类型|说明|默认值|
|:----:|:----:|:----:|:----:|:----:|
<#list doc.params as param>
|${param.name} |<#if (param.required)>是<#else>否</#if> |${param.type} |${param.description}|${param.defaultValue!"无"}|
</#list>
<#else>

  无
</#if>

**返回示例：**
```
${doc.responseDemo}
```
<#--**返回参数说明**-->

<#--|参数名|类型|说明|-->
<#--|:-----  |:-----|-----                           |-->
<#--|groupid |int   |用户组id，1：超级管理员；2：普通用户  |-->

<#--**备注**-->

<#--- 更多返回错误代码请看首页的错误代码描述-->
<#if doc_has_next>



---



</#if>
</#list>