package com.liu.blogger.pojo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
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
public class ArticleTagList implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章对应标签id
     */
    @TableId(value = "article_tag_list_id")
    private String articleTagListId;

    /**
     * 文章id
     */
    @TableField("article_id")
    private String articleId;

    /**
     * 文章标签id
     */
    @TableField("article_tag_list")
    private String articleTagId;


}
