<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title></title>
    <style>
        *{border: 0;margin: 0;padding: 0}
        .bg{
            background: url("${pageContext.request.contextPath}/images/bg.png") center center no-repeat;
            width: 1280px;
            height:800px;
            position: relative;
            margin: auto;
        }
        .back{
            position: absolute;
            left:580px;
            top:600px;
        }
    </style>

</head>
<body style="background: #bee8ff;">

<div class="bg">
    <a href="${pageContext.request.contextPath}/system/mainframe/index" class="back">
        <img src="${pageContext.request.contextPath}/images/back.png"/>
    </a>
</div>

</body>
</html>