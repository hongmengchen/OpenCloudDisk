package cn.ccs.utils;

import cn.ccs.pojo.User;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户工具类
 */

public class UserUtils {
    /**
     * 将密码进行MD5加密
     *
     * @param password 明文密码
     * @return 加密后的密码若输入密码为null，则返回null
     */
    public static String MD5(String password) {
        if (password != null) {
            return DigestUtils.md5DigestAsHex(password.getBytes()).toUpperCase();
        } else {
            return null;
        }
    }

    /**
     * 从请求中获取当前用户的用户名
     *
     * @param request HTTP请求对象
     * @return 当前用户的用户名如果没有登录用户，则返回null
     */
    public static String getUsername(HttpServletRequest request) {
        return (String) request.getSession().getAttribute(User.NAMESPACE);
    }
}
