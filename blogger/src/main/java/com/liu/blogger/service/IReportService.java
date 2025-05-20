package com.liu.blogger.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.blogger.pojo.PageResult;
import com.liu.blogger.pojo.Report;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author : Lucifer
 * @Date : 2025/04/27/21:39
 * @Description :
 */
public interface IReportService extends IService<Report> {
    Report submitReport(Report report);
    Report getReportById(Long id);
    PageResult<Report> listPending(int page, int size);
    Report changeStatus(Long id, String status);
}
