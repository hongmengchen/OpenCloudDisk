package cn.ccs.utils;

import cn.ccs.pojo.User;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

public class UserUtils {
    public static String MD5(String password){
        if(password!=null){
            return DigestUtils.md5DigestAsHex(password.getBytes()).toUpperCase();
        }else{
            return null;
        }
    }
    public static String getUsername(HttpServletRequest request){
		return (String) request.getSession().getAttribute(User.NAMESPACE);
	}
}
