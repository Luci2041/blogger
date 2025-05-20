package com.liu.blogger.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liu.blogger.pojo.Report;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Lucifer
 * @since 2025-03-04
 */
public interface ReportMapper extends BaseMapper<Report> {
    int insert(Report report);

    Report selectById(@Param("id") Long id);

    /** 分页查询待处理举报 */
    @Select(
            "SELECT * " +
                    "FROM report " +
                    "WHERE status = #{status} " +
                    "ORDER BY create_time DESC " +
                    "LIMIT #{offset}, #{limit}"
    )
    List<Report> selectByStatus(
            @Param("status") String status,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    @Select("SELECT COUNT(1) FROM report WHERE status = #{status}")
    int countByStatus(@Param("status") String status);

    @Update(
            "UPDATE report " +
                    "SET status = #{status}, update_time = NOW() " +
                    "WHERE id = #{id}"
    )
    int updateStatus(
            @Param("id") Long id,
            @Param("status") String status
    );
}
