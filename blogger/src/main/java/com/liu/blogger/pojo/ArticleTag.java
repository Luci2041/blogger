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
public class ArticleTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章标签id
     */
    @TableId(value = "article_tag_id")
    private String articleTagId;

    /**
     * 标签名称
     */
    private String articleTagName;

    /**
     * 添加时间
     */
    private LocalDateTime articleTagAddTime;


}
