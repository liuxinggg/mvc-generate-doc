**简要描述：**
- ${description}

**请求URL：**
<#list requestUrls as url>
- ` ${url} `
</#list>
**请求方式：**
- <#list requestMethods as method>` ${method} `</#list>

**参数：**

|参数名|必选|类型|说明|默认值|
|:----|:----|:-----|:-----|-----|
<#list params as param>
|${param.name} |<#if (param.required)>是<#else>否</#if> |${param.type} |${param.description}|${param.defaultValue!}|
</#list>


**返回示例**
```
{
    "error_code": 0,
    "data": {
    "uid": "1",
    "username": "12154545",
    "name": "吴系挂",
    "groupid": 2 ,
    "reg_time": "1436864169",
    "last_login_time": "0",
}
```
**返回参数说明**

|参数名|类型|说明|
|:-----  |:-----|-----                           |
|groupid |int   |用户组id，1：超级管理员；2：普通用户  |

**备注**

- 更多返回错误代码请看首页的错误代码描述
