<template>
  <div class="all-article-container" ref="scrollContainer">
    <div
        class="post-card"
        v-for="article in userArticles"
        :key="article.articleId"
    >
      <!-- 头部信息 -->
      <div class="post-header" @click="goToDetail(article.articleId)">
        <UserAvatar
            v-if="userInfoMap[article.user_id]"
            :userAvatar="userInfoMap[article.user_id]?.userAvatar"
            :userDisplayName="userInfoMap[article.user_id]?.userName || article.user_id"
            :avatarSize="50"
            @avatarClick="() => handleAvatarClick(article.user_id)"
            class="user_avatar"
        />
        <div class="post-meta">
          <span class="username">
            {{ userInfoMap[article.user_id]?.userName || article.user_id }}
          </span>
          <span class="time">发布时间: {{ article.articlePostTime }}</span>
        </div>
      </div>
      <!-- 帖子内容 -->
      <div class="post-box" @click="goToDetail(article.articleId)">
        <div class="post-title">{{ article.article_title }}</div>
        <div class="post-content collapsed" v-html="article.article_context"></div>
      </div>
      <!-- 操作栏 -->
      <div class="post-actions">
        <div class="post-actions-bubble">
          <i class="iconfont icon-user-likes-post"></i>
          <span>{{ article.articleLikes }}</span>
        </div>
        <div class="post-actions-bubble">
          <i class="iconfont icon-user-comment-post"></i>
          <span>评论</span>
        </div>
        <div class="post-actions-bubble">
          <i class="iconfont icon-user-share-post"></i>
          <span>分享</span>
        </div>
      </div>
    </div>
    <p v-if="userArticles.length === 0" class="no-data">你还没有发布任何文章。</p>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import axios from 'axios'
import {useRouter} from 'vue-router'
import UserAvatar from "../../components/UserAvatar.vue";

const userArticles = ref([])
const userInfoMap = ref({})
const userId = localStorage.getItem('userId')
const router = useRouter()
const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 获取当前用户发布的文章
async function fetchUserArticles() {
  try {
    // 拉取所有文章后过滤当前用户的
    const res = await api.get('/User/findAllArticle', {params: {page: 1, size: 1000}})
    userArticles.value = (res.data || []).filter(a => a.user_id === userId)
    // 加载用户信息
    for (const article of userArticles.value) {
      await fetchUserInfo(article.user_id)
    }
  } catch (err) {
    console.error('获取用户文章失败:', err)
  }
}

async function fetchUserInfo(uid) {
  if (userInfoMap.value[uid]) return
  try {
    const res = await api.get(`/User/findUserInfoByUserId/${uid}`)
    userInfoMap.value[uid] = res.data
  } catch (err) {
    console.error(`获取用户信息失败 uid=${uid}:`, err)
  }
}

function goToDetail(articleId) {
  router.push(`/ArticleDetails/${articleId}`)
}

function handleAvatarClick(uid) {
  router.push(`/userInfo`)
}

onMounted(fetchUserArticles)
</script>

<style scoped>
.all-article-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  height: 93vh;
  overflow-y: auto;
  box-sizing: border-box;
}

.post-card {
  width: 50vw;
  max-width: 800px;
  border-radius: 10px;
  padding: 30px;
  margin: 0 auto 20px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.post-card:hover {
  background-color: #f4f6f7;
}

.post-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.user_avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.post-meta {
  display: flex;
  flex-direction: column;
  font-size: 14px;
}

.username {
  font-weight: bold;
}

.time {
  color: grey;
}

.post-box {
  padding: 10px 0;
}

.post-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  border-bottom: 1px solid #ccc;
  padding-bottom: 4px;
}

.post-content.collapsed {
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 6;
  -webkit-box-orient: vertical;
  text-overflow: ellipsis;
  line-height: 1.5;
}

.post-actions {
  display: flex;
  margin-top: 10px;
}

.post-actions-bubble {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background-color: #e4eaed;
  border-radius: 20px;
  font-size: 14px;
  margin-right: 10px;
}

.no-data {
  text-align: center;
  color: #999;
  margin-top: 20px;
}
</style>
