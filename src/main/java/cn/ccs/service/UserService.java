package cn.ccs.service;

import cn.ccs.pojo.User;

/**
 * 用户服务接口
 */

public interface UserService {
    /**
     * 查询用户信息
     *
     * @param user 用户对象，包含查询条件
     * @return 返回查询到的用户对象
     */
    User findUser(User user);

    User findUser(String username);
    /**
     * 获取当前用户所用空间
     *
     * @param username 用户名
     * @return 返回用户的使用空间大小
     */
    String getCountSize(String username);

    boolean addUser(User user) throws Exception;
}
