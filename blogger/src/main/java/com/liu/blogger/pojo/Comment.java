package com.liu.blogger.pojo;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章评论id
     */
    @TableId(value = "comment_id")
    private String commentId;

    /**
     * 文章id
     */
    @JsonProperty("articleId")
    private String articleId;

    /**
     * 用户id(评论人)
     */
    @JsonProperty("userId")
    private String userId;

    /**
     * 评论内容
     */
    @JsonProperty("content")
    private String content;
    /**
     * 评论时间
     */
    private LocalDateTime commentTime;

    /**
     * 点赞次数
     */
    private Integer commentLikesTime;


}
