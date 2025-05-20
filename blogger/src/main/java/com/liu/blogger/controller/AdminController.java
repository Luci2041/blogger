package com.liu.blogger.controller;

import com.liu.blogger.pojo.*;
import com.liu.blogger.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author : Lucifer
 * @Date : 2025/03/04/16:48
 * @Description :
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/Admin")
public class AdminController {
    @Autowired
    private IAdminService adminService;

    @Autowired
    private IUserService userService;
    @Autowired
    private IArticleService articleService;
    @Autowired
    private IReportService reportService;
    @Autowired
    private IArticleTagListService articleTagListService;

    @Autowired
    private IArticleTagService articleTagService;

    /**
     * 管理员登录
     */
    @PostMapping("/adminLogin")
    public Object adminLogin(@RequestBody Admin admin) {
        Admin admin1 = adminService.adminLogin(admin);
        return admin1 == null ? false : admin1;
    }

    /**
     * 查询所有用户
     */
    @GetMapping("/allUser")
    public List<User> alluserList() {
        return userService.list();
    }

    /**
     * 封禁用户
     */
    @PutMapping("/banUser/{userId}")
    public int banUser(@PathVariable String userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return 400;
        }
        user.setUserStatus("3");
        boolean ok = userService.updateById(user);
        return ok ? 200 : 500;
    }

    /**
     * 解封用户
     */
    @PutMapping("/unBanUser/{userId}")
    public int unBanUser(@PathVariable String userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return 400;
        }
        user.setUserStatus("1");
        boolean ok = userService.updateById(user);
        return ok ? 200 : 500;
    }

    /**
     * 查询所有文章
     */
    @GetMapping("/allArticle")
    public List<Article> allArticleList() {
        return articleService.list();
    }

    /**
     * 根据文章 ID 获取该文章的所有标签
     */
    @GetMapping("/articleTagsByArticle/{articleId}")
    public List<ArticleTag> articleTagsByArticle(@PathVariable String articleId) {
        return articleTagService.getTagsByArticleId(articleId);
    }

    /**
     * 删除文章
     *
     * @param articleId 要删除的文章ID
     * @return 200-删除成功，400-未找到，500-删除失败
     */
    @DeleteMapping("/article/{articleId}")
    public int deleteArticle(@PathVariable String articleId) {
        Article article = articleService.getById(articleId);
        if (article == null) {
            return 400;
        }
        articleTagListService.removeByArticleId(articleId);

        boolean ok = articleService.removeById(articleId);
        return ok ? 200 : 500;
    }

    @GetMapping("/pending")
    public Result<PageResult<Report>> pending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<Report> pr = reportService.listPending(page, size);
        return Result.success(pr);
    }

    @GetMapping("/{id}")
    public Result<Report> get(@PathVariable Long id) {
        return Result.success(reportService.getReportById(id));
    }

    @PutMapping("/{id}/status")
    public Result<Report> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return Result.success(reportService.changeStatus(id, status));
    }
}
