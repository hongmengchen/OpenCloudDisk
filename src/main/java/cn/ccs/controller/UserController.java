package cn.ccs.controller;

import cn.ccs.pojo.User;
import cn.ccs.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
    // 注入 UserService
    @Autowired
    private UserServiceImpl userService;

    // 登录
    @RequestMapping(,"/login")
    public String login(HttpServletRequest request, User user, HttpServletResponse response) {
        if(user.getUsername()==null||user.getUsername().equals("")||user.getPassword()==null||user.getPassword().equals("")){
            request.setAttribute("msg", "请输入用户名或密码");
            return "login";    //   /WEB-INF/jsp/ login.jsp
        }
        User exsitUser = userService.findUser(user);
        if(exsitUser != null){
            HttpSession session = request.getSession();
            session.setAttribute(User.NAMESPACE, exsitUser.getUsername());
            session.setAttribute("totalSize", exsitUser.getTotalSize());
            return "redirect:/index.action";  //重定向到controller中,找一个新的RequestMapping,名称为index的请求
        }else{
            request.setAttribute("msg", "用户名或密码错误");
            return "login"; //   /WEB-INF/jsp/ login.jsp
        }
    }
}
