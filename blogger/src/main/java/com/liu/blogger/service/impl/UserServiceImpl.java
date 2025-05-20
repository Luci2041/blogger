package com.liu.blogger.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liu.blogger.pojo.User;
import com.liu.blogger.mapper.UserMapper;
import com.liu.blogger.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Lucifer
 * @since 2025-03-04
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    UserMapper userMapper;

    @Override
    public User loginCheck(User user) {
        final LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(user.getUserEmail() != null, User::getUserEmail, user.getUserEmail())
                .eq(user.getUserPassword() != null, User::getUserPassword, user.getUserPassword());
        return userMapper.selectOne(userLambdaQueryWrapper);
    }

    @Override
    public User selectUserByEmail(String email) {
        final LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(email != null, User::getUserEmail, email);
        return userMapper.selectOne(userLambdaQueryWrapper);
    }
}

