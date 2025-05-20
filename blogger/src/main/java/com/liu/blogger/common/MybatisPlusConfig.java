package com.liu.blogger.common;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author : Lucifer
 * @Date : 2025/03/04/16:35
 * @Description :
 */
@EnableTransactionManagement
@Configuration
@MapperScan("com.liu.blogger.mapper")
public class MybatisPlusConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置请求的页面大于最大页后操作，true调回到首页，false 继续请求默认false
        // paginationInterceptor.setOverflow(false);
        // 设置最大单页限制数量，默认500 条，-1.不受限制
        paginationInterceptor.setLimit(50);
        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
        // 开启 count 的 join优化,只针对部分left join
        return paginationInterceptor;
    }
}
