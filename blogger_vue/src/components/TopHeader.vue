<template>
  <el-container class="container">
    <el-header class="top_menu">
      <!-- LOGO -->
      <div class="logo">
        <p title="回到首页" class="p">
          <router-link to="/" @click="$emit('select-main', 'home')" class="text">LXBlogger</router-link>
        </p>
      </div>
      <!-- 搜索框 -->
      <div class="search_box">
        <el-input
            v-model="searchInput"
            placeholder="Search LXBlogger"
            :clearable="true"
            class="search_frame"
        ></el-input>
        <el-button class="search_button" @click="$emit('search', searchInput)">搜索</el-button>
      </div>
      <!-- 登录按钮 / 用户头像 -->
      <div class="avatar_box">
        <el-button
            v-if="!isLoggedIn"
            class="login_button"
            @click="$emit('open-login-dialog')"
        >
          Log In
        </el-button>
        <UserAvatar
            v-else
            :userAvatar="userAvatar"
            :userEmail="userEmail"
            :avatarSize="40"
            @avatarClick="$emit('avatar-click')"
            class="user_avatar"
        />
      </div>
    </el-header>
  </el-container>
</template>

<script setup>
import {ref} from 'vue';
import UserAvatar from "./UserAvatar.vue";

defineProps({
  isLoggedIn: Boolean,
  userAvatar: String,
  userEmail: String
});

defineEmits(['search', 'select-main', 'open-login-dialog', 'avatar-click']);

const searchInput = ref('');
</script>

<style scoped>

.top_menu {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 20px;
  background-color: #fdfdfd;
  border-bottom: 1px solid #ddd;
}

.logo {
  flex-shrink: 0;
  user-select: none;
  color: #2B2D30;
  font-size: 24px;
  font-weight: bold;
}

.top_menu .logo .p .text {
  text-decoration: none;
  color: #fd4500;
}

.logo p {
  margin: auto;
  font-family: "Pacifico", cursive;
  font-size: 30px;
}

.search_box {
  flex-grow: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1px;
}

:deep(.search_frame) {
  width: 30vw;
  height: 45px;
  border-radius: 18px !important;
  padding: 5px 15px;
  font-size: 15px;
  transition: all 0.3s ease-in-out;
}

.search_button {
  background-color: #fd5900;
  color: white;
  font-size: 14px;
  height: 34px;
  padding: 8px 16px;
  border-radius: 18px;
  border: none;
  transition: background 0.3s ease-in-out, transform 0.2s ease-in-out;
}

.search_button:hover {
  background-color: #d83900;
  color: white;
  font-size: 15px;
  transform: scale(1.03);
}

.search_button:active {
  transform: scale(0.98);
}

:deep(.el-input__wrapper) {
  margin-left: 5px;
  font-size: 15px;
  background-color: rgba(255, 255, 255, 0);
  color: black;
  border-radius: 150px;
}

:deep(.search_frame) {
  --el-input-focus-border-color: #fd5900;
}

.avatar_box {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  margin-left: 20px;
}

.login_button {
  border-radius: 20px;
  padding: 8px 20px;
  background-color: #fd4500;
  color: white;
}

.login_button:hover {
  background: #d83900;
  border-color: #ec0404;
  color: white;
  transform: scale(1.08);
}

.login_button:active {
  transform: scale(0.98);
}

.user_avatar {
  width: 45px !important;
  height: 45px !important;
  font-size: .9rem;
}
</style>