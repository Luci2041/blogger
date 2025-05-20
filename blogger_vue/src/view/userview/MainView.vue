<template>
  <div class="main">
    <el-container class="container">
      <el-header class="top_menu">
        <!-- LOGO -->
        <div class="logo">
          <p title="回到首页" class="p">
            <router-link to="/" @click="selectedMain = 'home'" class="text">LXBlogger</router-link>
          </p>
        </div>

        <!-- 搜索框 -->
        <div class="search_box">
          <el-input
              v-model="searchInput"
              placeholder="Search LXBlogger"
              :clearable="true"
              class="search_frame"\

          ></el-input>
          <el-button class="search_button" @click="search()">搜索</el-button>
        </div>

        <!-- 登录按钮 / 用户头像 -->
        <div class="avatar_box">
          <el-button
              v-if="!isLoggedIn"
              class="login_button"
              @click="dialogVisible = true"
          >
            Log In
          </el-button>
          <!-- 悬浮头像弹出退出登录 -->
          <el-popover
              v-else
              trigger="hover"
              placement="bottom"
              width="120"
              popper-class="avatar-popover"
          >
            <!-- 弹出内容：退出登录 -->
            <div class="popover-content">
              <el-button
                  type="text"
                  @click="handleLogout"
                  style="width: 100%; text-align: left;"
              >
                退出登录
              </el-button>
            </div>

            <!-- 绑定弹出触发的元素 -->
            <template #reference>
              <UserAvatar
                  :userAvatar="userStore.userAvatar"
                  :userDisplayName="userStore.userName && userStore.userName.trim() !== '' ? userStore.userName : userStore.userEmail"
                  :avatarSize="40"
                  class="user_avatar"
              />
            </template>
          </el-popover>
        </div>
      </el-header>

      <!-- 左侧导航栏 -->
      <el-container>
        <div class="left-container">
          <LeftNavigation
              :selectedMain="selectedMain"
              :selectedTopic="selectedTopic"
              :isCollapsed="isCollapsed"
              @selectMain="selectMain"
              @selectTopic="selectTopic"
          />
          <div class="collapse-btn" @click="toggleCollapse">
            <el-icon>
              <component :is="isCollapsed ? ArrowRight : ArrowLeft"/>
            </el-icon>
          </div>
        </div>
        <!-- 路由-->
        <el-main class="main_body">
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>

  <!-- 登录对话框 -->
  <el-dialog v-model="dialogVisible" width="400px" center class="custom-dialog">
    <div class="login-container">
      <div class="login-switch">
        <span
            :class="{ active: loginMethod === 'password' }"
            @click="loginMethod = 'password'"
        >
          密码登录
        </span>
        <span>|</span>
        <span
            :class="{ active: loginMethod === 'captcha' }"
            @click="loginMethod = 'captcha'"
        >
          验证码登录
        </span>
      </div>

      <div class="input-group">
        <!-- 密码登录区域 -->
        <div v-if="loginMethod === 'password'" class="input-box">
          <input
              v-model="loginForm.userEmail"
              class="custom-input"
              placeholder="请输入邮箱"
          />
          <input
              v-model="loginForm.userPassword"
              type="password"
              class="custom-input"
              placeholder="密码"
          />
          <div class="forgot-password">忘记密码</div>
        </div>

        <!-- 验证码登录区域 -->
        <div v-else class="input-box">
          <input
              v-model="loginForm.userEmail"
              class="custom-input"
              placeholder="请输入邮箱"
          />
          <div class="captcha-container">
            <input
                v-model="loginForm.captcha"
                class="custom-input"
                placeholder="请输入验证码"
            />
            <button
                class="captcha-btn"
                :disabled="captchaBtnDisabled"
                @click="handleGetCaptcha"
            >
              {{ captchaBtnDisabled ? `${countdown}秒后重试` : '获取验证码' }}
            </button>
          </div>
          <div class="tip-text">注意：未注册的邮箱将会自动注册！</div>
        </div>
      </div>

      <!-- 登录提交按钮 -->
      <button @click="handleLogin" class="login-btn">登录</button>
    </div>
  </el-dialog>
</template>

<script setup>
import {onBeforeUnmount, onMounted, ref} from 'vue';
import {ElMessage, ElNotification} from 'element-plus';
import axios from 'axios'
import router from "../../router/index.js";
import UserAvatar from "../../components/UserAvatar.vue";
import LeftNavigation from "../../components/LeftNavigation.vue";
import {ArrowLeft, ArrowRight} from "@element-plus/icons-vue";
import {useArticleStore} from "../../stores/articleStore.js";


const selectedMain = ref('home');
const selectedTopic = ref(null);

const isLoggedIn = ref(false);
const userAvatar = ref('');

const dialogVisible = ref(false);
const loginMethod = ref('password');
const userId = localStorage.getItem('userId')
const loginForm = ref({
  userEmail: '',
  userPassword: '',
  captcha: ''
});

const userInfo = ref({
  userName: '',
  userEmail: '',
  userAvatar: ''
});

// 搜索方法
const articleStore = useArticleStore()
const searchInput = ref('');
const searchResults = ref([]);
const loading = ref(false);
const search =  async ()  => {
  if (!searchInput.value) {
    ElMessage.warning("请输入搜索内容");
    return;
  }

  loading.value = true;
  try {
    const res = await api.get('/User/searchArticles', {
      params: { keyword: searchInput.value }
    })
    const list = res.data || []

    const enhanced = await Promise.all(
        list.map(async (article) => {
          let liked = false
          try {
            const r2 = await api.get('/User/hasLikedArticle', {
              params: {
                articleId: article.articleId,
                userId: userId
              }
            })
            liked = (r2.data === true)
          } catch (e) {
            console.warn('查询点赞状态失败', article.articleId, e)
          }
          return { ...article, liked }
        })
    )
    articleStore.setArticles(enhanced)
    articleStore.activateSearch()

    ElNotification({
      title: '搜索成功',
      message: `找到 ${res.data.length} 条结果`,
      type: 'success',
      duration: 1200,
    })

    // 跳转到首页展示
    await router.push(({ name: 'Home' }))

  } catch (error) {
    console.error(error)
    ElMessage.error("搜索失败，请稍后再试")
  } finally {
    loading.value = false
  }
};

// 获取验证码方法
const api = axios.create({
  baseURL: '/api',
  timeout: 20000
})

const captchaBtnDisabled = ref(false);
const countdown = ref(0);
const countDownTime = 60;
let timer = null;
const userEmail = ref(localStorage.getItem("userEmail") || '');
const validateEmail = () => {
  return /^\w+@\w+\.\w+$/.test(loginForm.value.userEmail);
};
const handleGetCaptcha = () => {
  if (captchaBtnDisabled.value) {
    ElMessage.warning(`请等待${countdown.value}秒后重试`);
    return;
  }

  if (!validateEmail()) {
    return ElMessage.error('请输入有效的邮箱地址');
  }

  if (timer){
    clearInterval(timer);//清楚定时器
  }

  api.get('/User/sendCode/' + loginForm.value.userEmail).then(() => {
    ElMessage.success('验证码已发送至' + loginForm.value.userEmail);
  }).catch(err => {
    ElMessage.error('发送失败: ' + err.message);
  });

  // 禁用按钮，开始倒计时
  captchaBtnDisabled.value = true;
  countdown.value = countDownTime;

  timer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(timer);
      captchaBtnDisabled.value = false;
    }
  }, 1000);
};
// 组件卸载时清除定时器

onMounted(async () => {
  checkScreenSize();
  window.addEventListener('resize', checkScreenSize);
  // 如果 store 里有 userId，就认为已登录，去拉最新的 profile
  if (userStore.userId) {
    isLoggedIn.value = true
    await userStore.fetchProfile()
  }

});

onBeforeUnmount(() => {
  if (timer) clearInterval(timer);
  window.removeEventListener('resize', checkScreenSize);

});

// 登录方法
const handleLogin = async () => {
  if (!loginForm.value.userEmail) {
    ElMessage.error("请输入邮箱");
    return;
  }
  if (loginMethod.value === "password") {
    await handlePasswordLogin();
  } else if (loginMethod.value === "captcha") {
    await handleCaptchaLogin();
  }
};
const handlePasswordLogin = async () => {
  if (!loginForm.value.userPassword) {
    ElMessage.error("请输入密码");
    return;
  }
  try {
    const res = await api.post('/User/loginCheck', loginForm.value);
    if (res.data === true) {
      // 登录密码校验通过，拿用户信息
      const infoRes = await api.get('/User/findUserInfoByEmail/' + loginForm.value.userEmail);
      const user = infoRes.data;
      // console.log('user:', user);
      if (Number(user.userStatus) === 3) {
        // 状态3表示账号状态不正常
        ElMessage.error('您的账号异常，登录失败');
        return;
      }
      await onLoginSuccess(user);
    } else {
      ElMessage.error("邮箱或密码错误");
    }
  } catch (err) {
    console.error("登录请求失败:", err);
    ElMessage.error("网络错误，请重试");
  }
};
const handleCaptchaLogin = async () => {
  if (!loginForm.value.captcha) {
    ElMessage.error("请输入验证码");
    return;
  }
  try {
    const res = await api.get(
        `/User/checkCaptcha/${loginForm.value.userEmail}/${loginForm.value.captcha}`
    );
    if (res.data === true) {
      // 验证码校验通过，拉用户信息判断状态
      const infoRes = await api.get('/User/findUserInfoByEmail/' + loginForm.value.userEmail);
      const user = infoRes.data;
      if (Number(user.userStatus) === 3) {
        ElMessage.error('您的账号异常，登录失败');
        return;
      }
      await onLoginSuccess(user);
    } else {
      ElMessage.error("验证码错误或已过期");
    }
  } catch (err) {
    console.error("验证码请求失败:", err);
    ElMessage.error("验证失败，请重试");
  }
};


const onLoginSuccess = async (user) => {
  try {
    ElMessage.success("登录成功");
    isLoggedIn.value = true;

    userInfo.value = {
      userName: user.userName,
      userEmail: user.userEmail,
      userAvatar: user.userAvatar
    };
    localStorage.setItem("userEmail", user.userEmail,);
    localStorage.setItem("userId", user.userId);
    localStorage.setItem("userName", user.userName);
    localStorage.setItem("userAvatar", user.userAvatar);
    userAvatar.value = user.userAvatar
    dialogVisible.value = false;
    router.push('/').then(() => {
      location.reload();
    });
  } catch (err) {
    console.error("获取用户信息失败:", err);
    ElMessage.error("加载用户信息失败");
    userAvatar.value = "";//加载失败则强制显示默认头像
  }
};

const handleAvatarClick = () => {
  console.log('头像被点击');
  router.push('/userInfo');
};

function handleLogout() {
  // 清空用户信息
  userStore.logout()
  isLoggedIn.value = false
  router.push({ name: 'Home' })
}
import { useUserStore } from '../../stores/userStore.js'
const userStore = useUserStore()


const isCollapsed = ref(false)
const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}
// 响应屏幕宽度，自动折叠或展开
const checkScreenSize = () => {
  // 例如，当屏幕宽度小于 768px 时自动折叠
  if(window.innerWidth < 1024) {
    isCollapsed.value = true;
  } else {
    // 根据具体需求决定是否在宽屏自动展开，或保持用户上次选择状态
    // 这里选择宽屏默认展开
    isCollapsed.value = false;
  }
};

// 选择方法
const selectMain = (item) => {
    selectedMain.value = item;
    selectedTopic.value = null;
};

const selectTopic = (item) => {
  selectedTopic.value = item;
  selectedMain.value = null;
  const topics = ['风景','萌宠','科技','游戏'];
  if (topics.includes(item)) {
    searchInput.value = item;
    search();
    router.push({ name: 'Home' });
  }
  else if (['userInfo','userLikes','userPost','userPostArticle'].includes(item)) {
    router.push(`/${item}`);
  }
};

</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
}

.main {
  height: 100vh;
  width: 100%;
}

.container {
  height: 100vh;

}

/* 顶部菜单栏 */
.top_menu {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 20px;
  background-color: #fdfdfd;
  border-bottom: 1px solid #ddd;
}

/* LOGO 样式 */
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

/* 搜索框 */
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

/* 搜索按钮 */
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

/* 输入框样式 */
:deep(.el-input__wrapper) {
  margin-left: 5px;
  font-size: 15px;
  background-color: rgba(255, 255, 255, 0);
  color: black;
  border-radius: 150px;
}

.main .container .top_menu .search_box :deep(.search_frame) {
  --el-input-focus-border-color: #fd5900;
}

/* 登录按钮 / 头像 */
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

/* 登录对话框 */
.custom-dialog {
  border: 1px solid blue !important;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-container {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 400px;
  padding: 20px;
}

/* 登录方式切换 */
.login-switch {
  display: flex;
  justify-content: center;
  gap: 10px;
  font-size: 16px;
  color: #777;
  margin-bottom: 20px;
  cursor: pointer;
}

.login-switch .active {
  color: #fd4500;
  font-weight: bold;
  border-bottom: 2px solid #fd4500;
  padding-bottom: 2px;
}

/* 输入框区域 */
.input-group {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.input-box {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.custom-input {
  flex: 1;
  padding: 10px;
  width: calc(100% - 20px);
  min-width: 0;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 5px;
  outline: none;
}

.custom-input:focus {
  border-color: #d9534f;
}

/* 忘记密码 */
.forgot-password {
  align-self: flex-end;
  font-size: 12px;
  color: grey;
  cursor: pointer;
}

.forgot-password:hover {
  text-decoration: underline;
  color: #fd4500;
}

.tip-text {
  color: #999;
  font-size: 12px;
  text-align: left;
}

/* 验证码输入框 */
.captcha-container {
  display: flex;
  width: 100%;
  max-width: 320px;
  gap: 10px;

}

.captcha-btn {
  background-color: #fd4500;
  color: white;
  font-size: 16px;
  padding: 8px 16px;
  border-radius: 5px;
  border: none;
  transition: background 0.3s ease-in-out, transform 0.2s ease-in-out;

  &:disabled {
    background-color: #ccc !important;
    cursor: not-allowed;
    transform: none;
  }

  &:active:not(:disabled) {
    transform: scale(0.98);
  }
}

.captcha-btn:hover {
  background-color: #c9302c;
  color: white;
  transform: scale(1.03);
}

.captcha-btn:active {
  transform: scale(0.98);
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  background-color: #ff6b6b;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 20px;
  transition: background 0.3s;
}

.login-btn:hover {
  background-color: #fd4500;
}

.user_avatar {
  width: 45px !important;
  height: 45px !important;
  font-size: .9rem;
}

.topic-menu :deep(.el-sub-menu__title),
.topic-menu :deep(.el-menu-item) {
  padding: 0 !important;
  background-color: transparent !important;
}

/* 左侧导航设置相对定位 */
.left-container {
  position: relative;
  display: inline-block;
  vertical-align: top;
}

.collapse-btn {
  position: absolute;
  top: 3%;
  right: -1rem;
  z-index: 2000;
  width: 2rem;
  height: 2rem;
  background: #ffffff;
  border: 1px solid #ddd;
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
}

.collapse-btn:hover {
  background: #f8fafc;
  box-shadow: 3px 0 6px rgba(0, 0, 0, 0.1);
}

@media (max-width: 1024px) {
  .collapse-btn {
    position: absolute;
    top: -5%;
    left: 15px;
    transform: translateX(-50%);
    z-index: 2001;
  }
}

/* 主内容区 */
.main_body {
  height: 100%;
  padding: 0;
//box-sizing: border-box; background-color: #fdfdfd; margin: 0;
}
</style>