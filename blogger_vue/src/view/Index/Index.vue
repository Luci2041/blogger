
<template>

<div style="width: 100%;border: 0px solid;">
<!--  走马灯-->
  <div style="width: 100%;display: flex;justify-content: center;margin-top: 5px">
    <el-carousel height="300px" style="width: 80%">
      <el-carousel-item>
        <img src="../../assets/banner1.png" style="height: 100%;width: 100%;">
      </el-carousel-item>
      <el-carousel-item>
        <img src="../../assets/banner2.png" style="height: 100%;width: 100%;">
      </el-carousel-item>
      <el-carousel-item>
        <img src="../../assets/banner3.png" style="height: 100%;width: 100%;">
      </el-carousel-item>
    </el-carousel>
  </div>

<!--  最新发布的失物和招领-->
  <div style="border: 0px solid;width: 100%;display: flex;margin-bottom: 30px;
  justify-content: center;align-items: center;margin-top: 20px">
    <div style="width: 80%;display: flex;justify-content: space-between">
<!--      失物-->
      <el-card style="width: 49%;display: flex;flex-direction: column;height: 430px">

       <div style="height:400px;width: 100%;display: flex;flex-direction: column;justify-content: space-between;">
         <div style="width: 100%;display: flex;justify-content: space-between;align-items: center;border: 0px solid">
           <div style="color: #725b04;font-size: 18px;font-weight: bold">最新发布失物</div>
           <div style="font-size: 14px;color: #666666">查看更多</div>
         </div>

         <div style="width: 100%;display: flex;flex-wrap: wrap;justify-content: space-between;border: 0px solid;height: 320px;">
           <el-card v-for="item in 4" style="height:150px;width: 48%;display: flex;flex-direction: column;">
             <div style="display: flex;gap: 5px">
               <img src="../../assets/vue.svg" style="background:#e2e0e0;width: 75px;border-radius: 10px">
               <div style="font-size: 14px;color: #666666;display: flex;flex-direction: column;gap: 10px">
                 <font style="color: #ef5d26;font-weight: bold">一只史莱姆</font>
                 <font>发布人：张三</font>
                 <font>时间：2024-9-25</font>
               </div>
             </div>
             <div style="margin-top: 20px">
               <el-button size="small" type="info" @click="dialogVisible = true">查看详情</el-button>
               <el-button size="small" type="success">联系失主</el-button>
             </div>
           </el-card>
         </div>
       </div>

      </el-card>

      <el-card style="width: 49%;display: flex;flex-direction: column;height: 430px">

        <div style="height:400px;width: 100%;display: flex;flex-direction: column;justify-content: space-between;">
          <div style="width: 100%;display: flex;justify-content: space-between;align-items: center;border: 0px solid">
            <div style="color: #725b04;font-size: 18px;font-weight: bold">最新发布招领</div>
            <div style="font-size: 14px;color: #666666">查看更多</div>
          </div>

          <div style="width: 100%;display: flex;flex-wrap: wrap;justify-content: space-between;border: 0px solid;height: 320px;">
            <el-card v-for="item in 4" style="height:150px;width: 48%;display: flex;flex-direction: column;">
              <div style="display: flex;gap: 5px">
                <img src="../../assets/vue.svg" style="background:#e2e0e0;width: 75px;border-radius: 10px">
                <div style="font-size: 14px;color: #666666;display: flex;flex-direction: column;gap: 10px">
                  <font style="color: #ef5d26;font-weight: bold">一只史莱姆</font>
                  <font>发布人：张三</font>
                  <font>时间：2024-9-25</font>
                </div>
              </div>
              <div style="margin-top: 20px">
                <el-button size="small" type="info">查看详情</el-button>
                <el-button size="small" type="success">联系失主</el-button>
              </div>
            </el-card>
          </div>
        </div>

      </el-card>
    </div>
  </div>

</div>


  <el-dialog
      v-model="dialogVisible"
      title="Tips"
      width="500"
      :before-close="handleClose"
  >
    <span>This is a message</span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="dialogVisible = false">
          Confirm
        </el-button>
      </div>
    </template>
  </el-dialog>



</template>

<script setup>

import banner from '../../assets/banner1.png'
import {onMounted, ref} from 'vue'
import { ElMessageBox } from 'element-plus'

var demo = ref([])
var obj = ref({})

let ufo = ref("45644")
const dialogVisible = ref(false)
const handleClose = () => {
  return
}


//
// x()
// const x = () => {
//   return new Promise((resolve, reject)=>{
//     setTimeout(()=>{
//       console.log("lodSuccess")
//     },10000);
//   })
// }
// ///建议使用以下方法（）
// onMounted(()=>{
//   x()
// })


</script>


<style scoped>
.demonstration {
  color: var(--el-text-color-secondary);
}

.el-carousel__item h3 {
  color: #475669;
  opacity: 0.75;
  line-height: 150px;
  margin: 0;
  text-align: center;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
  background-color: #d3dce6;
}
</style>