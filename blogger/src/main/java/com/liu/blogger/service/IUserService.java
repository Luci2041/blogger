package com.liu.blogger.service;

import com.liu.blogger.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Lucifer
 * @since 2025-03-04
 */
public interface IUserService extends IService<User> {
    User loginCheck(User user);
    User selectUserByEmail(String email);
}
