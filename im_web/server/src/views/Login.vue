<template>

  <el-form
      id="loginBox"
      :rules="rules"
      v-loading.fullscreen.lock="loading"
      element-loading-text="正在登录中"
      ref="loginForm" :model="loginForm" label-position="right"
      label-width="70px">

    <h2 class="title-login">IM-CHAT 即时通讯系统</h2>
    <el-form-item label="用户名" prop="username">
      <el-input type="text" auto-complete="false" v-model="loginForm.username" placeholder="请输入登录名"></el-input>
    </el-form-item>
    <el-form-item label="密码" prop="password">
      <el-input type="password" auto-complete="false" v-model="loginForm.password" placeholder="请输入密码"></el-input>
    </el-form-item>
    <el-form-item label="验证码" prop="code" style="height: 40px !important;">
      <el-row>
        <el-col :span="13">
          <el-input type="text" auto-complete="false" v-model="loginForm.code" placeholder="输入验证码"></el-input>
        </el-col>
        <el-col :span="9" style="margin-left: 8px">
          <el-button v-if="captchaUrl == null" @click="updateCaptcha">获取验证码</el-button>
          <img v-else :src="captchaUrl" @click="updateCaptcha" alt="验证码" style="margin-left: 11px;border-radius: 5px">
        </el-col>
      </el-row>
    </el-form-item>
    <el-button type="primary" plain style="width: 100%" @click="toLogin">登录</el-button>
  </el-form>

</template>

<script>
export default {
  name: "Login",
  head: {
    title: "登录",
    meta: [
      {name: 'description', content: '服务端登录'}
    ]
  },
  data() {
    return {
      captchaUrl: null,
      loading: false,
      loginForm: {
        username: 'admin',
        password: '123456',
        code: '',
      },
      rules: {
        username: [{required: true, message: '请输入登录名', trigger: 'blur'}],
        password: [{required: true, message: '请输入密码', trigger: 'blur'}],
        code: [{required: true, message: '请输入验证码', trigger: 'blur'}],
      },
    }
  },
  methods: {
    toLogin() {
      this.loading = true;

      this.postRequest('/login/server', this.loginForm).then(resp => {
        if (resp) {
          window.sessionStorage.setItem('token', resp.data.tokenHead + resp.data.token);
          let path = this.$route.query.redirect;
          this.$router.replace((path === '/' || path === undefined) ? '/home' : path);
        } else {
          this.updateCaptcha();
        }
      })
      this.loading = false;
    },
    updateCaptcha() {
      this.captchaUrl = '/captcha/server/login?username=' + this.loginForm.username + '?time' + new Date();
    },

  }
}
</script>

<style>
body {
  /* fallback for old browsers */
  background: #f7f0ac;
  /* Chrome 10-25, Safari 5.1-6 */
  background: -webkit-linear-gradient(to right, #f0acf7, #acf7f0, #f7f0ac);
  /* W3C, IE 10+/ Edge, Firefox 16+, Chrome 26+, Opera 12+, Safari 7+ */
  background: linear-gradient(to right, #f0acf7, #acf7f0, #f7f0ac);
}

#loginBox {
  border-radius: 15px;
  background-clip: padding-box;
  margin: 170px auto;
  width: 330px;
  padding: 15px 35px 15px 35px;
  background: #fff;
  border: 1px #eaeaea solid;
  box-shadow: 0 0 25px #cac6c6;
}

#loginBox .title-login {
  margin-top: 10px;
  margin-bottom: 25px;
  text-align: center;
}

.el-row, .el-form-item__content {
  height: 40px !important;
}
</style>