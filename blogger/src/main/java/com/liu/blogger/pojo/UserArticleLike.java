package com.liu.blogger.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author : Lucifer
 * @Date : 2025/04/17/11:48
 * @Description :
 */
@Data
@TableName("user_article_likes")

public class UserArticleLike implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId("id")
    private String id;

    @TableField("user_id")
    private String userId;

    @TableField("article_id")
    private String articleId;

    @TableField("liked_at")
    private LocalDateTime likedAt;

}
