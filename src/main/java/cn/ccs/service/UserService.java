package cn.ccs.service;

import cn.ccs.pojo.User;

public interface UserService {
    // 查询用户
    User findUser(User user);
    // 获取当前用户所用空间
    String getCountSize(String username);
}
