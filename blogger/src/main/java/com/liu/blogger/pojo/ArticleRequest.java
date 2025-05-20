package com.liu.blogger.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author : Lucifer
 * @Date : 2025/04/25/14:52
 * @Description :
 */
@Data
public class ArticleRequest {
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("article_title")
    private String articleTitle;

    @JsonProperty("article_context")
    private String articleContext;

    private List<String> tags;
}
