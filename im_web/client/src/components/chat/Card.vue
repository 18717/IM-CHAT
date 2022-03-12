<template>
  <div id="card">
    <el-row>
      <el-col :span="2" style="padding-left: 15px; height: 40px"><img class="avatar" v-bind:src="loginInfo.avatar"
                                                                      v-bind:alt="loginInfo.nickname"></el-col>
      <el-col :span="5" style="margin-right: 10px">{{ loginInfo.nickname }}</el-col>

      <el-col :span="16">
        <el-menu class="menu"
                 mode="horizontal"
                 :default-active="$route.path"
                 router>
          <el-menu-item index="/friend">
            <span><i class="el-icon-user"></i>好友列表</span>
          </el-menu-item>
          <el-menu-item index="/group">
            <span><i class="el-icon-message"></i>群组</span>
          </el-menu-item>
          <el-menu-item index="/notice">
            <span><i class="el-icon-chat-dot-round"></i>系统通知</span>
          </el-menu-item>
          <el-menu-item index="/setting">
            <span><i class="el-icon-setting"></i>设置</span>
          </el-menu-item>

        </el-menu>
      </el-col>


    </el-row>
  </div>
</template>

<script>

export default {
  name: 'Card',
  data() {
    return {
      loginInfo: {nickname: 'IM-CHAT', avatar: 'http://cdn.lrxya.icu/im_avatar/3.jpg'},
    }
  },
  mounted() {
    this.initLoginInfo();
  },
  methods: {
    initLoginInfo() {
      this.getRequest('/client/login/info').then(resp => {
        this.loginInfo = resp;
      })
    }
  }
}
</script>

<style>
#card {
  padding: 10px;
  line-height: 40px;
}

#card .avatar {
  width: 40px;
  height: 40px;
  border-radius: 3px;
}

.menu ul {
  border: 0;
  height: 40px !important;
  background: none !important;
}

.menu li {
  height: 40px !important;
  width: 150px;
  border: 0 !important;
  line-height: 40px !important;
}

.el-menu.el-menu--horizontal {
  border: 0 !important;
}

.el-menu--horizontal .el-menu-item:not(.is-disabled):focus, .el-menu--horizontal .el-menu-item:not(.is-disabled):hover {
  background: none !important;
}
.el-menu--horizontal > .el-menu-item.is-active {
  /*color: #409EFF !important;*/
}


</style>
