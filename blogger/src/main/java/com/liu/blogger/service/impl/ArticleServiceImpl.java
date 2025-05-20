package com.liu.blogger.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liu.blogger.pojo.Article;
import com.liu.blogger.mapper.ArticleMapper;
import com.liu.blogger.service.IArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.blogger.service.IArticleTagListService;
import com.liu.blogger.service.IArticleTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Lucifer
 * @since 2025-03-04
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {
    @Autowired
    private IArticleTagService articleTagService;

    @Autowired
    private IArticleTagListService articleTagListService;
    @Override
    public boolean incrementLikes(String articleId) {
        Article article = getById(articleId);
        if (article == null) {
            return false;
        }
        article.setArticleLikes(article.getArticleLikes() + 1);
        return updateById(article);
    }

    @Override
    public boolean decrementLikes(String articleId) {
        Article article = getById(articleId);
        if (article == null) {
            return false;
        }
        article.setArticleLikes(article.getArticleLikes() - 1);
        return updateById(article);
    }

    @Override
    public List<Article> searchArticlesByKeyword(String keyword) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(Article::getArticleTitle, keyword)   //搜索标题
                .or()
                .like(Article::getArticleContext, keyword);   //搜索内容
        List<Article> titleOrContentMatched = this.list(queryWrapper);

        List<String> tagMatchedArticleIds = articleTagService.getArticleIdsByTagKeyword(keyword);
        List<Article> tagMatchedArticles = new ArrayList<>();
        if (!tagMatchedArticleIds.isEmpty()) {
            tagMatchedArticles = this.listByIds(tagMatchedArticleIds);
        }

        // 根据 articleId合并两个结果然后去重
        Map<String, Article> resultMap = new LinkedHashMap<>();
        for (Article a : titleOrContentMatched) {
            resultMap.put(a.getArticleId(), a);
        }
        for (Article a : tagMatchedArticles) {
            resultMap.put(a.getArticleId(), a);
        }

        return new ArrayList<>(resultMap.values());
    }

    @Override
    public Article IncreaseViews(String articleId) {
        Article article = this.getById(articleId);
        if (article != null) {
            article.setArticleViewTimes(article.getArticleViewTimes() + 1);
            this.updateById(article);
        }
        return article;
    }
}
