package com.liu.blogger.pojo;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章id
     */
    @TableId(value = "article_id")
    private String articleId;

    /**
     * 用户id
     */
    @JsonProperty("user_id")
    private String userId;

    /**
     * 文章标题
     */
    @JsonProperty("article_title")
    private String articleTitle;

    /**
     * 文章发布时间
     */
    private String articlePostTime;

    /**
     * 文章内容
     */
    @JsonProperty("article_context")
    private String articleContext;

    /**
     * 点赞次数
     */
    private Integer articleLikes;

    /**
     * 观看次数
     */
    private Integer articleViewTimes;

    /**
     * 收藏次数
     */
    private Integer articleCollectionTimes;


}
