package cn.ccs.controller;

import cn.ccs.pojo.User;
import cn.ccs.service.impl.FileServiceImpl;
import cn.ccs.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 用户控制器，处理用户相关的请求
 */

@Controller
@RequestMapping("/user")
public class UserController {
    /**
     * UserController类的私有成员变量
     * userService用于处理用户相关的业务逻辑
     * fileService用于处理文件相关的业务逻辑
     */
    private final UserServiceImpl userService;
    private final FileServiceImpl fileService;

    /**
     * UserController的构造函数
     * 通过此构造函数，完成对UserService和FileService的依赖注入
     *
     * @param userService UserServiceImpl实例，用于处理用户业务逻辑
     * @param fileService FileServiceImpl实例，用于处理文件业务逻辑
     */
    @Autowired
    public UserController(UserServiceImpl userService, FileServiceImpl fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }

    /**
     * 用户登录
     *
     * @param request  HTTP请求对象，用于获取请求信息和设置响应信息
     * @param user     用户对象，包含用户名和密码
     * @param response HTTP响应对象，用于向客户端发送响应
     * @return 登录成功则重定向到主页，否则返回登录页面
     */
    @RequestMapping("/login")
    public String login(HttpServletRequest request, User user, HttpServletResponse response) {
        // 检查用户名和密码是否为空
        if (user.getUsername() == null || user.getUsername().equals("") || user.getPassword() == null || user.getPassword().equals("")) {
            request.setAttribute("msg", "请输入用户名或密码");
            return "login";    //   /WEB-INF/jsp/ login.jsp
        }
        // 验证用户
        User exsitUser = userService.findUser(user);
        if (exsitUser != null) {
            HttpSession session = request.getSession();
            session.setAttribute(User.NAMESPACE, exsitUser.getUsername());
            session.setAttribute("totalSize", exsitUser.getTotalSize());
            return "redirect:/index.action";  //重定向到controller中,找一个新的RequestMapping,名称为index的请求
        } else {
            request.setAttribute("msg", "用户名或密码错误");
            return "login"; //   /WEB-INF/jsp/ login.jsp
        }
    }

    /**
     * 用户注册
     *
     * @param request  HTTP请求对象，用于获取请求信息和设置响应信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @param user     用户对象，包含用户名和密码
     * @return 注册成功则返回登录页面，否则返回注册页面
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping("/regist")
    public String regist(HttpServletRequest request, HttpServletResponse response, User user) throws Exception {
        System.out.println(user.getUsername() + "-------" + user.getPassword());
        // 检查用户名和密码是否为空
        if (user.getUsername() == null || user.getPassword() == null || user.getUsername().equals("") || user.getPassword().equals("")) {
            request.setAttribute("msg", "请输入用户名和密码");
            return "regist";
        } else {
            // 添加用户
            boolean isSuccess = userService.addUser(user);
            if (isSuccess) {
                fileService.addNewNameSpace(request, user.getUsername());
                return "login";
            } else {
                request.setAttribute("msg", "注册失败");
                return "regist";
            }
        }
    }

}
