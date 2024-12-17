package cn.ccs.controller;

import cn.ccs.service.impl.UserServiceImpl;
import cn.ccs.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 控制器类，用于处理与用户首页相关的请求
 */

@Controller
public class IndexController {
    // 用户服务实现类，用于执行用户相关的业务逻辑
    private final UserServiceImpl userService;

    /**
     * 构造函数注入UserService
     *
     * @param userService 用户服务实现类实例
     */
    @Autowired
    public IndexController(UserServiceImpl userService) {
        this.userService = userService;
    }

    /**
     * 处理用户首页请求的方法
     *
     * @param request HTTP请求对象，用于获取用户信息
     * @return 返回首页视图的名称
     */
    @RequestMapping("/index")
    public String index(HttpServletRequest request) {
        // 从请求中获取当前用户的用户名
        String username = UserUtils.getUsername(request);
        //从上面得到的username作为参数 ,再向数据库发送获取当前用户所用空间的请求
        String countSize = userService.getCountSize(username);
        //将countSize放在request对象中 ,传递到页面
        request.setAttribute("countSize", countSize);
        // 返回首页视图的名称，对应实际路径 /WEB-INF/jsp/index.jsp 这是云盘软件的首页
        return "index";
    }
}
