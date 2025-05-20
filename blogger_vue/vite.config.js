import {defineConfig, loadEnv} from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  //加载 .env 文件中的变量
  const env = loadEnv(mode, process.cwd())

  // 后端地址
  const API_HOST = env.VITE_API_HOST || '你所在的ipv4地址'

  return {
    plugins: [vue()],
    server: {
      proxy: {
        // /static前缀的请求代理到后端静态资源服务
        '/static': {
          target: API_HOST,
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/static/, '/static')
        },
        // /api前缀的请求代理到后端接口（示例）
        '/api': {
          target: API_HOST,
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, '')
        }
      }
    },
    // 可选：在代码中使用 import.meta.env.VITE_API_HOST
    define: {
      'process.env': {}
    }
  }
})
