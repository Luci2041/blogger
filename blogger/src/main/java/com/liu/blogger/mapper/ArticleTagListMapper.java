package com.liu.blogger.mapper;

import com.liu.blogger.pojo.ArticleTagList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Lucifer
 * @since 2025-03-04
 */
public interface ArticleTagListMapper extends BaseMapper<ArticleTagList> {
    @Insert("INSERT INTO article_tag_list(article_tag_list_id, article_id, article_tag_list) VALUES (#{listId}, #{articleId}, #{tagId})")
    void insertBinding(String listId, String articleId, String tagId);

    @Delete("DELETE FROM article_tag_list WHERE article_id = #{articleId}")
    int deleteByArticleId(@Param("articleId") String articleId);
}
