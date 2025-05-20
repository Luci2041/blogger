package com.liu.blogger.pojo;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Lucifer
 * @since 2025-03-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Link implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 友情链接id
     */
    @TableId(value = "link_id")
    private String linkId;

    /**
     * 友情链接标题
     */
    private String linkTitle;

    /**
     * 友情链接的地址
     */
    private String linkUrl;

    /**
     * 友情链接logo
     */
    private String linkLogoUrl;

    /**
     * 添加友情链接的时间
     */
    private LocalDateTime linkAddTime;


}
