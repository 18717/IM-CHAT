<template>
  <div id="list">
    <!-- 搜索框 -->
    <el-header class="chat-friendsList-header">
      <el-row>
        <el-col :span="16">
          <el-input placeholder="搜索" v-model="search" clearable prefix-icon="el-icon-search" size="mini"
                    @input="searchList"
                    style="width: 170px">
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-button size="mini" @click="dialogVisible = true">搜索用户</el-button>
        </el-col>

      </el-row>
    </el-header>

    <!-- 好友列表 -->
    <el-main class="chat-friendsList-main scrollbar">
      <div class="friendItem" v-for="user in users"
           v-on:click="changeCurrentSession(user)">

        <el-button :class="{ activeItem: currentSession ? user.username === currentSession.username : false }">
          <el-row>
            <el-col :span="4">
              <el-avatar shape="square" :size="40" :src="user.avatar"></el-avatar>
            </el-col>
            <el-col :span="16">
              <el-badge :is-dot="isDot[user.username + '#' + currentUser.username]" class="item">
                <p>{{ user.nickname }}</p>
              </el-badge>
            </el-col>
          </el-row>

        </el-button>

      </div>
    </el-main>

    <!-- 搜索用户 -->
    <el-dialog
        :before-close="handleClose"
        style="background: rgba(58,58,58,1)"
        title="搜索用户"
        :visible.sync="dialogVisible"
        width="80%">

      <!-- 搜索框 -->
      <div style="width: 800px; margin: 0 auto">
        <el-input v-model="query.content"
                  @keydown.enter.native="doSearch('search')"
                  @clear="doSearch('clear')"
                  clearable
                  placeholder="请输入关键词">
          <el-select v-model="query.condition" slot="prepend" placeholder="请选择" style="width: 90px">
            <el-option label="UID" value="uid"></el-option>
            <el-option label="昵称" value="nickname"></el-option>
            <el-option label="邮箱" value="email"></el-option>
          </el-select>
          <el-button slot="append" icon="el-icon-search" @click="doSearch('search')">搜索</el-button>
        </el-input>
      </div>
      <!-- 用户列表 -->
      <div class="user-container">
        <el-card class="user-card" v-for="(user, index) in data" :key="index" shadow="hover">
          <div slot="header" class="clearfix">
            <el-row>
              <el-col :span="12" style="line-height: 40px"><i class="el-icon-user-solid"></i> {{ user.nickname }}
              </el-col>
              <el-col :span="12" style="text-align: right">
                <el-button type="text" @click="sendFriendRequest(user)">添加好友</el-button>
              </el-col>
            </el-row>
          </div>
          <el-row>
            <el-col :span="19" class="user-info-container">

              <div>UID：{{ user.uid }}</div>
              <div>用户名：{{ user.username }}</div>
              <div>性别：{{ user.gender }}</div>
              <div>邮箱：{{ user.email }}</div>
              <div>注册时间：{{ user.createTime }}</div>
              <div>在线状态：
                <el-tag size="small" :type="user.login ? 'success' : 'danger'">
                  {{user.login ? '在线' : '离线' }}
                </el-tag>
              </div>

            </el-col>
            <el-col :span="5" class="img-container">
              <img class="user-avatar" :src="user.avatar" :alt="user.nickname" :title="user.nickname">
            </el-col>
          </el-row>
          <!-- 头像 -->
        </el-card>
      </div>
      <!--  分页  -->
      <el-pagination
          @current-change="currentChange"
          background
          :page-size="pageSize"
          layout="prev, pager, next, jumper, ->, total"
          :total="total">
      </el-pagination>
    </el-dialog>

  </div>
</template>

<script>
import {mapState} from "vuex";

export default {
  name: "Friend",
  data() {
    return {
      dialogVisible: false,
      // 登录用户
      loginInfo: '',
      // 好友列表
      friendList: '',
      // 搜索参数
      search: '',
      // 分页参数
      total: 0,
      currentPage: 1,
      pageSize: 3,
      // 用户查询参数
      query: {condition: 'uid', content: 'ee'},
      // 查询结果
      data: [],
      // 添加好友备注
      comment: '',
    }
  },
  methods: {
    // 选中的聊天用户
    changeCurrentSession: function (currentSession) {
      this.$store.commit('changeCurrentSession', currentSession);
    },
    // 初始化登录信息
    initUser() {
      this.getRequest('/client/login/info').then(resp => {
        this.loginInfo = resp;
      })
    },
    refreshFriendList() {
      this.friendList = JSON.parse(window.sessionStorage.getItem('friend-list'))
    },
    // 发送好友请求
    sendFriendRequest(user) {
      let friendParams = {};
      this.$prompt('备注信息', '添加好友', {
        confirmButtonText: '发送请求',
        cancelButtonText: '取消',
      }).then(({value}) => {
        // 发送好友请求
        friendParams.sendUsername = this.loginInfo.username;
        friendParams.receiveUsername = user.username;
        friendParams.content = "【好友验证】" + this.loginInfo.nickname + "请求添加你为好友";
        friendParams.comment = value;
        friendParams.sendTime = new Date().format("yyyy-MM-dd hh:mm:ss");
        friendParams.requestType = 'add';

        this.$store.state.stomp.send('/ws/friend/send', {}, JSON.stringify(friendParams));

        this.$message({
          type: 'success',
          message: '向用户【' + user.nickname + '】发送好友请求成功！'
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消操作'
        });
      });


    },
    // 好友列表搜索
    searchList() {
      // TODO 留个坑
      console.log(this.search)
    },
    // 关闭搜索框调用
    handleClose(done) {
      done();
      this.data = null;
      this.query.content = null;
    },
    // 搜索
    doSearch(params) {
      if (params === 'search') {
        if (this.query.content == null) {
          Message.error("请输入搜索关键字")
        } else {
          this.getRequest('/client/search/page?currentPage=' + this.currentPage + '&size=' + this.pageSize + '&' + this.query.condition + '=' + this.query.content).then(resp => {
            this.data = resp.data;
            this.total = resp.total;
          })
        }
      } else if (params === 'clear') {
        this.data = null;
      }


    },
    // 分页
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.doSearch('search')
    },
  },
  computed: mapState([
    'users',
    'currentUser',
    'isDot',
    'currentSession',
  ]),
  mounted() {
    this.initUser();
    // this.refreshFriendList();
  },
}
</script>

<style>
.chat-friendsList-header {
  height: 60px;
  line-height: 60px;
  border-right: 1px rgba(89, 89, 89, 0.3) solid;
  padding-right: 5px;
}

.user-container {
  height: 276px;
  display: flex;
  justify-content: space-around;
  margin-top: 50px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.user-card {
  width: 300px;
  margin-bottom: 25px;
}

.user-card .el-card__header {
  padding: 7px 20px !important;
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 5px;
}

.img-container {
  text-align: right;
}

.user-info-container {
  font-size: 12px;
  padding-bottom: 10px;
}

.user-info-container div {
  margin-bottom: 10px;
}

/* 好友列表 */
.chat-friendsList-main {
  width: 100%;
  height: 478px;
  border-top: 1px rgba(89, 89, 89, 0.3) solid;
  border-right: 1px rgba(89, 89, 89, 0.3) solid;
}

.friendItem button {
  width: 100%;
  background: none;
  border: 0;
}

.friendItem button:hover {
  background-color: rgba(89, 89, 89, 0.3);
}

.friendItem p {
  height: 40px;
  line-height: 40px;
  margin: 0;
}

.activeItem {
  background-color: rgba(89, 89, 89, 0.3) !important;
}
</style>