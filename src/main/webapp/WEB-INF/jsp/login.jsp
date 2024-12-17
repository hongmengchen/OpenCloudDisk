<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<title>登陆</title>
<meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<link rel="stylesheet" href="${pageContext.request.contextPath }/css/login.css" />
<script type="text/javascript" src="${pageContext.request.contextPath }/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath }/js/login.js"></script>
<title></title>
</head>

<body>
	<div id="slider-title">
		<img src="${pageContext.request.contextPath }/img/logo.png" height="45" width="45" />
		<div id="title-logo">共享云盘</div>
	</div>
	<div class="slider">
		<ul class="slider-main">
			<li class="slider-panel"><a href="#" ><img
					alt="共享云盘" title="共享云盘" src="${pageContext.request.contextPath }/img/a.jpg"></a></li>
			<li class="slider-panel"><a href="#" ><img
					alt="共享云盘"   src="${pageContext.request.contextPath }/img/d.jpg"></a></li>
			<li class="slider-panel"><a href="#" ><img
					alt="共享云盘" title="共享云盘" src="${pageContext.request.contextPath }/img/g.jpg"></a></li>
			<li class="slider-panel"><a href="#" ><img
					alt="共享云盘" title="共享云盘" src="${pageContext.request.contextPath }/img/f.jpg"></a></li>
		</ul>
		<div class="slider-extra">
			<ul class="slider-nav">
				<li class="slider-item"></li>
				<li class="slider-item"></li>
				<li class="slider-item"></li>
				<li class="slider-item"></li>
			</ul>
		</div>
	</div>

	<div id="login">
		<form action="${pageContext.request.contextPath }/user/login.action" method="post">
			<div id="form-title">账号密码登录</div>
			<div align="center" style="color: red">${msg}</div>
			<input type="text" placeholder="用户名" name="username" class="login-input" id="name" />
			<input type="password" placeholder="密码" name="password" class="login-input" /><br/>
			<input type="submit" value="登录" class="login-btn" /><br/>
			<div id="bottom">
				<div id="inner">
					<div class="inner">
						<a href="#" class="a_inner">扫一扫登录</a>
					</div>
					<div class="img-login">
						<img src="${pageContext.request.contextPath }/img/weibo.png" width="25" height="25">
					</div>
					<div class="img-login">
						<img src="${pageContext.request.contextPath }/img/qq.png" width="25" height="25">
					</div>
					<div>
						<input type="submit" onclick="return regist()" value="立即注册" class="submit">
					</div>
					<div class="clearFloat"></div>
				</div>
			</div>
		</form>
	</div>
</body>
<script type="text/javascript">
	function regist(){
		window.location.href = "${pageContext.request.contextPath }/user/regist.action";
		return false;
	}
</script>
</html>
