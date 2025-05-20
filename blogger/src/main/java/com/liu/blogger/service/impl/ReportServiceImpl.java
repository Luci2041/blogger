package com.liu.blogger.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.blogger.mapper.ReportMapper;
import com.liu.blogger.pojo.PageResult;
import com.liu.blogger.pojo.Report;
import com.liu.blogger.service.IReportService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author : Lucifer
 * @Date : 2025/04/27/21:42
 * @Description :
 */
@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements IReportService {
    @Resource
    ReportMapper reportMapper;
    @Override
    public Report submitReport(Report report) {
        report.setStatus("PENDING");
        reportMapper.insert(report);
        return report;
    }

    @Override
    public PageResult<Report> listPending(int page, int size) {
        int offset = page * size;
        List<Report> list = reportMapper.selectByStatus("PENDING", offset, size);
        int total = reportMapper.countByStatus("PENDING");
        return new PageResult<>(list, total, page, size);
    }

    @Override
    public Report getReportById(Long id) {
        return reportMapper.selectById(id);
    }

    @Override
    public Report changeStatus(Long id, String status) {
        reportMapper.updateStatus(id, status);
        return reportMapper.selectById(id);
    }
}
