import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useArticleStore = defineStore('article', () => {
    const articles = ref([])              //存储文章列表
    const isSearchMode = ref(false)       //判断是否处于搜索状态

    /**
     * 替换文章列表
     * @param newArticles 新文章数组
     */
    const setArticles = (newArticles) => {
        articles.value = newArticles
    }

    /**
     * 追加文章列表（用于分页加载）
     * @param moreArticles 需要追加的文章数组
     */
    const appendArticles = (moreArticles) => {
        articles.value.push(...moreArticles)
    }

    /**
     * 清空文章列表
     */
    const clearArticles = () => {
        articles.value = []
        isSearchMode.value = false
    }

    /**
     * 激活搜索模式
     */
    const activateSearch = () => {
        isSearchMode.value = true
    }

    /**
     * 清除搜索模式（恢复首页模式）
     */
    const clearSearch = () => {
        isSearchMode.value = false
    }

    return {
        articles,
        isSearchMode,
        setArticles,
        appendArticles,
        clearArticles,
        activateSearch,
        clearSearch
    }
})