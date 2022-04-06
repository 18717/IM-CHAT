<template>
  <div id="list">
    <!-- 头部 -->
    <el-header class="chat-friendsList-header">
      <div style="width: 60%; display: inline-block"><h5><i class="el-icon-user"></i>&emsp; 好友列表</h5></div>
      <div style="width: 40%; display: inline-block">
        <el-button size="mini" type="primary" plain @click="dialogVisible = true" style="border: 0">搜索用户</el-button>
      </div>
    </el-header>
    <!-- 好友列表 -->
    <el-main class="chat-friendsList-main scrollbar">
      <div v-for="friend in friendList" class="friendItem"
           @click="changeCurrentUser(friend)">
        <el-button :class="{ activeItem: currentUser ? friend.username === currentUser.username : false }">
          <el-row>
            <el-col :span="4">
              <el-avatar shape="square" :size="40" :src="friend.avatar"></el-avatar>
            </el-col>
            <el-col :span="16">
              <el-badge :is-dot="isDot[friend.username + '#' + currentLogin.username]" class="item">
                <p>{{ friend.nickname }}</p>
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
                  {{ user.login ? '在线' : '离线' }}
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
import {Message} from "element-ui";

export default {
  name: "Friend",
  data() {
    return {
      dialogVisible: false,
      // 登录用户
      loginInfo: '',
      // 好友列表
      // friendList: '',
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
    changeCurrentUser: function (currentUser) {
      this.$store.commit('changeCurrentUser', currentUser);
    },
    // 初始化登录信息
    initUser() {
      this.getRequest('/client/login/info').then(resp => {
        this.loginInfo = resp.data;
      })
    },
    // 发送好友请求
    sendFriendRequest(user) {
      let friendParams = {};
      this.$prompt('备注信息', '添加好友', {
        confirmButtonText: '发送请求',
        cancelButtonText: '取消',
      }).then(({value}) => {
        // 发送好友请求
        friendParams.sender = this.loginInfo.username;
        friendParams.receiver = user.username;
        friendParams.title = "好友申请";
        friendParams.content = value;
        friendParams.add = 1;
        friendParams.flag = 0;
        friendParams.verified = 0;
        friendParams.time = new Date().format("yyyy-MM-dd hh:mm:ss");
        // friendParams.businessType = 'add';
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
    // 关闭搜索框调用
    handleClose(done) {
      done();
      this.data = null;
      this.query.content = null;
    },
    // 搜索
    doSearch(params) {
      var check = new RegExp("[\u4e00-\u9fa5]");
      if (params === 'search') {
        if (this.query.content == null) {
          Message.error("请输入搜索关键字")
        } else if ((this.query.condition === 'uid' && !check.test(this.query.content)) || this.query.condition !== 'uid') {
          this.getRequest('/client/search/page?currentPage=' + this.currentPage + '&size=' + this.pageSize + '&' + this.query.condition + '=' + this.query.content).then(resp => {
            this.data = resp.data;
            this.total = resp.total;
          })
        } else {
          Message.error("UID不能含有中文");
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
    'friendList',
    'currentLogin',
    'isDot',
    'currentUser',
  ]),
  mounted() {
    this.initUser();
    // this.refreshFriendList();
  },
}
</script>

<style>

h5 {
  padding: 0;
  margin: 0;
}

.chat-friendsList-header {
  height: 60px;
  border-right: 1px rgba(89, 89, 89, 0.3) solid;
  border-radius: 3px;
  margin-left: 20px;
  padding: 25px 0 0;
  text-align: left;
  line-height: 60px;
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