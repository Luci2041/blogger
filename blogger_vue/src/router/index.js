import {createRouter, createWebHistory} from 'vue-router';
import MainView from "../view/userview/MainView.vue";
import HomeView from "../view/userview/HomeView.vue";
import HotView from "../view/userview/HotView.vue";
import UserInfoView from "../view/userview/UserInfoView.vue";
import UserLikesView from "../view/userview/UserLikesView.vue";
import UserPostView from "../view/userview/UserPostView.vue";
import UserPostArticle from "../view/userview/UserPostArticle.vue";
import ArticleDetails from "../view/userview/ArticleDetails.vue";
import AdminMainView from "../view/adminview/AdminMainView.vue";
import UserCenter from "../view/adminview/UserCenter.vue";
import ArticleCenter from "../view/adminview/ArticleCenter.vue";
import ReportCenter from "../view/adminview/ReportCenter.vue";


const routes = [
    {
        path: '/',
        name: 'Main',
        component: MainView,
        children: [
            {
                path: '',
                name: 'Home',
                component: HomeView
            },
            {
                path: 'hot',
                name: 'Hot',
                component: HotView,
            },
            {
                path: 'userInfo',
                name: 'userInfo',
                component: UserInfoView,
            },
            {
                path: 'UserLikes',
                name: 'UserLikes',
                component: UserLikesView,
            },
            {
                path: 'UserPost',
                name: 'UserPost',
                component: UserPostView,
            },
            {
                path: 'UserPostArticle',
                name: 'UserPostArticle',
                component: UserPostArticle,
            },
            {
                path: 'ArticleDetails/:articleId',
                name: 'ArticleDetails',
                component: ArticleDetails,
            }
        ]
    },
    {
        path: '/admin',
        name: 'Admin',
        component: AdminMainView,
        children: [
            {
                path: 'UserCenter',
                name: 'UserCenter',
                component: UserCenter
            },{
                path: 'ArticleCenter',
                name: 'ArticleCenter',
                component: ArticleCenter
            },{
                path: 'ReportCenter',
                name: 'ReportCenter',
                component: ReportCenter
            }
        ]
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
    scrollBehavior(to, from, savedPosition) {
        if (savedPosition) {
            // 用户通过浏览器的后退/前进按钮导航时，返回保存的滚动位置
            return savedPosition
        } else {
            // 其他方式跳转页面时默认滚动到顶部
            return { top: 0 }
        }
    },
});

export default router;
