package cn.ccs.controller;

import cn.ccs.service.impl.UserServiceImpl;
import cn.ccs.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {
    @Autowired
    private UserServiceImpl userService;
    @RequestMapping("/index")
    public String index(HttpServletRequest request){
        String username = UserUtils.getUsername(request);
        //从上面得到的username作为参数 ,再向数据库发送获取当前用户所用空间的请求
        String countSize = userService.getCountSize(username);
        //将countSize放在request对象中 ,传递到页面
        request.setAttribute("countSize",countSize);
        return "index";  //     /WEB-INF/jsp/ index.jsp   这是云盘软件的首页
    }
}
