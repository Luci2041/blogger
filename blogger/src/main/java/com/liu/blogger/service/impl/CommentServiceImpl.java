package com.liu.blogger.service.impl;

import com.liu.blogger.pojo.Comment;
import com.liu.blogger.mapper.CommentMapper;
import com.liu.blogger.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Lucifer
 * @since 2025-03-04
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {
    @Resource
    CommentMapper commentMapper;
    @Override
    public List<Comment> getCommentsByArticleId(String articleId) {
        return commentMapper.getCommentsByArticleId(articleId);
    }
}
