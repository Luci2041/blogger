package com.liu.blogger.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.blogger.pojo.ArticleTag;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 *
 * @Author : Lucifer
 * @Date : 2025/04/25/14:55
 * @Description :
 */
public interface IArticleTagService extends IService<ArticleTag> {
    String findOrCreateTagByName(String tagName);
    List<String> getArticleIdsByTagKeyword(String keyword);
    List<ArticleTag> getTagsByArticleId(String articleId);
}
