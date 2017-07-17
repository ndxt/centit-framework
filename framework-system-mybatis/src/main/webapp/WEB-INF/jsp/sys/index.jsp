<%-- <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> --%>
<%@ taglib uri="http://www.centit.com/el/coderepo" prefix="cp"%>
<!DOCTYPE html>
<html lang="zh-cmn-Hans">
	<head>
		<meta charset="UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1">
    	<meta name="renderer" content="webkit">
    	<meta name="viewport" content="width=device-width, initial-scale=1.0">
    	
    	<title></title>
    	
		<script>
			window.ContextPath = '<%= request.getContextPath()%>/';
			window.ViewContextPath ='${cp:SYS_VALUE("app.staticfile.home")}/';
			
			// Js全局变量，在Js文件中使用
	        var GLOBAL_IDENTIFY = {
	            // 登录入口类型 是否为实施人员入口
	            IS_DEVELOP: '${"DEPLOY" eq sessionScope.ENTRANCE_TYPE}' == 'true',
	            userCode: '${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal.userCode}',
	            socket_host: '${cp:SYS_VALUE("socketio.host")}',
	            socket_port: '${cp:SYS_VALUE("socketio.port")}',
	            enable: '${cp:SYS_VALUE("socketio.enable")}' == 'true'
	        };
		</script>
	</head>

	<body class="centit-ui">
	</body>

	<script data-main="${cp:SYS_VALUE('app.staticfile.home')}/ui/app" src="${cp:SYS_VALUE('app.staticfile.home')}/ui/js/require.js"></script>
	
	<!-- 国际化 -->
<script type="text/javascript" src="${cp:SYS_VALUE('app.staticfile.home')}/ui/js/jquery/jquery-1.11.2.min.js"></script>
<script type="text/javascript">
 //jQuery前台获取i18n值    	 
 function setI18NText(moduleID){
	 
 }    
 
/*  (function() {
	 jQuery.i18n.properties({
         name : "messages", 		  //资源文件名称
         path : "${cp:SYS_VALUE('app.staticfile.home')}/data/", //资源文件路径
         mode : "both", 			  //用Map或Key的方式使用资源文件中的Key
         language : 'zh_CN'           //语言
     });
 })(); */
</script>
</html>
