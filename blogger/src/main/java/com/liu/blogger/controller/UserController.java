package com.liu.blogger.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liu.blogger.pojo.*;
import com.liu.blogger.service.*;
import io.github.thundzeng.miniemail.config.MailConfig;
import io.github.thundzeng.miniemail.constant.SmtpEnum;
import io.github.thundzeng.miniemail.core.MiniEmailFactory;
import io.github.thundzeng.miniemail.core.MiniEmailFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author : Lucifer
 * @Date : 2025/03/02/17:08
 * @Description :
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/User")
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IArticleService articleService;
    @Autowired
    private ICommentService commentService;

    @Autowired
    private IArticleTagService tagService;
    @Autowired
    private IReportService reportService;

    @Autowired
    private IArticleTagListService articleTagListService;

    @Autowired
    private IUserArticleLikeService userArticleLikeService;
    private static final String userName = "2041741256@qq.com";
    private static final String password = "*****************";  //密钥
    private static final String nickName = "LXBlogger";
    MiniEmailFactory miniEmailFactory;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 发送邮件
     * @param email 邮箱
     */
    @GetMapping("/sendCode/{email}")
    private Object sendCode(@PathVariable String email) {
        this.miniEmailFactory = new MiniEmailFactoryBuilder().build(MailConfig.config(userName, password)
                .setMailDebug(false)
                .setSenderNickname(nickName)
                .setMailSmtpHost(SmtpEnum.SMTP_QQ));
        try {
            final String code = random3();
            String redisKey = "code:" + email;
            String emailContent = "您的验证码是：" + code + "，10分钟内有效，请勿泄露。";
            miniEmailFactory.init().send(email, emailContent);
            redisTemplate.opsForValue().set(redisKey, code, 600, TimeUnit.SECONDS);
            System.out.println("发送到 " + email + " 的验证码：" + code);
            return null;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * 判断验证码
     * @param userEmail 邮箱
     * @param captcha 验证码
     * @return true/false
     */
    @GetMapping("/checkCaptcha/{userEmail}/{captcha}")
    private Object checkCode(@PathVariable String userEmail, @PathVariable String captcha) {
//        System.out.println("验证登录");
        String redisKey = "code:" + userEmail;
        String storedCode = (String) redisTemplate.opsForValue().get(redisKey);
        if (!captcha.equals(storedCode)) {
            return false;
        }
        User existingUser = userService.selectUserByEmail(userEmail);
        if (existingUser != null) {
            return true;
        } else {
            try {
                User newUser = new User();
                newUser.setUserId(UUID.randomUUID().toString());
                newUser.setUserEmail(userEmail);
                newUser.setUserName(userEmail.split("@")[0]);
                newUser.setUserStatus("1");
                newUser.setUserRegisterTime(LocalDateTime.now());
                boolean isSaved = userService.save(newUser);
                if (isSaved) {
                    System.out.println("自动创建新用户: " + userEmail);
                    return true;
                } else {
                    System.out.println("自动创建用户失败: " + userEmail);
                    return false;
                }
            } catch (Exception e) {
                System.err.println("自动创建用户异常: " + e.getMessage());
                return false;
            }
        }
    }

    /**
     *
     */
    @PostMapping("/loginCheck")
    public Object loginCheck(@RequestBody User user) {
        User user1 = userService.loginCheck(user);
        return user1 != null;
    }

    /**
     * 用户个人信息查询
     * @param userEmail 通过邮箱查询用户信息
     * @return User
     */
    @GetMapping("/findUserInfoByEmail/{userEmail}")
    private User findUserInfoById(@PathVariable String userEmail) {
        return userService.selectUserByEmail(userEmail);
    }

    @GetMapping("/findUserInfoByUserId/{userId}")
    private User findUserInfoByUserId(@PathVariable String userId) {
        return userService.getById(userId);
    }

    /**
     * 用户修改个人信息
     * @param userId 用户id
     * @param updateData 传入需要修改的参数
     * @return true/false
     */
    @PostMapping("/updateUserInfoByUserId/{userId}")
    public Object updateUserInfoByUserId(@PathVariable String userId, @RequestBody Map<String, String> updateData) {
        try {
            User existingUser = userService.getById(userId);
            if (updateData.containsKey("userAvatar")) {
                existingUser.setUserAvatar(updateData.get("userAvatar"));
            }
            if (updateData.containsKey("userName")) {
                existingUser.setUserName(updateData.get("userName"));
            }
            if (updateData.containsKey("userEmail")) {
                existingUser.setUserEmail(updateData.get("userEmail"));
            }
            if (updateData.containsKey("userSex")) {
                existingUser.setUserSex(updateData.get("userSex"));
            }
            boolean updated = userService.updateById(existingUser);
            if (updated) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * 上传文件
     * @param file 文件
     * @return 文件名
     */
    @PostMapping("/upload")
    @ResponseBody
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String uploadDir = "C:/blogger/uploads/";

        // 获取文件名并生成新的唯一文件名
        String fileName = file.getOriginalFilename();
        String newFileName = UUID.randomUUID() + fileName.substring(fileName.lastIndexOf("."));

        // 创建文件目录（如果目录不存在）
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();  // 如果目录不存在则创建
        }
        File fileToSave = new File(uploadDir + newFileName);// 文件保存路径
        file.transferTo(fileToSave);
        String imageUrl = "/static/" + newFileName;

        // 返回图片URL
        Map<String, String> response = new HashMap<>();
        response.put("data", imageUrl);
        System.out.println("上传的图片地址 " + imageUrl);
        return response;
    }


    /**
     * 用户上传文章
     * @return 返回true/false
     */
    @PostMapping("/userPostArticle")
    public boolean userPostArticle(@RequestBody ArticleRequest articleRequest) {
        try {
            Article article = new Article();
            article.setArticleId(UUID.randomUUID().toString());
            article.setUserId(articleRequest.getUserId());
            article.setArticleTitle(articleRequest.getArticleTitle());
            article.setArticleContext(articleRequest.getArticleContext());
            article.setArticlePostTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            article.setArticleLikes(0);
            article.setArticleViewTimes(0);
            article.setArticleCollectionTimes(0);
            articleService.save(article);
            System.err.println(article);

            // 3. 遍历标签名，查找或创建标签并绑定
            if (articleRequest.getTags() != null) {
                for (String tagName : articleRequest.getTags()) {
                    String tagId = tagService.findOrCreateTagByName(tagName);
                    articleTagListService.bindArticleToTag(article.getArticleId(), tagId);
                }
            }
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     *加载所有帖子
     * @return 返回所有帖子的列表
     */
    @GetMapping("/findAllArticle")
    public List<Article> findAllArticle(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Article> pageParam = new Page<>(page, size);
        IPage<Article> result = articleService.page(pageParam);
        return result.getRecords();
    }
    /**
     * 加载热门帖子（按点赞数降序）
     * @return 返回按点赞数排序的帖子的列表
     */
    @GetMapping("/findHotArticles")
    public List<Article> findHotArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Article> pageParam = new Page<>(page, size);

        // 创建 QueryWrapper，指定按点赞数排序
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("article_likes");

        IPage<Article> result = articleService.page(pageParam, queryWrapper);
        return result.getRecords();
    }
    /**
     * 通过id查询帖子并增加观看次数
     * @param articleId 帖子id
     * @return Article
     */
    @GetMapping("/findArticleById/{articleId}")
    public Article findArticleById(@PathVariable String articleId) {
        return articleService.IncreaseViews(articleId);
    }
    /**
     * 判断帖子点赞状态
     * @param articleId 帖子id
     * @param userId 用户id
     * @return 是否存在
     */
    @GetMapping("/hasLikedArticle")
    public boolean hasLikedArticle(@RequestParam String articleId, @RequestParam String userId) {
        return userArticleLikeService.exists(userId, articleId);
    }

    /**
     * 根据userId查找点赞过的贴子
     * @param userId
     * @return
     */
    @GetMapping("/findUserLikedArticle/{userId}")
    public List<Article> findUserLikedArticle(@PathVariable String userId) {
        try {
            // 调用 Service 层获取用户点赞的文章列表
            return userArticleLikeService.findLikedArticlesByUserId(userId);
        } catch (Exception e) {
            System.err.println("搜索用户点赞文章出错：" + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 根据关键词搜索帖子标题或内容
     * @param keyword 搜索关键词
     * @return 相关文章列表
     */
    @GetMapping("/searchArticles")
    public List<Article> searchArticles(@RequestParam String keyword) {
        try {

            return articleService.searchArticlesByKeyword(keyword);
        } catch (Exception e) {
            System.out.println("搜索文章时出错: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 用户点赞
     */
    @PostMapping("/userLikesArticle/{articleId}")
    private synchronized boolean userLikesArticle(@PathVariable String articleId, @RequestParam String userId) {
        try {
            // 判断是否已经点过赞
            boolean alreadyLiked = userArticleLikeService.exists(userId, articleId);
            if (alreadyLiked) return false; // 已经点过赞

            String likeId = UUID.randomUUID().toString();
            userArticleLikeService.insert(likeId,userId, articleId);

            articleService.incrementLikes(articleId);            // 更新文章点赞数
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * 用户取消点赞
     */
    @PostMapping("/userUnLikesArticle/{articleId}")
    private boolean userUnLikesArticle(@PathVariable String articleId, @RequestParam String userId) {
        try {
            // 删除点赞记录
            userArticleLikeService.delete(userId, articleId);

            // 更新文章点赞数
            articleService.decrementLikes(articleId);

            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    /**
     * 新增评论
     */
    @PostMapping("/userAddComment")
    public Comment userAddComment(@RequestBody Comment comment) {
        try {
            System.out.println(comment);
            comment.setCommentId(UUID.randomUUID().toString());
            comment.setCommentTime(LocalDateTime.now());
            comment.setCommentLikesTime(0);
            commentService.save(comment);
            return comment;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    /**
     * 获取帖子的评论
     */
    @GetMapping("/getCommentsByArticleId/{articleId}")
    public List<Comment> getCommentsByArticleId(@PathVariable String articleId) {
        try {
            List<Comment> comments = commentService.getCommentsByArticleId(articleId);
            System.out.println("获取该帖子"+articleId+"评论");
            return comments;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Collections.emptyList();
        }
    }
    /**
     * 举报
     */
    @PostMapping("/reportArticle")
    public Result<Report> reportArticle(@RequestBody Report report) {
        Report saved = reportService.submitReport(report);
        return Result.success(saved);
    }

    /**
     * 随机数验证码
     */
    private static String random3() {
        // jdk1.7的随机生成数，并发安全
        // nextInt(int origin, int bound) 范围：[origin,bound)
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    @GetMapping("/allUserName")
    public String index() {
        for (User user : userService.list()) {
            System.out.println(user.getUserName());
        }
        return "ok";
    }
}
