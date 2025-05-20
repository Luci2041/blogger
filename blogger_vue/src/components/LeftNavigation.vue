<template>
  <!-- 使用 prop isCollapsed 来设置类名 -->
  <el-aside class="left_menu" :class="{ 'collapsed': isCollapsed }">
    <div class="left-bar-top" v-if="!isCollapsed">
      <router-link
          to="/"
          class="menu-item"
          :class="{ active: selectedMain === 'home' }"
          @click="selectMain('home')"
      >
        <div class="menu-icon">⚪</div>
        <div class="menu-text">主页</div>
      </router-link>
      <router-link
          to="/hot"
          class="menu-item"
          :class="{ active: selectedMain === 'hot' }"
          @click="selectMain('hot')"
      >
        <div class="menu-icon">⚪</div>
        <div class="menu-text">热门</div>
      </router-link>
    </div>
    <!-- 发布 -->
    <div class="left-bar-post" v-if="!isCollapsed">
      <el-menu class="topic-menu" :default-active="selectedTopic">
        <el-sub-menu index="topics" class="topic-submenu">
          <template #title>
            <div class="menu-item">
              <div class="menu-text">发布</div>
            </div>
          </template>
          <el-menu-item index="userPostArticle" class="topic-item" @click="onPostArticle">
            <div class="menu-text">文章</div>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </div>
    <!-- 话题 -->
    <div class="left-bar-topic" v-if="!isCollapsed">
      <el-menu class="topic-menu" :default-active="selectedTopic">
        <el-sub-menu index="topics" class="topic-submenu">
          <template #title>
            <div class="menu-item">
              <div class="menu-text">话题</div>
            </div>
          </template>
          <el-menu-item index="scenery" class="topic-item" @click="selectTopic('风景')">
            <div class="menu-text">风景</div>
          </el-menu-item>
          <el-menu-item index="food" class="topic-item" @click="selectTopic('萌宠')">
            <div class="menu-text">萌宠</div>
          </el-menu-item>
          <el-menu-item index="tech" class="topic-item" @click="selectTopic('科技')">
            <div class="menu-text">科技</div>
          </el-menu-item>
          <el-menu-item index="game" class="topic-item" @click="selectTopic('游戏')">
            <div class="menu-text">游戏</div>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </div>
    <!-- 我的 -->
    <div class="left-bar-my" v-if="!isCollapsed">
      <el-menu class="my-menu" :default-active="selectedTopic">
        <el-sub-menu index="topics" class="topic-submenu">
          <template #title>
            <div class="menu-item">
              <div class="menu-text">我的</div>
            </div>
          </template>
          <!-- 我的信息 -->
          <el-menu-item
              index="userInfo"
              class="topic-item"
              @click="onMyPage('userInfo', { name: 'UserInfo' })"
          >
            <div class="menu-text">我的信息</div>
          </el-menu-item>

          <!-- 我的点赞 -->
          <el-menu-item
              index="userLikes"
              class="topic-item"
              @click="onMyPage('userLikes', { name: 'UserLikes' })"
          >
            <div class="menu-text">我的点赞</div>
          </el-menu-item>

          <!-- 我的发布 -->
          <el-menu-item
              index="userPost"
              class="topic-item"
              @click="onMyPage('userPost', { name: 'UserPost' })"
          >
            <div class="menu-text">我的发布</div>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </div>
    <!-- 底部信息 -->
    <div class="left-footer" v-if="!isCollapsed">
      <div class="left-footer-text">
        联系我们:liuxun347@gmail.com
        LXBlogger&copy;
      </div>
    </div>
  </el-aside>
</template>

<script setup>
import router from "../router/index.js";
import {ElMessage} from "element-plus";
import {useUserStore} from "../stores/userStore.js";

const props = defineProps({
  selectedMain: String,
  selectedTopic: String,
  isCollapsed: Boolean
});

const emit = defineEmits(['selectMain', 'selectTopic']);
const userStore = useUserStore()
function onPostArticle() {
  if (!userStore.userId) {
    ElMessage.error('请先登录')
    return
  }
  // 选中状态
  emit('selectTopic', 'userPostArticle')
  // 真正跳转到发布文章页面
  router.push({ name: 'PostArticle' })
}

function onMyPage(key, route) {
  if (!userStore.userId) {
    ElMessage.error('请先登录')
    return
  }
  emit('selectTopic', key)
  router.push(route)
}
const selectMain = (item) => {
  emit('selectMain', item);
};

const selectTopic = (item) => {
  emit('selectTopic', item);
};
</script>

<style scoped>
.left_menu {
  position: relative;
  width: 240px;
  min-width: 240px;
  height: 100%;
  padding: 20px;
  border-right: 1px solid #00000033;
  background-color: #fdfdfd;
  transition: all 0.3s ease;
  overflow: auto;
}

.left_menu.collapsed {
  width: 0;
  min-width: 0;
}

.left-bar-top {
  border-bottom: 1px solid #e5e5e5;
  display: flex;
  flex-flow: column;
  padding-bottom: 15px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 10px;
  border-radius: 10px;
  cursor: pointer;
  text-decoration: none;
  color: inherit;
  user-select: none;
  margin: 2px 0;
  height: 40px;
  width: 200px;
  box-sizing: border-box;
}

.menu-item:hover {
  background-color: #f6f8f9;
}

.topic-menu :deep(.el-sub-menu__title),
.topic-menu :deep(.el-menu-item) {
  padding: 0 !important;
  background-color: transparent !important;
}

.menu-icon {
  margin-right: 8px;
}

.menu-text {
  font-size: 0.875rem;
}
.left-bar-post,
.left-bar-topic,
.left-bar-my {
  border: 0 solid red;
  display: flex;
  flex-flow: column;
  border-bottom: 1px solid #ddd;
  padding: 10px;
}

.topic-menu, .my-menu {
  border-right: none !important;
  background-color: transparent !important;
}

.topic-menu :deep(.el-sub-menu__title),
.my-menu :deep(.el-sub-menu__title) {
  padding: 0 !important;
  background-color: transparent !important;
}

.topic-menu :deep(.el-menu-item),
.my-menu :deep(.el-menu-item) {
  height: 40px !important;
  line-height: 40px !important;
  padding: 0 !important;
  background-color: transparent !important;
  display: flex;
  align-items: center;
  border-radius: 10px;
}

.topic-menu :deep(.el-menu-item) .menu-text,
.my-menu :deep(.el-menu-item) .menu-text {
  margin-left: 28px;
  user-select: none;
}

.topic-menu :deep(.el-menu-item:hover),
.my-menu :deep(.el-menu-item:hover) {
  background-color: #f6f8f9 !important;
}

.menu-item.active,
.topic-menu :deep(.el-menu-item.is-active),
.my-menu :deep(.el-menu-item.is-active) {
  background-color: #e4eaed !important;
  color: inherit;
}

.left-footer {
  display: flex;
  user-select: none;
  border: 0 solid red;
  padding: 10px;
  font-size: 12px;
  color: #999a9f;
}

.left-footer-text {
  border: 0 solid yellow;
  text-align: center;
  display: flex;
}

:deep(.el-aside) {
  overflow: visible !important;
}

@media (max-width: 1024px) {
  .left_menu {
    position: fixed;
    left: -240px;
    z-index: 1000;
    height: 100vh;
    box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
  }

  .left_menu:not(.collapsed) {
    left: 0;
  }

}

</style>