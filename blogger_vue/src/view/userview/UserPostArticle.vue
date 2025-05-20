<template>
  <div class="main-container">
    <div class="userPostTitle">发帖内容：</div>
    <el-input
        v-model="title"
        class="title-input"
        placeholder="请输入标题"
        size="small"
        style="margin: 10px 0;"/>
    <!-- 标签输入 -->
    <div class="tags-wrapper">
      <el-input
          v-model="tagInput"
          size="small"
          placeholder="输入标签，回车添加"
          @keyup.enter.native="addTag"
          @blur="addTag"
          class="tag-input custom-padding"
          clearable
      />
      <div class="tags-list">
        <el-tag
            v-for="(tag, idx) in tags"
            :key="tag"
            closable
            @close="removeTag(idx)"
            class="tag-item"
        >
          {{ tag }}
        </el-tag>
      </div>
    </div>
    <div class="editor-wrapper">
      <div
          class="editable"
          contenteditable="true"
          ref="editor"
          placeholder="请输入内容..."
          @paste.prevent="pasting"
      ></div>
      <el-progress
          v-if="uploading"
          :text-inside="true"
          :stroke-width="18"
          :percentage="uploadPercent"
          style="margin-bottom: 12px;"
      />
      <el-button type="primary" @click="submitArticle" class="submitButton">发布文章</el-button>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue'
import axios from 'axios'
import {ElMessage} from "element-plus";


const title = ref('')
const editor = ref(null)

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})
const uploading = ref(false)      // 是否正在上传
const uploadPercent = ref(0)      // 当前上传进度 (0–100)

const url = '/User/upload/'

// 粘贴事件处理
const pasting = (event) => {
  const txt = event.clipboardData.getData('Text')
  const items = event.clipboardData.items
  console.log('粘贴的文本:', txt)

  if (typeof txt === 'string') {
    // textMsg.value += txt
    document.execCommand('insertText', false, txt)
  }
  console.log('粘贴的 items:', items)

  if (items.length) {
    for (let i = 0; i < items.length; i++) {
      if (items[i].type.indexOf('image') !== -1) {
        const file = items[i].getAsFile()
        console.log('粘贴的图片文件:', file)
        handleImageUpload(file)
        break
      }
    }
  }
}
//标签
const tagInput = ref('')
const tags = ref([])
// 添加标签
function addTag() {
  const t = tagInput.value
  if (t && !tags.value.includes(t)) {
    tags.value.push(t)
  }
  tagInput.value = ''
}

// 删除标签
function removeTag(idx) {
  tags.value.splice(idx, 1)
}
// 上传方法
const handleImageUpload = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  // console.log('上传的文件:', file)

  uploading.value = true
  uploadPercent.value = 0

  api.post(url, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: (progressEvent) => {
      // progressEvent.loaded: 已上传字节数
      // progressEvent.total: 总字节数
      if (progressEvent.total) {
        const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
        uploadPercent.value = percent
      }
    }
  }).then(res => {
    console.log('上传结果:', res.data)

    const imageUrl = res.data.data
    insertImageToEditor(imageUrl)
  }).catch(err => {
    console.error('上传失败:', err)
    ElMessage.error('图片上传失败')
  }).finally(() => {
    // 上传完毕隐藏进度条
    setTimeout(() => {
      uploading.value = false
      uploadPercent.value = 0
    }, 500)
  })
}
const insertImageToEditor = (url) => {
  const fullUrl = api.defaults.baseURL + url
  const img = document.createElement('img')
  img.src = fullUrl
  img.style.maxWidth = '100px'
  img.style.margin = '2px'
  editor.value.appendChild(img)
}


const submitArticle = async () => {
  const rawContent = editor.value.innerHTML.trim()

  const content = rawContent.replaceAll(api.defaults.baseURL, '')

  if (!title.value || !content) {
    ElMessage.warning("标题或内容不能为空");
    return
  }

  const articleData = {
    user_id: localStorage.getItem('userId'),
    article_title: title.value,
    article_context: content,
    article_post_time: new Date().toISOString(),
    tags: tags.value
  }

  try {
    const res = await api.post('/User/userPostArticle', articleData, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
    ElMessage.success('发布成功！')
    // 发布后可清空表单
    title.value = ''
    editor.value.innerHTML = ''
    tags.value = []
  } catch (error) {
    console.error('发布失败:', error)
    alert('发布失败，请检查后重试')
  }
}
</script>

<style scoped>
.main-container {
  max-width: 800px;
  margin: 40px auto;
  padding: 24px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.userPostTitle {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  user-select: none;
}
.tag-input {
  width: auto; /* 改为自动宽度 */
  min-width: 200px; /* 设置最小宽度 */
  flex-grow: 1; /* 允许扩展 */
  flex-shrink: 0;
}
:deep(.title-input .el-input__wrapper),
:deep(.tag-input   .el-input__wrapper) {
  border: 1px solid #dcdfe6 !important;
  border-radius: 8px !important;
  padding: 0 !important;
  background-color: transparent;
}

:deep(.title-input .el-input__inner),
:deep(.tag-input   .el-input__inner) {
  height: 45px;
  line-height: 45px;
  padding: 10px 14px;
  border: none;
  border-radius: 8px;
  background-color: #fff;
  font-size: 16px;
  color: #333;
}

:deep(.title-input .el-input__inner:focus),
:deep(.tag-input   .el-input__inner:focus) {
  border: 1px solid #fd4500 !important;
  box-shadow: 0 0 8px rgba(253, 69, 0, 0.5) !important;
}
:deep(.tag-input .el-input__inner) {
  padding: 0 10px !important; /* 统一内边距 */
}
:deep(.tag-input .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #fd4500 inset !important; /* 替换蓝色边框为橙色 */
  border: 1px solid #fd4500 !important;
}
.tags-list .tag-item {
  display: inline-flex;
  align-items: center;
  background-color: #faf5f0;
  border: 1px solid #fd4500;
  border-radius: 8px;
  padding: 2px 8px;
  margin: 4px 9px 0 0;
  color: #333;
  font-size: 14px;
}

:deep(.tag-item .el-tag__close) {
  color: #fd4500;
  margin-left: 4px;
}
:deep(.tag-item .el-tag__close:hover) {
  background-color: #fd4500;
  color: #fff;
  border-radius: 50%;
}
:deep(.el-input__wrapper) {
  outline: none !important;
}
.tags-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center; /* 新增 */
}

.editor-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex-grow: 1;
}

.editable {
  min-height: 200px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  padding: 10px 14px;
  background-color: #fafafa;
  font-size: 15px;
  line-height: 1.6;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
}
.editable:focus {
  border: 1px solid #fd4500 !important;
  box-shadow: 0 0 8px rgba(253, 69, 0, 0.5) !important;
}

.submitButton {
  align-self: flex-end;
  width: 120px;
  background-color: #fd4500;
  border: none;
}
.submitButton:hover {
  background-color: #d83900;
  color: #fff;
  transform: scale(1.03);
}
.submitButton:active {
  transform: scale(0.98);
}

.el-input {
  width: 100%;
}

.el-button {
  align-self: flex-end;
  width: 120px;
}

</style>
