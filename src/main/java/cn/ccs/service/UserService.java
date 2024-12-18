package cn.ccs.service;

import cn.ccs.pojo.User;

/**
 * 用户服务接口
 */

public interface UserService {
    // 根据用户名查询用户
    User findUser(User user);
    User findUser(String username);

    // 根据用户名查询用户的存储空间大小
    String getCountSize(String username);
    // 添加用户
    boolean addUser(User user) throws Exception;
}
