<script setup>
import {ref} from "vue";

import {useRoute, useRouter} from "vue-router";
const router = useRouter()

let selectIndex = ref(0)
const select = (index) => {
  selectIndex.value = index
  switch (index) {
    case 0:
      router.push('/Top')
          break
    case 1:
      router.push('/Squre')
      break
  }
}
</script>

<template>
  <div style="width: 100%;">
    <div style="width: 100%;height: 26px;font-size: 13px;color: rgb(128,128,128);display: flex;align-items: center;">
      <font>公告：所有功能都已完成，可以正常使用</font>
    </div>

    <!--      导航栏目-->
    <div style="display: flex;width: 100%;background:#f8db71;height: 60px;justify-content: space-between;">
        <div style="display: flex;border: 0px solid;align-items: center;width: 50%;justify-content: space-between;padding-left: 30px">
          <div style="display: flex;align-items: center;gap: 10px">
            <img src="../assets/logo.png" style="width: 40px;border-radius: 50%">
            <font style="font-size: 20px;font-weight: bolder;color: #355476">失物招领平台</font>
          </div>
          <div style="border: 0px solid;height: 100%;width: 60%;display: flex;align-items: center;font-size: 14px;gap: 18px">
            <span @click="select(0)" :style="`padding-left: 15px;padding-right: 15px;height: 100%;display: flex;align-items: center;${selectIndex==0?'font-weight: bold;border-bottom: 3px solid #009788':''}`"><font>首页</font></span>
            <span @click="select(1)" :style="`padding-left: 15px;padding-right: 15px;height: 100%;display: flex;align-items: center;${selectIndex==1?'font-weight: bold;border-bottom: 3px solid #009788':''}`"><font>失物广场</font></span>
            <span @click="select(2)" :style="`padding-left: 15px;padding-right: 15px;height: 100%;display: flex;align-items: center;${selectIndex==2?'font-weight: bold;border-bottom: 3px solid #009788':''}`"><font>招领广场</font></span>
            <span @click="select(3)" :style="`padding-left: 15px;padding-right: 15px;height: 100%;display: flex;align-items: center;${selectIndex==3?'font-weight: bold;border-bottom: 3px solid #009788':''}`"><font>反馈建议</font></span>
          </div>
        </div>
        <div style="display: flex;height: 100%;justify-content: center;align-items: center;gap: 14px;padding-right: 30px">
          <div style="height: 31.6px;width: 55.6px;background:#fff;font-size: 13px;color: #606266;display: flex;justify-content: center;align-items: center;border-radius: 2px"><font>登录</font></div>
          <div style="height: 31.6px;width: 55.6px;background:#fff;font-size: 13px;color: #606266;display: flex;justify-content: center;align-items: center;border-radius: 2px"><font>注册</font></div>
        </div>
    </div>
<!--    内容-->
    <el-scrollbar height="88vh">
      <router-view/>
    </el-scrollbar>
  </div>
</template>

<style scoped>

</style>