package com.liu.blogger.service;

import com.liu.blogger.pojo.ArticleTagList;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Lucifer
 * @since 2025-03-04
 */
public interface IArticleTagListService extends IService<ArticleTagList> {
    void bindArticleToTag(String articleId, String tagId);
    boolean removeByArticleId(String articleId);
}
