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
public class CommentReply implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论回复id
     */
    @TableId(value = "comment_reply_id")
    private String commentReplyId;

    /**
     * 评论id
     */
    private String commentId;

    /**
     * 回复人id
     */
    private String replyUserId;

    /**
     * 继续回复的人id
     */
    private String secondlyUserId;

    /**
     * 评论回复的时间
     */
    private LocalDateTime commentReplyTime;


}
