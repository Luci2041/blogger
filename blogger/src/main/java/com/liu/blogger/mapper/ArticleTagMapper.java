package com.liu.blogger.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liu.blogger.pojo.ArticleTag;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author : Lucifer
 * @Date : 2025/04/25/14:57
 * @Description :
 */
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {
    @Select("SELECT article_tag_id FROM article_tag WHERE article_tag_name = #{tagName}")
    String findTagIdByName(String tagName);

    @Insert("INSERT INTO article_tag(article_tag_id, article_tag_name, article_tag_add_time) VALUES (#{tagId}, #{tagName}, #{addTime})")
    void insertTag(String tagId, String tagName, Date addTime);
}
