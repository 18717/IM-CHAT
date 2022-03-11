<template>
  <el-main>
    <el-header class="title-welcome"><b>Welcome Login to IM-CHAT SERVER</b></el-header>
    <el-container class="text">
      <p>尊敬的 <span>IM-CHAT</span> 用户，欢迎登录 IM-CHAT 即时通讯系统。</p>
      <p>当前北京时间：<span id="bjTime"></span></p>
    </el-container>
  </el-main>
</template>

<script>
import {UTCToLocalTimeString} from "@/api/api";

export default {
  name: "Welcome"
}

setInterval(function () {
  var time = UTCToLocalTimeString(new Date(), 'yyyy-MM-dd hh:mm:ss');
  document.getElementById("bjTime").innerHTML = time;
}, 1000);
</script>


<style>
.title-welcome {
  padding: 0;
  margin: 0;
  font-size: 36px;
  color: #8c939d;
}

.text {
  font-size: 18px;
}
</style>