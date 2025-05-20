package com.liu.blogger.service.impl;

import com.liu.blogger.pojo.ArticleTagList;
import com.liu.blogger.mapper.ArticleTagListMapper;
import com.liu.blogger.service.IArticleTagListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Lucifer
 * @since 2025-03-04
 */
@Service
public class ArticleTagListServiceImpl extends ServiceImpl<ArticleTagListMapper, ArticleTagList> implements IArticleTagListService {
    @Autowired
    private ArticleTagListMapper articleTagListMapper;

    @Override
    public void bindArticleToTag(String articleId, String tagId) {
        String listId = UUID.randomUUID().toString();
        articleTagListMapper.insertBinding(listId, articleId, tagId);
    }

    @Override
    public boolean removeByArticleId(String articleId) {
        int rows = baseMapper.deleteByArticleId(articleId);
        return rows >= 0;
    }
}
