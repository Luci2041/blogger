package com.liu.blogger.mapper;

import com.liu.blogger.pojo.Article;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author : Lucifer
 * @Date : 2025/04/17/11:51
 * @Description :
 */
public interface UserArticleLikeMapper {
    @Insert("INSERT INTO user_article_likes(id,user_id, article_id) VALUES(#{id},#{userId}, #{articleId})")
    int insert(@Param("id") String id,@Param("userId") String userId, @Param("articleId") String articleId);

    @Select("SELECT COUNT(*) FROM user_article_likes WHERE user_id = #{userId} AND article_id = #{articleId}")
    boolean exists(@Param("userId") String userId, @Param("articleId") String articleId);


    @Delete("DELETE FROM user_article_likes WHERE user_id = #{userId} AND article_id = #{articleId}")
    int delete(@Param("userId") String userId, @Param("articleId") String articleId);

    @Select(
            "SELECT a.* " +
                    "FROM article a " +
                    "JOIN user_article_likes ual ON a.article_id = ual.article_id " +
                    "WHERE ual.user_id = #{userId}"
    )
    List<Article> selectLikedArticlesByUserId(@Param("userId") String userId);
}
