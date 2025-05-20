package com.liu.blogger.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.blogger.mapper.AdminMapper;
import com.liu.blogger.pojo.Admin;
import com.liu.blogger.service.IAdminService;
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
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {
    @Resource
    AdminMapper adminMapper;

    @Override
    public Admin adminLogin(Admin admin) {
        final LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();

        if (admin.getAdminUsername() != null && !admin.getAdminUsername().isEmpty()) {
            queryWrapper.eq(Admin::getAdminUsername, admin.getAdminUsername());
        }
        if (admin.getAdminPassword() != null && !admin.getAdminPassword().isEmpty()) {
            queryWrapper.eq(Admin::getAdminPassword, admin.getAdminPassword());
        }

        return adminMapper.selectOne(queryWrapper);
    }
}

