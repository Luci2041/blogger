<template>
  <div class="main-container">
    <div class="main-body">
      <div class="main-left">
        <div class="userInfo-top">
          <div class="top-left">
            <UserAvatar
                :userAvatar="userInfo?.userAvatar"
                :userDisplayName="userInfo?.userName || userEmail"
                @avatarClick="handleAvatarClick"
                :avatarSize = 100
                class="user-avatar"
            />
          </div>
          <div class="top-right">
            <div class="userInfo-top top-right userName">
              用户名：{{userInfo?.userName || '未获取到邮箱' }}
            </div>
          </div>
        </div>
        <div class="userInfo-title">
          基本信息
        </div>
        <div class="userInfo-main">
          <div class="userInfo-details" @click="openEditDialog('userName')">
            <div class="userInfo-details-title">用户名</div>
            <div class="userInfo-details-value">{{userInfo?.userName}}</div>
            <div class="change-userInfo-button"><el-icon><ArrowRight /></el-icon></div>
          </div>
          <div class="userInfo-details" @click="openEditDialog('userEmail')">
            <div class="userInfo-details-title">邮箱</div>
            <div class="userInfo-details-value">{{userInfo?.userEmail}}</div>
            <div class="change-userInfo-button"><el-icon><ArrowRight /></el-icon></div>
          </div>
          <div class="userInfo-details" @click="openEditDialog('userSex')">
            <div class="userInfo-details-title">性别</div>
            <div class="userInfo-details-value">{{userInfo?.userSex}}</div>
            <div class="change-userInfo-button"><el-icon><ArrowRight /></el-icon></div>
          </div>
        </div>
      </div>
      <div class="main-right">

      </div>
    </div>
  </div>
  <!-- 头像上传的弹窗部分 -->
  <el-dialog
      title="修改头像"
      v-model="avatarDialogVisible"
      width="400px"
      center
  >
    <div style="text-align:center">
      <img v-if="previewUrl" :src="previewUrl" alt="预览头像" style="width:100px;height:100px;border-radius:50%;margin-bottom:20px;">
    </div>

    <div style="text-align:center;margin-bottom:20px;">
      <el-button @click="triggerFileInput">选择图片</el-button>
      <input type="file" ref="fileInput" style="display: none;" @change="handleFileChange" accept="image/*" />
    </div>

    <template #footer>
      <el-button @click="avatarDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="uploadAvatar" :disabled="!selectedFile">上传</el-button>
    </template>
  </el-dialog>
<!--  编辑资料的弹窗部分-->
  <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="400px"
      center
  >
    <el-form-item :label="dialogLabel">
      <el-select v-if="editingField === 'userSex'" v-model="editForm.userSex" placeholder="请选择性别">
        <el-option label="男" value="男" />
        <el-option label="女" value="女" />
        <el-option label="保密" value="保密" />
      </el-select>
      <el-input v-else v-model="editForm[editingField]" />
    </el-form-item>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="saveEdit">确定</el-button>
    </template>
  </el-dialog>

</template>

<script setup>
import {onMounted, reactive, ref} from "vue";
import axios from "axios";
import UserAvatar from "../../components/UserAvatar.vue";
import {ArrowRight} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { useUserStore } from '../../stores/userStore.js'

const userStore = useUserStore()
const userInfo = ref({
  userName:"",
  userEmail:"",
  userSex:"",
});
const userEmail = localStorage.getItem("userEmail");
const userId = localStorage.getItem("userId")

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

const dialogVisible = ref(false);
const avatarDialogVisible = ref(false);

const editingField = ref("");
const editForm = reactive({
  userName: "",
  userEmail: "",
  userSex: ""
});
const dialogTitle = ref("修改信息");
const dialogLabel = ref("");

const selectedFile = ref(null);
const previewUrl = ref("");
const fileInput = ref(null);

onMounted(async () => {
  try {
    await findUserInfoByUserId();
  } catch (err) {
    console.error('初始化失败:', err);
  }
});
const findUserInfoByUserId = async () => {
  try {
    if (!userId) {
      console.error('localStorage为空，是否登录？');
      return;
    }
    const response = await api.get('/User/findUserInfoByUserId/' + userId);
    console.log("用户进入个人信息页面获取信息：",response.data);
    userInfo.value = response.data;
    editForm.userName = response.data.userName;
    editForm.userEmail = response.data.userEmail;
    editForm.userSex = response.data.userSex;
  } catch (err) {
    console.error('请求失败:', err);
    throw err;
  }
}

const openEditDialog = (field) => {
  editingField.value = field;
  if (field === 'userName') {
    dialogTitle.value = "修改用户名";
    dialogLabel.value = "用户名";
  } else if (field === 'userEmail') {
    dialogTitle.value = "修改邮箱";
    dialogLabel.value = "邮箱";
  } else if (field === 'userSex') {
    dialogTitle.value = "修改性别";
    dialogLabel.value = "性别";
  }
  editForm[field] = userInfo.value[field];
  dialogVisible.value = true;
};

const saveEdit = async () => {
  try {
    const updateData = {
      [editingField.value]: editForm[editingField.value]
    };
    console.log("修改",[editingField.value],"的信息为", editForm[editingField.value])
    console.log(updateData)
    const response = await api.post('/User/updateUserInfoByUserId/' + userId, updateData);
    if (response.data === true) {
      userInfo.value[editingField.value] = editForm[editingField.value];
      ElMessage.success("修改成功");

    } else {
      ElMessage.error("修改失败");
    }
  } catch (err) {
    console.error('修改请求失败:', err);
    ElMessage.error("修改请求失败");
  } finally {
    dialogVisible.value = false;
  }
};
const handleAvatarClick = () => {
  console.log('点击头像');
  avatarDialogVisible.value = true;
};
// 触发隐藏的文件选择器
const triggerFileInput = () => {
  fileInput.value.click();
};

// 选择图片后，生成本地预览
const handleFileChange = (e) => {
  const file = e.target.files[0];
  if (file) {
    selectedFile.value = file;
    previewUrl.value = URL.createObjectURL(file);
  }
};
const uploadAvatar = async () => {
  if (!selectedFile.value) {
    ElMessage.error("请先选择图片");
    return;
  }
  const formData = new FormData();
  formData.append('file', selectedFile.value);

  try {
    const uploadResponse = await api.post('/User/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    if (uploadResponse.data && uploadResponse.data.data) {
      const avatarUrl = uploadResponse.data.data;

      const updateResponse = await api.post('/User/updateUserInfoByUserId/' + userId, {
        userAvatar: avatarUrl
      });

      if (updateResponse.data === true) {
        userInfo.value.userAvatar = avatarUrl;
        userStore.setAvatar(avatarUrl)
        ElMessage.success('头像上传并更新成功');
        avatarDialogVisible.value = false;
      } else {
        ElMessage.error('头像上传成功，但更新信息失败');
      }
    } else {
      ElMessage.error('上传头像失败');
    }
  } catch (err) {
    console.error('上传失败:', err);
    ElMessage.error('上传失败');
  }
};
</script>

<style>
.main-container {
  display: flex;
  border: 0 solid red;
  box-sizing: border-box;
  width: 100%;
  height: 100%;
  padding: 20px;
  justify-content: center;
}

.main-body {
  width: 100%;
  border: 0 solid yellow;
  max-width: 1200px;
  gap: 20px;
  display: flex;

}

.userInfo-title{
  border: 0 solid gold;
  padding: 10px;
  font-weight: 600;
  font-size: 1.5rem;
  line-height: 2.25rem;
  border-bottom: 1px solid #e5e5e5;
  user-select: none;
}

.userInfo-main{
  border: 0 solid red;
  display: flex;
  flex-direction: column;
  padding: 10px;
}

.userInfo-details{
  border: 0 solid yellow;
  display: flex;
  padding: 10px;
  margin-bottom: 5px;
  align-items: center;
  box-sizing: border-box;
  cursor: pointer;
  justify-content: space-between;
  &:hover {
    .change-userInfo-button {
      background-color: #dae3e8;
    }
  }
}

.userInfo-details-title{
  width: 56px;
  padding-left: 16px;
  box-sizing: content-box;
  text-align-last: justify;
  margin-right: 20px;
  user-select: none;
  flex-shrink: 0;
}

.userInfo-details-value{
  flex-grow: 1;
  text-align: right;
  padding-right: 20px;
}

.change-userInfo-button{
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  flex-shrink: 0;
  transition: background-color 0.2s;
  box-shadow: 0 0 6px rgba(0, 0, 0, 0.1);
}

.change-userInfo-button:hover {
  background-color: #dae3e8; /* 悬停效果 */
}

.main-left {
  flex: 1;
  min-width: 0;
  border: 0 solid blue;

}

.main-right {
  width: 30%;
  border: 0 solid green;
}

.userInfo-top {
  border: 0 solid black;
  width: 100%;
  box-sizing: border-box;
  padding: 30px;
  display: flex;
  gap: 20px;
}

.top-left{
  border: 0 solid red;
  display: flex;
  align-items: center;
  justify-content: center;
}

.top-right{
  border: 0 solid blue;
}

.top-left .user-avatar {
  width: 70px;
  height: 70px;
}

.userName{
  font-size: 0.9rem;
  border: 0 solid yellow;
  user-select: none;
}

@media (max-width: 768px) {
  .main-body {
    flex-direction: column;
  }

  .main-right {
    display: none;
  }
}
</style>