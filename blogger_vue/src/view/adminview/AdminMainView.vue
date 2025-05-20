<template>
  <div class="container">
    <!-- 登录页面 -->
    <div v-show="!loggedIn" class="login-card">
      <div class="login-title">
        <h2>管理员登录</h2>
      </div>
      <div class="login-form-wrapper">
        <el-form :model="adminLoginForm" label-width="80px" class="login-form">
          <!-- 用户名输入 -->
          <el-form-item label="用户名:" prop="adminUsername">
            <el-input
                v-model="adminLoginForm.adminUsername"
                placeholder="请输入用户名"
                autocomplete="off"
            />
          </el-form-item>

          <!-- 密码输入 -->
          <el-form-item label="密码:" prop="adminPassword">
            <el-input
                v-model="adminLoginForm.adminPassword"
                type="password"
                placeholder="请输入密码"
                autocomplete="off"
            />
          </el-form-item>

          <!-- 登录按钮 -->
          <el-form-item>
            <el-button type="primary" @click="adminLogin">登录</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 登录成功后的后台管理页面 -->
    <div v-show="loggedIn" class="admin-panel">
      <div class="admin-header">
        <h2>后台管理</h2>
      </div>
      <div class="admin-main">
        <div class="admin-side-margin"></div>
        <div class="admin-sidebar">
          <div class="sidebar-links">
            <router-link
                v-for="item in adminRoutes"
                :key="item.path"
                :to="item.path"
                class="sidebar-link"
                active-class="active-link"
            >
              <div class="sidebar-link-text">
                <i :class="item.icon"></i>&nbsp;&nbsp;{{ item.title }}
              </div>
            </router-link>
          </div>
        </div>
        <div class="admin-content">
          <router-view />
        </div>
        <div class="admin-side-margin"></div>
      </div>
      <div class="admin-footer">
        联系我们：liuxun347@gmail.com
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

// 配置axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

const adminLoginForm = ref({
  adminUsername: '',
  adminPassword: ''
})
const adminRoutes = ref([
  {
    path: '/admin/UserCenter',
    title: '用户管理',
  },
  {
    path: '/admin/ArticleCenter',
    title: '文章管理',
  },
  {
    path: '/admin/ReportCenter',
    title: '举报管理',
  }
])
const loggedIn = ref(false)

// 管理员登录方法
const adminLogin = async () => {
  try {
    // 发送登录请求
    const res = await api.post('/Admin/adminLogin', adminLoginForm.value)
    const admin = res.data

    // 根据后端返回的 adminId 判断登录成功
    if (!admin.adminId) {
      ElMessage.error('登录失败，请检查用户名或密码是否正确')
    } else {
      ElMessage.success('登录成功')
      loggedIn.value = true
      localStorage.setItem('adminId', admin.adminId)
      localStorage.setItem('adminUsername', admin.adminUsername)
    }
  } catch (err) {
    console.error('登录请求失败:', err)
    ElMessage.error('网络异常，请稍后重试')
  }
}
</script>


<style scoped>
.container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}


.login-card {
  width: 30%;
  height: 40%;
  border: 0 solid;
  margin: auto;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 0 10px #CCCCCC;
}

.login-title {
  text-align: center;
  border: 0 solid;
  margin-top: 30px;
}

.login-form-wrapper {
  width: 100%;
  height: 83%;
  border: 0 solid red;
  display: flex;
}

.login-form {
  margin: auto;
  border: 0 solid;
  display: flex;
  flex-direction: column;
  width: 500px;
}

.admin-panel {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.admin-header {
  flex: 0 0 60px;
  text-align: center;
  line-height: 60px;
  background-color: #e3e4e5;
}
.admin-main {
  flex: 1;
  display: flex;
}

.admin-side-margin {
  width: 5%;
}

.admin-sidebar {
  width: 15%;
  background-color: #f4f4f4;
  display: flex;
  flex-direction: column;
}
.active-link {
   background-color: #e0e0e0;
   border-left: 4px solid #409eff;
 }

.sidebar-links {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  border-right-width: 1px;
  user-select: none;
}

.sidebar-link {
  text-decoration: none;
  color: inherit;
  height: 60px;
  display: flex;
  align-items: center;
  border: 0 solid;
}

.sidebar-link-text {
  cursor: pointer;
  border: 0 solid;
  width: 100%;
  text-align: center;
}

.admin-content {
  width: 75%;
  height: 100%;
}

.admin-footer {
  flex: 0 0 40px;
  background-color: #e3e4e5;
  text-align: center;
  line-height: 40px;
  color: grey;
}
</style>
