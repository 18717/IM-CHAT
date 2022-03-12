<template>
  <el-main>

    <el-card class="forget-box">
      <div slot="header">
        <h3>找回 IM-CHAT 账号密码</h3>
      </div>
      <el-steps :active="active" finish-status="finish" align-center>
        <el-step title="填写信息" icon="el-icon-edit"></el-step>
        <el-step title="验证" icon="el-icon-edit"></el-step>
        <el-step title="完成" icon="el-icon-edit"></el-step>
      </el-steps>

      <div v-if="active === 1" class="">
        <el-form label-position="left" label-width="80px" :model="userInfo">
          <el-form-item label="用户名">
            <el-input v-model="userInfo.username"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div v-if="active === 2">
        <el-input v-model="userInfo.email"></el-input>
        <p style="color: red">注意：切勿泄露验证码，否则造成的一切后果将由您来承担！</p>
      </div>
      <div v-if="active === 3">
        <p style="color: red">注意：切勿泄露验证码，否则造成的一切后果将由您来承担！</p>
      </div>
      <el-button style="margin-top: 12px;" @click="back">上一步</el-button>
      <el-button style="margin-top: 12px;" @click="next">下一步</el-button>

    </el-card>


  </el-main>
</template>

<script>
export default {
  name: "Forget",

  data() {
    return {
      active: 1,
      userInfo: {username: '', email: ''},
    }
  },

  methods: {
    back() {
      if (this.active-- < 1) this.active = 1;
    },
    next() {
      if (this.active++ > 3) this.active = 3;
    }
  }
}
</script>

<style>
.forget-box {
  width: 70%;
  margin: 0 auto;
}

.forget-info button {
  margin-top: 0 !important;
}
</style>