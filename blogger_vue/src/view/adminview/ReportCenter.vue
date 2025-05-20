<template>
  <div class="report-manage-container">
    <!-- 页面标题 -->
    <div class="report-manage-header">
      举报管理
    </div>

    <!-- 表格部分 -->
    <div class="report-manage-table">
      <el-table
          :data="reportsData"
          stripe
          style="width: 100%;"
          show-overflow-tooltip
          :row-style="{ height: '64px' }"
          :cell-style="{ textAlign: 'center', verticalAlign: 'middle' }"
          :header-cell-style="{ textAlign: 'center', verticalAlign: 'middle' }"
      >
        <!-- 举报ID -->
        <el-table-column prop="id" label="举报 ID" width="180" />
        <!-- 文章ID -->
        <el-table-column
            prop="articleId"
            label="文章 ID"
            width="180"
        />
        <!-- 评论ID -->
        <el-table-column
            prop="commentId"
            label="评论 ID"
            width="180"
        />
        <!-- 举报人ID -->
        <el-table-column
            prop="reporterId"
            label="举报人 ID"
            width="180"
        />
        <!-- 原因 -->
        <el-table-column prop="reason" label="原因" width="120" />
        <!-- 状态 -->
        <el-table-column prop="status" label="状态" width="120" />
        <!-- 举报时间 -->
        <el-table-column
            prop="createTime"
            label="举报时间"
            width="180"
        />
        <!-- 更新时间 -->
        <el-table-column
            prop="updateTime"
            label="处理时间"
            width="180"
        />
        <!-- 操作 -->
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button
                type="primary"
                size="small"
                @click="openDetailReport(row)"
            >
              查看详情
            </el-button>
            <el-button
                type="success"
                size="small"
                :disabled="row.status !== 'PENDING'"
                @click="changeStatus(row.id, 'RESOLVED')"
            >
              处理
            </el-button>
            <el-button
                type="danger"
                size="small"
                :disabled="row.status !== 'PENDING'"
                @click="changeStatus(row.id, 'REJECTED')"
            >
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 查看详情对话框 -->
    <el-dialog
        title="举报详情"
        v-model="detailVisible"
        append-to-body
        width="600px"
    >
      <el-descriptions title="" :column="1" border>
        <el-descriptions-item label="举报 ID">{{ reportDetail.id }}</el-descriptions-item>
        <el-descriptions-item label="文章 ID">
          <div style="display: flex; align-items: center;">
            <span>{{ reportDetail.articleId }}</span>
            <el-button
                type="primary"
                size="small"
                text
                icon="CopyDocument"
                style="margin-left: 8px;"
                @click="copyToClipboard(reportDetail.articleId)"
            >复制</el-button>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="评论 ID">{{ reportDetail.commentId }}</el-descriptions-item>
        <el-descriptions-item label="举报人 ID">
          <div style="display: flex; align-items: center;">
            <span>{{ reportDetail.reporterId }}</span>
            <el-button
                type="primary"
                size="small"
                text
                icon="CopyDocument"
                style="margin-left: 8px;"
                @click="copyToClipboard(reportDetail.reporterId)"
            >复制</el-button>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="原因">{{ reportDetail.reason }}</el-descriptions-item>
        <el-descriptions-item label="补充说明">
          <div class="report-detail">{{ reportDetail.detail }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="状态">{{ reportDetail.status }}</el-descriptions-item>
        <el-descriptions-item label="举报时间">{{ reportDetail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ reportDetail.updateTime }}</el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="detailVisible = false"
          >关闭</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// axios 实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 数据与状态
const reportsData = ref([])
const detailVisible = ref(false)
const reportDetail = reactive({
  id: '',
  articleId: '',
  commentId: '',
  reporterId: '',
  reason: '',
  detail: '',
  status: '',
  createTime: '',
  updateTime: ''
})

// 加载待处理举报
const loadReports = async () => {
  try {
    const res = await api.get('/Admin/pending', {
      params: { page: 0, size: 20 }
    })
    // 假设后端返回 { code:0, data:{ items:[], total:..., page:..., size:... }}
    const { items } = res.data.data
    reportsData.value = items
  } catch (err) {
    ElMessage.error('加载举报列表失败')
  }
}

// 打开详情对话框
const openDetailReport = (row) => {
  Object.assign(reportDetail, row)
  detailVisible.value = true
}

// 修改举报状态
const changeStatus = async (id, status) => {
  try {
    await api.put(`/Admin/${id}/status`, null, {
      params: { status }
    })
    ElMessage.success(status === 'RESOLVED' ? '已通过' : '已拒绝')
    loadReports()
  } catch (err) {
    ElMessage.error('操作失败')
  }
}
const copyToClipboard = (text) => {
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('复制成功！')
  }).catch(() => {
    ElMessage.error('复制失败，请手动复制')
  })
}

onMounted(loadReports)
</script>

<style scoped>
.report-manage-container {
  width: 100%;
  height: calc(100vh - 60px - 40px);
  display: flex;
  flex-direction: column;
}

.report-manage-header {
  border-bottom: 4px solid #494949;
  color: #494949;
  font-size: larger;
  font-weight: bold;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.report-manage-table {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

.report-detail {
  max-height: 150px;
  overflow-y: auto;
  white-space: pre-wrap;
}

.dialog-footer {
  text-align: right;
}
</style>
