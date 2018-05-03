# 前后端通讯数据格式

框架现在是4.0版本，相比3.×最大的变化就是前后端分离，前端页面显示和后端的逻辑设计完全分离，部署在不同的服务容器上。框架强力建议前段采用html+js（或者js框架比如 easyui、vue等等）的静态方案，但是这并不是强制，开发依然可以使用jsp、velocity等等前段解决方案，但是要求前段必须单独一个服务。这样做的目的是要求开发组将后端的业务通过接口的方式抽象出来，好处是不同平台的客户端，pc端、iOS、Android等等可以用共同的后端。减少后期的维护成本。

后端采用restful风格的接口，前后端用json的数据格式交换。后端除了返回单个数字、字符串、布尔型等标量其他的json都要符合[ResponseData](http://gitlab.centit.com/gitlab/ctm/centit-framework/blob/master/framework-adapter/src/main/java/com/centit/framework/common/ResponseData.java)定义的接口标准。

```java
    //JSON有三个域，其中data
    String RES_CODE_FILED="code";    
    String RES_MSG_FILED="message";
    String RES_DATA_FILED="data";
    //处理结果，0标识正常，其他的为错误号  ，具体错误好参见接口中的注释 
    int getCode();
    //处理结果的文字说明
    String getMessage();
    //返回数据，可以是任何类型数据包括数组，如果返回多个结果可以用Map
    Object getData();
```

这个接口有两个具体的实现[ResponseSingleData](http://gitlab.centit.com/gitlab/ctm/centit-framework/blob/master/framework-adapter/src/main/java/com/centit/framework/common/ResponseSingleData.java)和[ResponseMapData](http://gitlab.centit.com/gitlab/ctm/centit-framework/blob/master/framework-adapter/src/main/java/com/centit/framework/common/ResponseMapData.java)，在客户端接受到这个JSON时可以用[ResponseJSON](http://gitlab.centit.com/gitlab/ctm/centit-framework/blob/master/framework-adapter/src/main/java/com/centit/framework/common/ResponseJSON.java)来解析。框架中[JsonResultUtils](http://gitlab.centit.com/gitlab/ctm/centit-framework/blob/master/framework-adapter/src/main/java/com/centit/framework/common/JsonResultUtils.java)类提供了直接向HttpServletResponse写符合上述格式要求的JSON的便捷方法。所以在controller类中可以有多种方式来实现json格式的数据返回，示例代码如下：

```java
    //返回一个标量,比如:数字\字符串\布尔值等等
    @RequestMapping(value = "/url", method = { RequestMethod.GET })
    @ResponseBody
    public boolean checkUserOptPower() {
        return true;
    }

//返回符合格式的JSON对象
    @RequestMapping(value = "/url")
    @ResponseBody
    public ResponseData forExample() {
        //仅仅返回成功信息
        return new ResponseSingleData();
        //返回错误信息
        return new ResponseSingleData(ResponseData.ERROR_SESSION_TIMEOUT,
                    "用户没有登录或者超时，请重新登录。");
        //返回单个数据对象
        return ResponseSingleData.makeResponseData(new Object[]{"hello","world"});
        //返回多个对象
        ResponseMapData resData = new ResponseMapData();
        resData.addResponseData(BaseController.OBJLIST, new Object[]{"hello","world"});
        resData.addResponseData(BaseController.PAGE_DESC, new PageDesc());
        return resData;
    }

//用JsonResultUtils直接向 HttpServletResponse response 写json字符串的方式返回json
    @GetMapping(value = "/url")
    public void forExample(HttpServletResponse response) {
        //返回一个标量
        JsonResultUtils.writeOriginalObject(true, response);
        //仅仅返回成功信息
        JsonResultUtils.writeSuccessJson(response);
        return;
        //返回错误信息
        JsonResultUtils.writeErrorMessageJson(ResponseData.ERROR_UNAUTHORIZED, 
                        "用户没有登录或者超时，请重新登录",response);
        return;
        //返回单个数据对象
        JsonResultUtils.writeSingleDataJson(new Object[]{"hello","world"}, response);
        return;
        //返回多个对象
        ResponseMapData resData = new ResponseMapData();
        resData.addResponseData(BaseController.OBJLIST, new Object[]{"hello","world"});
        resData.addResponseData(BaseController.PAGE_DESC, new PageDesc());
        JsonResultUtils.writeResponseDataAsJson(resData, response);
        return;
    }
```

前端获取后端返回的json字符串可以使用[ResponseJSON](http://gitlab.centit.com/gitlab/ctm/centit-framework/blob/master/framework-adapter/src/main/java/com/centit/framework/common/ResponseJSON.java)来解析。示例代码：

java客户端

```java
CloseableHttpClient client = HttpExecutor.createKeepSessionHttpClient();
ResponseJSON resJson = ResponseJSON.valueOfJson(HttpExecutor.simpleGet(client,"url"));
client.close();
```

js客户端


```js
var Core = require('core/core');
Core.ajax(url, options)
    .then(function(data) {
        alert('成功');
    })
    // 如果需要捕获错误
    .catch(function(data) {
        alert('错误');
    });
```