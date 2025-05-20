package com.liu.blogger.pojo;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Lucifer
 * @since 2025-03-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "user_id")
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户手机
     */
    private String userPhone;

    /**
     * 用户性别
     */
    private String userSex;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 用户账号状态
     */
    private String userStatus;

    /**
     * 用户注册时间
     */
    private LocalDateTime userRegisterTime;


}
