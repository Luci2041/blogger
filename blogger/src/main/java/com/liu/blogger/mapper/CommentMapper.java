package com.liu.blogger.mapper;

import com.liu.blogger.pojo.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Lucifer
 * @since 2025-03-04
 */
public interface CommentMapper extends BaseMapper<Comment> {
    @Select("SELECT * FROM comment WHERE article_id = #{articleId} ORDER BY comment_time DESC")
    List<Comment> getCommentsByArticleId(String articleId);
}
