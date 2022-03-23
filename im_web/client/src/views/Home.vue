<template>
  <div id="chatArea">
    <div class="chat-main">
      <el-container id="chat">
        <el-header class="menu">
          <Card/>
        </el-header>
        <el-container>
          <el-aside class="sidebar">
            <router-view/>
          </el-aside>
          <el-main class="main">
            <Message/>
            <TextInput/>
          </el-main>
        </el-container>
      </el-container>
    </div>
    &nbsp;
    <div class="desc-info">

      <!-- 用户信息 -->
      <el-drawer
          v-if="chatType === 'private'"
          :visible.sync="showInfo"
          :modal="false"
          :show-close="false"
          size="100%"
          style="position: absolute"
          direction="ltr">
        <div slot="title">
          <div style="display: inline-block; width: 60%">{{ currentUser.nickname }}</div>
          <div style="display: inline-block; width: 30%; text-align: right">
            <el-button @click="delFriend(currentUser)"
                       icon="el-icon-delete" size="mini"
                       style="background: none; border: 0;">删除好友
            </el-button>
          </div>

        </div>
        <div class="group-info">
          <el-descriptions :column="1" size="small" border>
            <el-descriptions-item>
              <template slot="label">
                头像
              </template>
              <img class="avatar" v-bind:src="currentUser.avatar"
                   v-bind:alt="currentUser.nickname">
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                昵称
              </template>
              <span>{{ currentUser.nickname }}</span>
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                用户名
              </template>
              <span>{{ currentUser.username }}</span>
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                UID
              </template>
              {{ currentUser.uid }}
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                性别
              </template>
              {{ currentUser.gender }}
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                邮箱
              </template>
              {{ currentUser.email }}
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                注册时间
              </template>
              {{ currentUser.createTime }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

      </el-drawer>

      <!-- 群聊信息 -->
      <el-drawer
          v-if="chatType === 'public'"
          :visible.sync="showInfo"
          :modal="false"
          :show-close="false"
          size="100%"
          style="position: absolute"
          direction="ltr">
        <div slot="title">
          <div style="display: inline-block; width: 60%">{{ currentGroup.groupName }}</div>
          <div style="display: inline-block; width: 30%; text-align: right">
            <el-button v-if="currentGroup.masterUsername !== currentLogin.username" @click="quitGroup(currentGroup)"
                       icon="el-icon-circle-close" size="mini"
                       style="background: none; border: 0;">退出群聊
            </el-button>
            <el-dropdown v-else trigger="click" placement="top">
              <el-button icon="el-icon-setting" size="mini"
                         style="background: none; border: 0;">更多操作
              </el-button>
              <el-dropdown-menu slot="dropdown">
                  <span @click="editGroupInfo(currentGroup)"><el-dropdown-item icon="el-icon-edit"
                                                                               style="color: #0000ff">编辑信息</el-dropdown-item></span>
                <span @click="delGroup(currentGroup)"><el-dropdown-item icon="el-icon-circle-close"
                                                                        style="color: #ff0000">解散群聊</el-dropdown-item></span>
              </el-dropdown-menu>
            </el-dropdown>
          </div>

        </div>
        <div class="group-info">
          <el-descriptions :column="1" size="small" border>
            <el-descriptions-item>
              <template slot="label">
                群名
              </template>
              <span>{{ currentGroup.groupName }}</span>
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                群号
              </template>
              {{ currentGroup.gid }}
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                群主
              </template>
              {{ currentGroup.masterUsername }}
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                成员
              </template>
              {{ currentGroup.memberNum }} / 10
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                创建时间
              </template>
              {{ currentGroup.createTime }}
            </el-descriptions-item>
          </el-descriptions>
          <h5 style="margin: 10px 10px">成员列表</h5>
          <el-descriptions v-if="currentGroup.masterUsername !== currentLogin.username" :column="1"
                           class="group-member1"
                           size="small" border>
            <el-descriptions-item v-for="(member,index) in currentGroup.memberArr">
              <template slot="label">
                {{ index + 1 }}
              </template>
              {{ member }}
            </el-descriptions-item>
          </el-descriptions>


          <el-descriptions v-else :column="1" class="group-member2"
                           size="small" border>
            <el-descriptions-item v-for="member in currentGroup.memberArr">
              <template slot="label">
                {{ member }}
              </template>
              <el-button type="text" size="mini" icon="el-icon-circle-close" @click="forceQuitUser(member)">强制退群
              </el-button>
            </el-descriptions-item>
          </el-descriptions>
        </div>
        <el-divider/>
        <div class="group-desc">
          <h5 style="padding: 20px 10px">群描述</h5>
          <div>
            <p>{{ currentGroup.groupDescribe }}</p>
          </div>
        </div>

      </el-drawer>
    </div>
  </div>

</template>

<script>
import Card from "@/components/chat/Card";
import Message from "@/components/chat/Message";
import TextInput from "@/components/chat/TextInput";
import Friend from "@/components/chat/Friend";
import {mapState} from "vuex";
import {postRequest} from "@/api/api";

export default {
  name: "Home",
  head: {
    title: "IM-CHAT",
    meta: [
      {name: 'description', content: 'IM-CHAT'}
    ]
  },
  created() {
    this.$store.dispatch("connect")
  },
  mounted() {
    this.$forceUpdate();
  },
  computed: mapState([
    'currentLogin',
    'currentUser',
    'currentGroup',
    'chatType',
    'showInfo',
  ]),
  methods: {

    delFriend(user) {
      let th = this
      let message = '确定和好友 ' + user.nickname + ' 解除好友关系，是否继续？';
      this.$confirm(message, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.commit('delFriend')
        this.postRequest('/friend/del?username=' + user.username)
            .then(resp => {
              let friendNotice = {};
              friendNotice.avatarUrl = this.currentLogin.avatar;
              friendNotice.sendNickname = this.currentLogin.nickname;
              friendNotice.sendUsername = this.currentLogin.username;
              friendNotice.receiveUsername = user.username;
              friendNotice.content = "解除好友关系";
              friendNotice.businessType = 'del';
              friendNotice.sendTime = new Date().format("yyyy-MM-dd hh:mm:ss");
              this.$store.state.stomp.send('/ws/friend/send', {}, JSON.stringify(friendNotice));
              this.getRequest('/friend/list?username=' + this.currentLogin.username).then(friendList => {
                this.$store.commit('INIT_FRIEND_LIST', friendList)
              })
            })
            .catch({});
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消操作'
        });
      });
    },

    quitGroup(group) {
      alert(group.gid + "退出")
    },
    delGroup(group) {
      alert(group.gid + "解散")
      // TODO 刷新群信息
    },
    forceQuitUser(user) {
      alert("强制退出" + user)
      // TODO 刷新群信息
    },
    editGroupInfo(groupInfo) {
      alert("修改群信息" + groupInfo)
    },
  },
  components: {
    Card,
    Friend,
    Message,
    TextInput
  },
}
</script>

<style>

#chatArea {
  width: 85%;
  display: inline-flex;
  margin: 4.5% 13% 5.3%;
}

#chatArea .chat-main {
  display: inline-block;
  width: 80%;
}

#chatArea .desc-info {
  display: inline-block;
  overflow: hidden;
  position: relative;
  width: 20%;
  height: 600px;
}

.desc-info {
  border-radius: 5px;
}

.desc-info .el-drawer__header {
  width: 100%;
  padding: 10px;
  margin: 0;
  line-height: 40px;
  background: #B0C5A9 none;
}

.desc-info .el-drawer__body {
  background: rgba(234, 250, 231, 0.99);
}

.desc-info .group-info {
  width: 100%;
  height: 69.75%;
  max-height: 69.75%;
  overflow-y: auto;
  background: none;
  border: 0;
}

.group-info .el-descriptions__header {
  margin: 10px 0;
}

.group-member1 .el-descriptions-item__label {
  width: 10%;
}

.group-member2 .el-descriptions-item__label {
  width: 63.6%;
}

.desc-info .group-desc {
  width: 100%;
  height: 29.3%;
  background: none;
  border: 0;
}

.desc-info .group-desc div {
  background-color: #ecf5ff;
  width: 80%;
  margin: 0 auto;
  height: 55%;
  padding: 0 10px;
  line-height: 25px;
  font-size: 13px;
  border: 1px solid #d9ecff;
  border-radius: 4px;
}

.desc-info .el-divider {
  padding: 0;
  margin: 0;
}

#chat {
  width: 100%;
  height: 600px;
  border-radius: 5px;
  overflow: hidden;
}

#chat .sidebar, #chat .main, #chat .menu {
  height: 100%;
  padding: 0;
}

#chat .menu {
  padding: 0;
  background-color: rgba(89, 89, 89, 0.3);
  color: black;
}

#chat .sidebar {
  background: rgba(255, 255, 255, 0.5);
}

#chat .main {
  position: relative;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.5);
}
</style>