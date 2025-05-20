package com.liu.blogger.pojo;

import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author : Lucifer
 * @Date : 2025/04/27/21:55
 * @Description :
 */
@Data
public class PageResult<T> {
    private List<T> items;
    private int total;
    private int page;
    private int size;

    public PageResult(List<T> items, int total, int page, int size) {
        this.items = items;
        this.total = total;
        this.page = page;
        this.size = size;
    }
}
