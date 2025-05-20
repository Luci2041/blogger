package com.liu.blogger.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author : Lucifer
 * @Date : 2025/04/27/21:24
 * @Description :
 */
@Data
public class Report {

    private String id;

    private String articleId;
    private String commentId;
    private String reporterId;

    private String reason;

    private String detail;

    private String status = "PENDING";

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}

