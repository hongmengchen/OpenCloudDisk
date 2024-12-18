<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-/W3C/DTD HTML 4.01 Transitional/EN">
<html>
<head>

<title>注册 | OCD</title>
<meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests">
<link rel="icon" href="${pageContext.request.contextPath }/img/logo.png" type="image/">
<link rel="stylesheet" href="${pageContext.request.contextPath }/css/regist.css" />
<script type="text/javascript" src="${pageContext.request.contextPath }/js/jquery.min.js"></script>
<title></title>
</head>
<body align="center">
    <!--header开始-->
    <div class="header">
        <div class="logo">
        <a href="${pageContext.request.contextPath }/user/regist.action" class="logo">注册账号</a>
        </div>
        <div class="login">
        <span>我已注册，现在就</span> <a href="${pageContext.request.contextPath }/user/login.action" class="login_icon">登录</a>
        </div>
        <div class="hr"></div>
    </div>
<!--header结束-->
<!--content开始-->
    <div class="content">
        <div class="reg">
            <form action="${pageContext.request.contextPath }/user/regist.action" method="post">
                <dl>
                <dt>用户名</dt> 
                <dd class="ipt_box">
                <input id="usr" name="username" type="text" placeholder="请设置用户名" /> <span class="clear"></span>
                </dd>
                <dd class="mes">
                <div class="tip">设置后不可更改<br>中英文均可，最长14个英文或7个汉字</div>
                <div class="error"><span class="error_icon"></span><span></span></div>
                </dd>
                </dl>
                <dl>
                <dt>密码</dt>
                <dd class="ipt_box">
                <input id="pwd" name="password" type="password" placeholder="密码" />
                <span class="clear"></span>
                </dd>
                <dd class="mes">
                <div class="error">
                <span class="error_icon"></span> <span></span>
                </div>
                <ul>
                    <li><span class="pwd_icon">○</span><span class="pwd_tip">长度为6~14个字符</span></li>
                    <li><span class="pwd_icon">○</span><span class="pwd_tip">支持数字、大小写字母和标点符号</span></li>
                    <li><span class="pwd_icon">○</span><span class="pwd_tip">不允许有空格</span></li>
                </ul>
                </dd>
                </dl>
                <dl>
                <dt></dt>
                <dd class="ipt_box pro">
                    <input id="agree" type="checkbox" checked /> <span>阅读并接受<a href="#">《XX用户协议》</a></span>
                     <span align="center" style="color: red">${msg}</span>
                </dd>
                </dl>
                <dl>
                <dt></dt>
                <dd><input class="regBtn" type="submit" value="注册" /></dd>
                <dd class="mes">
                <div class="error agreeErr">
                
                    <span class="error_icon"></span> <span>您还未接受百度用户协议</span>
                </div>
               
                </dd>
                </dl>
                
            </form>
            
        </div>
    </div>
    <!--content结束-->
    <div class="footer">
    <p>©qst青软实训</p>
    </div>
</body>
</html>