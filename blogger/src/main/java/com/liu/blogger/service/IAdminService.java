package com.liu.blogger.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.blogger.pojo.Admin;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Lucifer
 * @since 2025-03-04
 */
public interface IAdminService extends IService<Admin> {
    Admin adminLogin(Admin admin);
}
