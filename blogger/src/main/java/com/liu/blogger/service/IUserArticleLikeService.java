package com.liu.blogger.service;

import com.liu.blogger.pojo.Article;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author : Lucifer
 * @Date : 2025/04/17/11:53
 * @Description :
 */
public interface IUserArticleLikeService {
    boolean exists(String userId, String articleId);

    void insert(String id, String userId, String articleId);

    void delete(String userId, String articleId);
    List<Article> findLikedArticlesByUserId(String userId);

}
