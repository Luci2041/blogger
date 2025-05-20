import { defineStore } from 'pinia'
import axios from 'axios'

export const useUserStore = defineStore('user', {
    state: () => ({
        userId: localStorage.getItem('userId') || '',
        userName: '',
        userEmail: '',
        userAvatar: ''
    }),
    actions: {
        async fetchProfile() {
            if (!this.userId) return
            const res = await axios.get(`api/User/findUserInfoByUserId/${this.userId}`)
            const u = res.data
            this.userName   = u.userName
            this.userEmail  = u.userEmail
            this.userAvatar = u.userAvatar
            localStorage.setItem('userAvatar', u.userAvatar)
            localStorage.setItem('userName', u.userName)
            localStorage.setItem('userEmail', u.userEmail)
        },
        setAvatar(url) {
            this.userAvatar = url
            localStorage.setItem('userAvatar', url)
        },
        logout() {
            // 清空 Pinia state
            this.userId     = ''
            this.userName   = ''
            this.userEmail  = ''
            this.userAvatar = ''

            // 删除 localStorage 中的所有用户字段
            localStorage.removeItem('userId')
            localStorage.removeItem('userName')
            localStorage.removeItem('userEmail')
            localStorage.removeItem('userAvatar')

        }
    }
})
