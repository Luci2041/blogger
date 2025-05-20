<template>
  <div class="all-article-container">
    <div class="post-card"
         v-for="article in articles"
         :key="article.articleId"
    >
      <!-- 头部信息 -->
      <div class="post-header" @click="goToDetail(article.articleId)">
        <UserAvatar
            v-if="userInfoMap[article.user_id]"
            :userAvatar="userInfoMap[article.user_id]?.userAvatar"
            :userDisplayName="userInfoMap[article.user_id]?.userName  || article.user_id"
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
        <div
            class="post-content collapsed"
            v-html="article.article_context"
        ></div>
      </div>

      <!-- 操作栏 -->
      <div class="post-actions">
        <!--        点赞-->
        <div
            class="post-actions-bubble"
            :class="{ liked: article.liked }"
            @click="toggleLike(article)"
        >
          <i class="iconfont icon-user-likes-post"></i>
          <span>{{ article.articleLikes }}</span>
        </div>

        <!-- 评论数 -->
        <div class="post-actions-bubble">
          <i class="iconfont icon-user-comment-post"></i>
          <span>{{ article.commentCount }}</span>
        </div>
        <div
            class="post-actions-bubble"
            @click.stop="shareArticle(article.articleId)"
        >
          <i class="iconfont icon-user-share-post"></i>
          <span>分享</span>
        </div>
      </div>
    </div>
    <div v-if="noMoreData" class="no-more">没有更多文章了...</div>
  </div>
</template>

<script setup>
import axios from 'axios'
import {ref, onMounted, computed, onBeforeUnmount} from 'vue'
import UserAvatar from "../../components/UserAvatar.vue";
import {useArticleStore} from '../../stores/articleStore'
import {watch} from 'vue'

const articleStore = useArticleStore()
const articles = computed(() => articleStore.articles)       // 所有文章
const page = ref(1)            // 当前页
const pageSize = 10            // 每页加载条数
const loading = ref(false)     // 是否正在加载
const noMoreData = ref(false)  // 是否没有更多数据
const scrollContainer  = ref(null) // 滚动容器引用
const userInfoMap = ref({})    // 存放 user_id -> 用户信息
const userId = localStorage.getItem('userId')
const route = useRoute()
const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

const loadArticles = async () => {
  // 如果正在加载或已经到最后一页，就不再请求
  if (loading.value || noMoreData.value) return
  loading.value = true

  try {
    // 请求后端分页接口
    const res = await api.get('/User/findHotArticles', {
      params: {
        page: page.value,
        size: pageSize
      }
    })
    const articlesRaw = res.data || []
    const userIdLocal = localStorage.getItem('userId')

    if (articlesRaw.length < pageSize) {
      noMoreData.value = true
    }

    const existingIds = new Set(articles.value.map(a => a.articleId))
    const uniqueRaw = articlesRaw.filter(a => !existingIds.has(a.articleId))

    const newArticles = await Promise.all(
        uniqueRaw.map(async (article) => {
          // 默认未点赞
          let liked = false
          if (userIdLocal) {
            try {
              const { data: hasLiked } = await api.get('/User/hasLikedArticle', {
                params: { articleId: article.articleId, userId: userIdLocal }
              })
              liked = hasLiked
            } catch (err) {
              console.warn(`获取点赞状态失败 articleId=${article.articleId}`, err)
            }
          }

          // 默认评论数为 0
          let commentCount = 0
          try {
            const { data: comments } = await api.get(`/User/getCommentsByArticleId/${article.articleId}`)
            commentCount = Array.isArray(comments) ? comments.length : 0
          } catch (err) {
            console.warn(`获取评论数量失败 articleId=${article.articleId}`, err)
          }

          return {
            ...article,
            liked,
            commentCount
          }
        })
    )

    // 将新文章追加到列表中
    articles.value.push(...newArticles)

    // 为新文章异步获取作者信息
    for (const art of newArticles) {
      await fetchUserInfo(art.user_id)
    }

    // 分页自增
    page.value++
  } catch (err) {
    console.error('加载文章失败:', err)
  } finally {
    loading.value = false
  }
}


const fetchUserInfo = async (userId) => {
  if (userInfoMap.value[userId]) return

  try {
    const res = await api.get(`/User/findUserInfoByUserId/${userId}`)
    if (res.data) {
      userInfoMap.value[userId] = res.data
      // console.log(userInfoMap)
    }
  } catch (err) {
    console.error(`获取用户 ${userId} 信息失败:`, err)
  }
}
const handleAvatarClick = (userId) => {
  console.log('点击了用户头像:', userId)
}
const toggleLike = async (article) => {
  try {
    const liked = article.liked

    const url = liked
        ? `/User/userUnLikesArticle/${article.articleId}`
        : `/User/userLikesArticle/${article.articleId}`

    const res = await api.post(url, null, {
      params: {
        userId: userId
      }
    })

    if (res.data) {
      article.liked = !liked
      article.articleLikes += liked ? -1 : 1
    } else {
      console.error('点赞失败:', res)
    }
  } catch (err) {
    console.error('请求失败:', err)
  }
}
function shareArticle(articleId) {
  const url = window.location.origin + `/ArticleDetails/${articleId}`

  navigator.clipboard.writeText(url)
      .then(() => {
        ElMessage.success('已成功将链接复制到剪贴板')
      })
      .catch(() => {
        ElMessage.error('复制失败，请手动复制链接')
      })
}

import {useRoute, useRouter} from 'vue-router'
import {ElMessage} from "element-plus";

const router = useRouter()

const goToDetail = (articleId) => {
  router.push(`/ArticleDetails/${articleId}`)
}
// 滚动到底部时触发加载
function onScroll() {
  const el = scrollContainer.value
  if (el && el.scrollTop + el.clientHeight >= el.scrollHeight - 10) {
    loadArticles()
  }
}

// 初始加载
onMounted(() => {
  // 绑定滚动事件
  scrollContainer.value = document.querySelector('.all-article-container')
  scrollContainer.value?.addEventListener('scroll', onScroll)
  // 首次加载
  loadArticles()

  if (!articleStore.isSearchMode) {
    page.value = 1
    noMoreData.value = false
    articleStore.clearArticles()
    loadArticles()
  }
})
onBeforeUnmount(() => {
  const el = scrollContainer.value
  if (el) {
    el.removeEventListener('scroll', onScroll)
  }
  articleStore.clearArticles()
})
watch(
    () => articles.value,
    (newList) => {
      newList.forEach(article => {
        fetchUserInfo(article.userId)
      })
    },
    {immediate: true} // 组件挂载时也会执行一次
)
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
  margin: 0 auto 20px;
  padding: 16px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  background-color: #fff;
  transition: transform .2s, box-shadow .2s;

  &:hover {
    transform: scale(1.01);
    cursor: pointer;
    background-color: #f4f6f7ff;
  }
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
  border: 0 solid #000;
  padding: 10px;
  margin: 0;
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
  color: #000;
  margin: 0 10px;
  cursor: pointer;

  &:hover {
    background-color: #dbe4e9;
  }
}

.icon-user-likes-post,
.icon-user-comment-post,
.icon-user-share-post {
  color: #0b0b0b;
}

.post-actions-bubble.liked {
  background-color: #fd4500;
  color: #fff;
}

.liked .icon-user-likes-post {
  color: #fff;
}


</style>
