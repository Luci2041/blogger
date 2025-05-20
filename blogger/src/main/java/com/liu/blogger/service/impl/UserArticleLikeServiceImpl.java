package com.liu.blogger.service.impl;

import com.liu.blogger.mapper.UserArticleLikeMapper;
import com.liu.blogger.pojo.Article;
import com.liu.blogger.service.IUserArticleLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author : Lucifer
 * @Date : 2025/04/17/11:53
 * @Description :
 */
@Service
public class UserArticleLikeServiceImpl implements IUserArticleLikeService {

    @Autowired
    private UserArticleLikeMapper userArticleLikeMapper;

    @Override
    public boolean exists(String userId, String articleId) {
        return userArticleLikeMapper.exists(userId, articleId);
    }

    @Override
    public void insert(String id, String userId, String articleId) {
        userArticleLikeMapper.insert(id,userId, articleId);
    }

    @Override
    public void delete(String userId, String articleId) {
        userArticleLikeMapper.delete(userId, articleId);
    }

    @Override
    public List<Article> findLikedArticlesByUserId(String userId) {
        return userArticleLikeMapper.selectLikedArticlesByUserId(userId);
    }


}
