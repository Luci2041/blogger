package com.liu.blogger.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.blogger.mapper.ArticleTagListMapper;
import com.liu.blogger.mapper.ArticleTagMapper;
import com.liu.blogger.pojo.ArticleTag;
import com.liu.blogger.pojo.ArticleTagList;
import com.liu.blogger.service.IArticleTagListService;
import com.liu.blogger.service.IArticleTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author : Lucifer
 * @Date : 2025/04/25/14:56
 * @Description :
 */
@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements IArticleTagService {
    @Autowired
    private ArticleTagMapper tagMapper;
    @Autowired
    private IArticleTagListService articleTagListService;
    @Override
    public String findOrCreateTagByName(String tagName) {
        String tagId = tagMapper.findTagIdByName(tagName);
        if (tagId == null) {
            tagId = UUID.randomUUID().toString();
            tagMapper.insertTag(tagId, tagName, new Date());
        }
        return tagId;
    }

    @Override
    public List<String> getArticleIdsByTagKeyword(String keyword) {
        // 查询包含keyword的标签名
        QueryWrapper<ArticleTag> tagWrapper = new QueryWrapper<>();
        tagWrapper.lambda().like(ArticleTag::getArticleTagName, keyword);
        List<ArticleTag> matchedTags = this.list(tagWrapper);

        if (matchedTags == null || matchedTags.isEmpty()) {
            return new ArrayList<>();
        }

        //获取标签id列表
        List<String> tagIds = new ArrayList<>();
        for (ArticleTag tag : matchedTags) {
            tagIds.add(tag.getArticleTagId());
        }

        //查询中间表中绑定的帖子id
        List<ArticleTagList> bindings = articleTagListService.lambdaQuery()
                .in(ArticleTagList::getArticleTagId, tagIds)
                .list();

        //提取文章ID
        Set<String> articleIds = new HashSet<>();
        for (ArticleTagList bind : bindings) {
            articleIds.add(bind.getArticleId());
        }

        return new ArrayList<>(articleIds);
    }

    @Override
    public List<ArticleTag> getTagsByArticleId(String articleId) {
        List<ArticleTagList> tagLinks = articleTagListService.list(
                new QueryWrapper<ArticleTagList>()
                        .eq("article_id", articleId)
        );

        if (tagLinks.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> tagIds = tagLinks.stream()
                .map(ArticleTagList::getArticleTagId)
                .collect(Collectors.toList());

        return this.listByIds(tagIds);
    }


}
