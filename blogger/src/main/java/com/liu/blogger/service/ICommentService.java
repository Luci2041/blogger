package com.liu.blogger.service;

import com.liu.blogger.pojo.Comment;
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
public interface ICommentService extends IService<Comment> {

    List<Comment> getCommentsByArticleId(String articleId);
}
