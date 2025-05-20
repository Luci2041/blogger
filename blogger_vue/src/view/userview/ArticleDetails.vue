<template>
  <div class="article-container">
    <div class="article-header">
      <el-button
          circle
          @click="goBack"
          class="back-button"
      >
        <el-icon>
          <ArrowLeft />
        </el-icon>
      </el-button>
      <!-- 作者头像和信息 -->
      <UserAvatar
          :userAvatar="authorInfo.userAvatar"
          :userDisplayName="authorInfo.userName || '未知用户'"
          :avatarSize="50"
          @avatarClick="() => handleAvatarClick(authorInfo.userId)"
          class="author-avatar"
      />
      <div class="author-meta">
        <div class="author-name">
          {{ authorInfo.userName || '未知用户' }}
        </div>
        <div class="post-time">
          发表于 {{ article.articlePostTime }}
        </div>
      </div>
    </div>

    <!-- 标题 -->
    <h1 class="article-title">{{ article.article_title }}</h1>

    <!-- 正文内容 -->
    <div class="article-content" v-html="safeHtml(article.article_context)"></div>

    <!-- 操作栏 -->
    <div class="article-actions">
      <!--        点赞-->
      <div
          class="article-actions-bubble"
          :class="{ liked: article.liked }"
          @click="toggleLike(article)"
      >
        <i class="iconfont icon-user-likes-post"></i>
        <span>{{ article.articleLikes }}</span>
      </div>

      <!-- 评论 -->
      <div class="article-actions-bubble" @click="scrollToCommentBox">
        <i class="iconfont icon-user-comment-post"></i>
        <span>{{ article.commentCount }}</span>
      </div>

      <el-popover
          v-model:visible="popoverVisible"
          placement="top"
          width="100"
          trigger="hover"
          :popper-class="'report-popover'"
      >
        <div class="report-popover-content">
          <el-icon class="el-icon warn" size="30">
            <WarnTriangleFilled />
          </el-icon>
          <el-button type="danger" @click="openReportDialog">
            举报
          </el-button>
        </div>
        <template #reference>
          <div class="article-actions-bubble">
            <el-icon><More/></el-icon>
          </div>
        </template>

      </el-popover>
    </div>

    <!-- 评论区域 -->
    <div class="comment-container">
      <div class="comment-box">
        <div class="comment-input-area">
          <textarea
              v-model="newComment"
              ref="commentInput"
              placeholder="请输入......"
              :rows="3"
              @paste="handlePaste"
          ></textarea>
          <button
              class="reply-btn"
              @click="userAddComment"
              :disabled="!newComment.trim()"
          >
            发表评论
          </button>
        </div>
      </div>

      <div class="comment-list">
        <div class="comment-item" v-for="comment in comments" :key="comment.id">
          <div class="comment-avatar-container">
            <UserAvatar
                :userAvatar="comment.userAvatar"
                :userDisplayName="comment.userName || '匿名'"
                :avatarSize="40"
                class="comment-avatar"
            />
            <div class="comment-meta">
              <div class="username">{{ comment.userName || '匿名' }}</div>
              <div class="time">发布于&nbsp;:&nbsp;{{ comment.commentTime }}</div>
            </div>
          </div>
          <div class="comment-content">
            {{ comment.content }}
          </div>
          <div class="comment-actions">
            <div
                class="comment-actions-bubble"
                :class="{ liked: comment.liked }"
                @click="toggleLike(comment)"
            >
              <i class="iconfont icon-user-likes-post"></i>
              <span>{{ comment.commentLikes }}</span>
            </div>
            <div class="comment-actions-bubble">
              <i class="iconfont icon-user-comment-post"></i>
              <span>回复</span>
            </div>
            <div class="comment-actions-bubble">
              <el-icon>
                <More/>
              </el-icon>
              <span></span>
            </div>
          </div>
        </div>
      </div>
      <el-dialog
          title="举报文章"
          v-model="reportDialogVisible"
          width="400px"
          :destroy-on-close="true"
      >
        <div>
          <div class="dialog-section-title">请选择举报原因（必选）：</div>
          <el-radio-group v-model="reportReason">
            <el-radio-button value="广告营销">广告营销</el-radio-button>
            <el-radio-button value="淫秽色情">淫秽色情</el-radio-button>
            <el-radio-button value="违法违规">违法违规</el-radio-button>
            <el-radio-button value="其他">其他</el-radio-button>
          </el-radio-group>
        </div>

        <!-- 可选项：补充说明 -->
        <div class="dialog-section" style="margin-top: 16px;">
          <div class="dialog-title">补充说明（可选）：</div>
          <el-input
              type="textarea"
              v-model="reportDetail"
              placeholder="请输入举报详情"
              rows="3"
          />
        </div>

        <template #footer>
          <el-button @click="reportDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReport">提交</el-button>
        </template>
      </el-dialog>
      <el-dialog
          v-model="previewVisible"
          width="60vw"
          :close-on-click-modal="true"
          :close-on-press-escape="true"
          :show-close="true"
          top="10vh"
      >
        <img
            :src="previewImageUrl"
            class="preview-image"
        />
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import UserAvatar from "../../components/UserAvatar.vue";
import {nextTick, onMounted, ref} from 'vue';
import {useRoute} from 'vue-router';
import axios from 'axios';
import {ElMessage} from "element-plus";
import {ArrowLeft, More, WarnTriangleFilled} from "@element-plus/icons-vue";

const articleId = useRoute().params.articleId;

const article = ref({
  articleLikes: 0,
  commentCount: 0,
  liked: false
});
const authorInfo = ref({});
const userInfo = ref(null);
const commentUserInfoMap = ref(null);

const newComment = ref('');
const comments = ref([]);

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

const toggleLike = async (item) => {
  try {
    const liked = item.liked
    const articleId = item.articleId  // 或者你用的是 item.id，看你的后端接口要什么

    const userId = localStorage.getItem('userId')
    if (!userId) {
      return ElMessage.error('请先登录')
    }

    const url = liked
        ? `/User/userUnLikesArticle/${articleId}`
        : `/User/userLikesArticle/${articleId}`

    const res = await api.post(url, null, { params: { userId } })

    if (res.data) {
      item.liked = !liked
      item.articleLikes += liked ? -1 : 1
    } else {
      console.error('点赞失败:', res)
    }
  } catch (err) {
    console.error('请求失败:', err)
  }
}

const commentInput = ref(null)
const scrollToCommentBox = async () => {
  const commentBox = document.querySelector('.comment-box')
  if (commentBox) {
    commentBox.scrollIntoView({ behavior: 'smooth' })
    await nextTick()
    commentInput.value && commentInput.value.focus()
  }
}

const handleReportArticle = () => {
  console.log('举报功能待开发');
};
const handlePaste = (e) => {
  const items = e.clipboardData?.items
  if (!items) return
  for (let i = 0; i < items.length; i++) {
    if (items[i].type.startsWith('image')) {
      e.preventDefault()
      ElMessage.error('评论区禁止发送图片')
      return
    }
  }
}
const userAddComment = async () => {
  if (!newComment.value) return;

  try {
    const res = await api.post('/User/userAddComment', {
      articleId,
      content: newComment.value,
      userId: localStorage.getItem("userId") || null
    });

    const newCommentData = res.data;
    await fetchCommentUserInfo(newCommentData);
    comments.value.push({...newCommentData, commentTime: formatDate(newCommentData.commentTime)});

    newComment.value = '';
  } catch (error) {
    console.error('发表评论失败:', error);
  }
};

const formatDate = (timestamp) => {
  const date = new Date(timestamp);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

const fetchCommentUserInfo = async (comment) => {
  try {
    const userRes = await api.get(`/User/findUserInfoByUserId/${comment.userId}`);
    comment.userAvatar = userRes.data.userAvatar;
    comment.userName = userRes.data.userName;
  } catch (error) {
    console.error(`获取评论者信息失败, commentId: ${comment.id}`, error);
  }
};

onMounted(async () => {
  const userIdLocal = localStorage.getItem('userId');
  window.__showImagePreview = (src) => {
    previewImageUrl.value = src;
    previewVisible.value = true;
  };
  await nextTick();

  document.querySelectorAll('.article-content img').forEach(img => {
    img.style.cursor = 'pointer';
    img.onclick = () => window.__showImagePreview(img.src);
  });


  try {
    const { data: art } = await api.get(`/User/findArticleById/${articleId}`);
    Object.assign(article.value, art);
    const userId = art.user_id;
    const { data: authorRes } = await api.get(`/User/findUserInfoByUserId/${userId}`);
    authorInfo.value = authorRes;
    article.value.articleLikes = art.articleLikes || 0;

    if (userIdLocal) {
      const { data: hasLiked } = await api.get('/User/hasLikedArticle', {
        params: { articleId, userId: userIdLocal }
      });
      article.value.liked = hasLiked;
    }

    const { data: commentList } = await api.get(`/User/getCommentsByArticleId/${articleId}`);

    article.value.commentCount = Array.isArray(commentList) ? commentList.length : 0;

    comments.value = await Promise.all(commentList.map(async c => {
      await fetchCommentUserInfo(c);
      return {
        ...c,
        commentTime: formatDate(c.commentTime),
        commentLikes: c.commentLikes || 0,
        liked: false
      };
    }));

  } catch (err) {
    console.error('加载详情页数据失败:', err);
  }
});

const popoverVisible = ref(false)
const reportDialogVisible = ref(false)
const reportReason = ref('')
const reportDetail = ref('')

const openReportDialog = () => {
  popoverVisible.value = false
  reportReason.value = ''
  reportDetail.value = ''
  reportDialogVisible.value = true
}

const submitReport = async () => {
  if (!reportReason.value) {
    return ElMessage.warning('请选择举报原因')
  }
  const userId = localStorage.getItem('userId')
  if (!userId) {
    return ElMessage.error('请先登录')
  }

  const payload = {
    articleId,
    // commentId: ,
    reporterId: userId,
    reason: reportReason.value,
    detail: reportDetail.value
  }

  try {
    const res = await api.post('/User/reportArticle', payload)
    if (res.data && res.data.code === 0) {
      ElMessage.success('举报提交成功，感谢反馈')
      reportDialogVisible.value = false
    } else {
      ElMessage.error(res.data.message || '举报失败，请重试')
    }
  } catch (err) {
    console.error('举报接口调用失败', err)
    ElMessage.error('网络异常，举报失败')
  }
}
const goBack = () => {
  window.history.back();
};
const imagePreviewUrl = ref('');

window.__showImagePreview = (src) => {
  previewImageUrl.value = src;
  previewVisible.value = true;
};

const previewVisible = ref(false);
const previewImageUrl = ref('');
const safeHtml = (html) => {
  // 用 DOMParser 过滤不安全标签后，再给所有 img 注入 onclick
  const doc = new DOMParser().parseFromString(html, 'text/html');
  return doc.body.innerHTML.replace(
      /<img(.*?)src="(.*?)"(.*?)>/g,
      (_, attr1, src, attr2) =>
          `<img${attr1} src="${src}"${attr2} style="cursor: zoom-in;" onclick="window.__showImagePreview('${src}')">`
  );
};
</script>


<style scoped>
.article-container {
  border: 0 solid red;
  overflow-y: auto;
  height: 91vh;
  box-sizing: border-box;
  padding: 20px 100px;
}

.article-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.author-avatar {
  margin-right: 15px;
  flex-shrink: 0;
}

.author-meta {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.author-name {
  font-weight: bold;
  font-size: 16px;
  color: #333;
}

.post-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.article-content {
  margin-top: 20px;
  line-height: 1.8;
  font-size: small;
}

.article-actions {
  display: flex;
  margin-top: 10px;
}

.article-actions-bubble {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background-color: #e4eaed;
  border-radius: 20px;
  font-size: 14px;
  color: #000;
  margin: 0 10px;
  cursor: pointer;

  &:hover {
    background-color: #dbe4e9;
  }
}

.article-actions-bubble.liked {
  background-color: #fd4500;
  color: white;
}

.article-action-bubble.liked .icon-user-likes-post {
  color: white;
}

.iconfont {
  font-size: 18px;
}

.comment-container {
  margin-top: 30px;
  padding: 20px;
  border-top: 1px solid #ccc;
}

.comment-box {
  width: 100%;
  display: flex;
  flex-direction: column;
}

.comment-input-area {
  display: flex;
  flex-direction: column;
}

.comment-box textarea {
  width: 100%;
  resize: vertical;
  padding: 10px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 6px;
  margin-bottom: 10px;
  box-sizing: border-box;
}

.comment-box .reply-btn {
  align-self: flex-end;
  padding: 8px 16px;
  background-color: #fd4500;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
  transition: background-color 0.2s;

  &:hover {
    background-color: #d83900;
  }
}

.comment-list {
  margin-top: 20px;
}

.comment-item {
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  display: flex;
  flex-direction: column;
}

.comment-avatar-container {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
}

.comment-avatar {
  margin-right: 15px;
  flex-shrink: 0;
}

.comment-meta {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.username {
  font-weight: bold;
  font-size: 14px;
}

.time {
  font-size: 12px;
  color: #999;
}

.comment-content {
  margin-top: 10px;
  margin-left: 10px;
  font-size: 14px;
}

.comment-actions {
  font-size: 12px;
  color: #888;
  margin-top: 5px;
  display: flex;
}

.comment-actions-bubble {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background-color: #fff;
  border-radius: 20px;
  font-size: 14px;
  color: #333;
  margin: 0 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.comment-actions-bubble:hover {
  background-color: #f3f5f7;
}

.comment-actions-bubble.liked {
  background-color: #fd4500;
  color: white;
}

.comment-actions-bubble.liked .icon-user-likes-post {
  color: white;
}

.reply-btn {
  margin-left: 15px;
  cursor: pointer;
  color: #007bff;
}

.reply-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;

  &:hover {
    background-color: #ccc;
  }
}

.report-btn {
  color: #f44336;
  font-weight: bold;
}

.report-btn:hover {
  background-color: #ffebeb;
}

.article-actions-bubble {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background-color: #e4eaed;
  border-radius: 20px;
  font-size: 14px;
  color: #000;
  cursor: pointer;
}
.article-actions-bubble:hover {
  background-color: #dbe4e9;
}
.dialog-section {
  margin-bottom: 12px;
}
.dialog-title {
  font-weight: bold;
  margin-bottom: 8px;
}

.el-icon.warn{
  color: #f36c6c;
}

.preview-image {
  width: 100%;
  height: auto;
  max-height: 80vh;   /* 防止太高撑破屏幕 */
  object-fit: contain;
}

.report-popover-content {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  height: 30px;
  gap: 10px;
}
.back-button{
  margin-right: 12px;
  background-color: #f5f5f5;
  border: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  align-items: center;
  justify-content: center;
  &:hover{
    background-color: #f5f5f5;
    color: #fd4500;
  }
}
</style>
