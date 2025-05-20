package com.liu.blogger.service;

import com.liu.blogger.pojo.Article;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Lucifer
 * @since 2025-03-04
 */
public interface IArticleService extends IService<Article> {
    boolean incrementLikes(String articleId);
    boolean decrementLikes(String articleId);
    List<Article> searchArticlesByKeyword(String keyword);
    Article IncreaseViews(String articleId);
}
