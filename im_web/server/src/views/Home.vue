<template>
  <el-container id="server">
    <el-header>
      <el-row style="height: 61px">
        <el-col :span="12"><h1 style="margin: 0">IM-CHAT 即时通讯系统</h1></el-col>
        <el-col :span="12" style="text-align: right">
          <el-dropdown trigger="click">
            <div style="cursor: pointer;">
              <i class="el-icon-s-custom" style="margin-right: 15px"></i>
              {{ loginUser.nickname }}
            </div>
            <el-dropdown-menu slot="dropdown">
              <span @click="toSpace"><el-dropdown-item>个人中心</el-dropdown-item></span>
              <span @click="logout"><el-dropdown-item>退出系统</el-dropdown-item></span>
            </el-dropdown-menu>
          </el-dropdown>
        </el-col>
      </el-row>
    </el-header>
    <el-container style="height: 637px">
      <el-aside width="200px" style="background-color: rgb(238, 241, 246)">
        <el-menu :default-active="$route.path" router>
          <el-menu-item index="/space">
            <i class="el-icon-s-home"></i>个人中心
          </el-menu-item>
          <el-menu-item index="/notice">
            <i class="el-icon-s-comment"></i>推送消息
          </el-menu-item>
          <el-menu-item index="/list">
            <i class="el-icon-s-grid"></i>用户列表
          </el-menu-item>
          <el-menu-item index="/new-user">
            <i class="el-icon-circle-plus-outline"></i>添加用户
          </el-menu-item>
        </el-menu>
      </el-aside>
      <router-view/>
    </el-container>
    <el-footer>
      Copyright © 2022 xiangli.org.cn 版权所有.
    </el-footer>
  </el-container>
</template>

<script>

export default {
  name: 'Home',
  head: {
    meta: [
      {name: 'description', content: '服务端首页'}
    ]
  },

  data() {
    return {
      loading: false,
      loginUser: '',
    }
  },

  mounted() {
    this.$store.dispatch("connect")
    this.refreshLoginInfo();
  },

  methods: {
    refreshLoginInfo() {
      this.getRequest("/server/login/info").then(resp => {
        this.loginUser = resp;
        window.sessionStorage.setItem('login-user', JSON.stringify(resp))
      });
    },
    logout() {
      this.$confirm('此操作将注销登录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.postRequest('/logout/server').then(() => {
          window.sessionStorage.clear();
          this.$router.replace('/');
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        });
      });
    },
    toSpace() {
      this.$router.push('/space');
    },
    handleClose(done) {
      this.$confirm('确定注销登录吗？')
          .then(_ => {
            done();
          })
          .catch(_ => {
          });
    },
  }
}
</script>

<style>

#server {
  height: 100%;
  max-height: 100%;
  border-radius: 3px;
  background: rgb(238, 241, 246);
  border: 3px #eaeaea solid;
  box-shadow: 0 0 25px #cac6c6;
}

.el-menu-item, .el-menu, .el-main {
  border-radius: 3px;
}

.el-header {
  line-height: 60px;
}

.el-footer {
  height: 33px !important;
  line-height: 33px;
  text-align: center;
  margin: 0;
  padding: 0;
  font-size: 12px;
  color: #525252;
}

.el-main {
  background: #fff;
  border: 0 #eaeaea solid;
}
</style>
