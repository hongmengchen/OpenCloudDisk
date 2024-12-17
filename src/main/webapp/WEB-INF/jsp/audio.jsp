<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath }/css/audio.css">
<title>Insert title here</title>
</head>
<body>
<div class="audio-box">
		<div class="audio-container">
			<div class="audio-cover"></div>
			<div class="audio-view">
				<h3 class="audio-title">${fileName }</h3>
				<input type="hidden" id="currentPath" value="${currentPath }"/>
				<div class="audio-body">
					<div class="audio-backs">
						<div class="audio-this-time">00:00</div>
						<div class="audio-count-time">00:00</div>
						<div class="audio-setbacks">
							<i class="audio-this-setbacks">
								<span class="audio-backs-btn"></span>
							</i>
							<span class="audio-cache-setbacks">
							</span>
						</div>
					</div>
				</div>
				<div class="audio-btn">
					<div class="audio-select">
						<div class="audio-prev"></div>
						<div class="audio-play"></div>
						<div class="audio-next"></div>
						<div class="audio-menu"></div>
						<div class="audio-volume"></div>
					</div>
					<div class="audio-set-volume">
						<div class="volume-box">
							<i><span></span></i>
						</div>
					</div>
					<div class="audio-list">
						<div class="audio-list-head">
							<p>☺随心听</p>
							<span class="menu-close">关闭</span>
						</div>
						<ul class="audio-inline">
						</ul>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>

<script src="${pageContext.request.contextPath }/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath }/js/audio.js"></script>
<script type="text/javascript">
$(function(){
	var currentPath = $("#currentPath").val();
	var fileName = $("h3").text();
	var url = encodeURI('currentPath='+currentPath+'&fileType=audio&fileName='+fileName);
	var song = [
		{
			'cover' : '../img/cover.jpg',
			'src' : 'openFile.action?' + url,
			'title' : fileName
		}

	];

	var audioFn = audioPlay({
		song : song,
		autoPlay : true  
	});

	
});
</script>
</html>