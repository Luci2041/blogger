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
public class AdType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 广告类型id
     */
    @TableId(value = "ad_type_id")
    private String adTypeId;

    /**
     * 广告类型名称
     */
    private String adTypeTitle;

    /**
     * 广告标识(首页顶部广告、轮播图广告、文章详情广告)
     */
    private String adTypeTag;

    /**
     * 广告类型排序(越小越靠前)
     */
    private Integer adTypeSort;

    /**
     * 广告类型添加时间
     */
    private LocalDateTime adTypeAddTime;


}
