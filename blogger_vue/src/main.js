import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import '../src/assets/icon_font/iconfont.css'
import { createPinia } from 'pinia'

const app = createApp(App);

app.use(ElementPlus)
app.use(router);
app.use(createPinia())
app.mount('#app');

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}