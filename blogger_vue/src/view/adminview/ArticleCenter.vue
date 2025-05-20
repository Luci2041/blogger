<template>
  <div class="article-manage-container">
    <!-- 页面标题 -->
    <div class="article-manage-header">
      <div class="title">文章管理</div>
      <div class="search-area">
        <el-input
            v-model="searchId"
            placeholder="请输入文章ID"
            size="small"
            clearable
            style="width:200px; margin-right:8px"
        />
        <el-button type="primary" size="small" @click="searchArticle">搜索</el-button>
      </div>
    </div>

    <!-- 表格部分 -->
    <div class="article-manage-table">
      <el-table
          :data="allArticlesData"
          stripe
          style="width: 100%;"
          show-overflow-tooltip
          :row-style="{ height: '64px' }"
          :cell-style="{ textAlign: 'center', verticalAlign: 'middle' }"
          :header-cell-style="{ textAlign: 'center', verticalAlign: 'middle' }"
      >
        <!-- 文章ID -->
        <el-table-column prop="articleId" label="文章 ID" width="180"/>
        <!-- 发帖人ID -->
        <el-table-column prop="user_id" label="发帖人ID" width="180"/>
        <!-- 标题 -->
        <el-table-column prop="article_title" label="标题" width="240"/>
        <!-- 发布时间 -->
        <el-table-column prop="articlePostTime" label="发布时间" width="180"/>
        <!-- 点赞数 -->
        <el-table-column prop="articleLikes" label="点赞数" width="100"/>

        <!-- 操作 -->
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openDetailArticle(row.articleId)">查看详情</el-button>
          </template>

        </el-table-column>
      </el-table>
    </div>

    <!-- 查看详情对话框 -->
    <el-dialog
        title="文章详情"
        v-model="detailVisible"
        append-to-body
        width="600px"
    >
      <el-descriptions title="" :column="1" border :label-style="{ width: '120px', textAlign: 'right' }">
        <el-descriptions-item label="文章 ID">
          <div class="copy-line">
            <span>{{ articleDetail.articleId }}</span>
            <el-button
                type="text"
                icon="CopyDocument"
                size="small"
                @click="copyToClipboard(articleDetail.articleId)"
            >复制
            </el-button>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="发帖人 ID">
          <div class="copy-line">
            <span>{{ articleDetail.user_id }}</span>
            <el-button
                type="text"
                icon="CopyDocument"
                size="small"
                @click="copyToClipboard(articleDetail.user_id)"
            >复制
            </el-button>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="标题">{{ articleDetail.article_title }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ articleDetail.articlePostTime }}</el-descriptions-item>
        <el-descriptions-item label="点赞数">{{ articleDetail.articleLikes }}</el-descriptions-item>
        <el-descriptions-item label="浏览量">{{ articleDetail.articleViewTimes }}</el-descriptions-item>
        <el-descriptions-item label="收藏数">{{ articleDetail.articleCollectionTimes }}</el-descriptions-item>
        <el-descriptions-item label="标签">
          <template #default>
            <el-tag
                v-for="tag in articleTags"
                :key="tag.articleTagId"
                class="tag-item"
            >
              {{ tag.articleTagName }}
            </el-tag>
          </template>
        </el-descriptions-item>
        <el-descriptions-item label="内容">
          <div v-html="articleDetail.article_context" class="article-content"/>
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <span class="dialog-footer">
          <!-- 删除按钮 -->
          <el-button type="danger" @click="confirmDelete">删除文章</el-button>
          <!-- 关闭按钮 -->
          <el-button type="primary" @click="detailVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
import axios from 'axios'
import {ElMessage, ElMessageBox} from 'element-plus'

// axios 实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 数据与状态
const allArticlesData = ref([])
const detailVisible = ref(false)
const articleDetail = reactive({
  articleId: '',
  user_id: '',
  article_title: '',
  article_context: '',
  articlePostTime: '',
  articleLikes: 0,
  articleViewTimes: 0,
  articleCollectionTimes: 0
})
// 用于存储标签列表
const articleTags = ref([])

// 加载所有文章
const loadAllArticles = async () => {
  try {
    const res = await api.get('/Admin/allArticle')
    allArticlesData.value = res.data
    console.log(allArticlesData)
  } catch (err) {
    ElMessage.error('加载文章失败')
  }
}
const searchId = ref('')

// 点击搜索
const searchArticle = () => {
  if (!searchId.value) {
    ElMessage.warning('请输入文章ID')
    return
  }
  const found = allArticlesData.value.some(a => a.articleId === searchId.value)
  if (found) {
    openDetailArticle(searchId.value)
  } else {
    ElMessage.error('未找到对应文章')
  }
}
// 打开详情对话框，并获取标签
const openDetailArticle = async id => {
  const item = allArticlesData.value.find(a => a.articleId === id)
  if (!item) return
  Object.assign(articleDetail, item)
  detailVisible.value = true
  try {
    const tagRes = await api.get(`/Admin/articleTagsByArticle/${id}`)
    articleTags.value = tagRes.data
  } catch {
    articleTags.value = []
  }
}
const confirmDelete = async id => {
  try {
    await ElMessageBox.confirm(
        '此操作将永久删除该文章，是否继续？',
        '删除文章',
        { type: 'warning' }
    )
    await api.delete(`/Admin/article/${articleDetail.articleId}`)
    ElMessage.success('删除成功')
    detailVisible.value = false
    await loadAllArticles()       // 刷新列表
  } catch {
    if (err !== 'cancel') {
      console.error(err)
      ElMessage.error('删除失败，请重试')
    }
  }
}
const copyToClipboard = (text) => {
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('复制成功！')
  }).catch(() => {
    ElMessage.error('复制失败，请手动复制')
  })
}

onMounted(loadAllArticles)
</script>

<style scoped>
.article-manage-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px - 40px);
}


.article-manage-header {
  position: relative;
  height: 60px;
  border-bottom: 4px solid #494949;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #494949;
  font-size: larger;
  font-weight: bold;
}

.article-manage-header .search-area {
  position: absolute;
  right: 20px;
  display: flex;
  align-items: center;
}

.article-manage-table {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

::v-deep(.el-descriptions__label) {
  width: 120px;
  text-align: right;
  white-space: nowrap;
}

.tag-item {
  margin: 2px;
}

.article-content {
  max-height: 200px;
  overflow-y: auto;
}

.dialog-footer {
  text-align: right;
}
</style>
