<template>
  <div class="user-manage-container">
    <div class="user-manage-content">
      <!-- 页面标题 -->
      <div class="user-manage-header" >
        <div class="title">用户管理</div>
        <div class="search-area">
          <el-input
              v-model="searchId"
              placeholder="请输入用户ID"
              size="small"
              style="width: 200px; margin-right: 10px;"
              clearable
          />
          <el-button type="primary" size="small" @click="searchUser">搜索</el-button>
        </div>
      </div>

      <!-- 表格部分 -->
      <div class="user-manage-table">
        <el-table
            :data="allUserData"
            stripe
            style="width: 100%;"
            show-overflow-tooltip
            :row-style="{ height: '64px' }"
            :cell-style="{ textAlign: 'center', verticalAlign: 'middle' }"
            :header-cell-style="{ textAlign: 'center', verticalAlign: 'middle' }"
        >
          <el-table-column prop="userId" label="用户id" width="200" />
          <el-table-column prop="userName" label="用户名" width="140" />

          <el-table-column prop="userAvatar" label="用户头像" width="120">
            <template #default="scope">
              <!-- 如果有头像地址，显示图片 -->
              <div v-if="scope.row.userAvatar">
                <img
                    :src="scope.row.userAvatar"
                    class="user-avatar"
                    alt="用户头像"
                />
              </div>
              <!-- 否则显示占位文字 -->
              <div v-else class="user-avatar-placeholder">
                用户暂未设置
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="userPhone" label="联系电话" width="140">
            <template #default="scope">
              {{ showOrDefault(scope.row.userPhone) }}
            </template>
          </el-table-column>

          <el-table-column prop="userSex" label="性别" width="110">
            <template #default="scope">
              {{ showOrDefault(scope.row.userSex) }}
            </template>
          </el-table-column>

          <el-table-column prop="userEmail" label="邮箱地址" width="260">
            <template #default="scope">
              {{ showOrDefault(scope.row.userEmail) }}
            </template>
          </el-table-column>

          <el-table-column prop="userStatus" label="状态" width="150">
            <template #default="scope">
              <span v-if="Number(scope.row.userStatus) === 1" class="status-normal">正常</span>
              <span v-else-if="Number(scope.row.userStatus) === 3" class="status-banned">封禁中</span>
              <span v-else class="status-unknown">未知状态</span>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button
                  v-if="Number(scope.row.userStatus) === 1"
                  @click="banUser(scope.row.userId)"
                  type="primary"
                  size="small"
                  class="btn-ban"
              >
                封禁
              </el-button>
              <el-button
                  v-else-if="Number(scope.row.userStatus) === 3"
                  @click="unBanUser(scope.row.userId)"
                  type="primary"
                  size="small"
              >
                解封
              </el-button>
            </template>
          </el-table-column>

        </el-table>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import {useRouter} from "vue-router";

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

const allUserData = ref([])
const showOrDefault = (value) => {
  return value ? value : '用户暂未设置'
}
const loadAllUsers = () => {
  api.get('/Admin/allUser')
      .then(res => {
        console.log(res.data)
        allUserData.value = res.data
      })
      .catch(err => {
        console.error('加载用户失败', err)
      })
}
const searchId = ref('');
const router = useRouter();
const searchUser = async () => {
  if (!searchId.value) {
    ElMessage.warning('请输入用户ID');
    return;
  }

  try {
    // 假设你有一个API接口，通过ID查找用户
    const response = await fetch(`/api/user/${searchId.value}`);
    if (response.ok) {
      const user = await response.json();
      if (user && user.id) {
        // 跳转到用户详情页，假设路由是 /user/:id
        await router.push(`/user/${user.id}`);
      } else {
        ElMessage.error('未找到对应用户');
      }
    } else {
      ElMessage.error('未找到对应用户');
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('搜索过程中发生错误');
  }
};
const banUser = (uid) => {
  api.put(`/Admin/banUser/${uid}`)
      .then(res => {
        if (res.data === 200) {
          ElMessage.success('用户封禁成功')
          loadAllUsers()
        } else {
          ElMessage.error('封禁失败')
        }
      })
}

const unBanUser = (uid) => {
  api.put(`/Admin/unBanUser/${uid}`)
      .then(res => {
        if (res.data === 200) {
          ElMessage.success('用户解封成功')
          loadAllUsers()
        } else {
          ElMessage.error('解封失败')
        }
      })
}

onMounted(() => {
  loadAllUsers()
})
</script>


<style scoped>

.user-manage-container {
  width: 100%;
}

.user-manage-content {
  width: 100%;
}

.user-manage-header {
  border-bottom: 4px solid #494949;
  color: #494949;
  font-size: 20px;
  font-weight: bold;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}
.title {
  font-size: 20px;
  font-weight: bold;
}
.search-area {
  position: absolute;
  right: 20px;
  display: flex;
  align-items: center;
}

.user-manage-table {
  width: 95%;
  height: 88%;
  display: flex;
  justify-content: center;
  margin: 0 auto;
}
.user-table .el-table__cell {
  padding-top: 12px;
  padding-bottom: 12px;
}
.user-avatar {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 50%;
}

.status-normal {
  border: 1px solid #adff30;
  color: #adff30;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.status-banned {
  border: 1px solid red;
  color: red;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.btn-ban {
  color: #f23606;
  background-color: white;
  border: red;
  &:hover{
    background-color: red;
    color: white;
  }
}
</style>
