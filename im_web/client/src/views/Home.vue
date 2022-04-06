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
            <el-button v-if="currentGroup.leader !== currentLogin.username" @click="quitGroup(currentGroup)"
                       icon="el-icon-circle-close" size="mini"
                       style="background: none; border: 0;">退出群聊
            </el-button>
            <el-dropdown v-else trigger="click" placement="top">
              <el-button icon="el-icon-setting" size="mini"
                         style="background: none; border: 0;">更多操作
              </el-button>
              <el-dropdown-menu slot="dropdown">
                  <span @click="dialogGroupForm = true"><el-dropdown-item icon="el-icon-edit"
                                                                          style="color: #0000ff">编辑信息</el-dropdown-item></span>
                <span @click="dismissGroup(currentGroup)"><el-dropdown-item icon="el-icon-circle-close"
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
              {{ currentGroup.user.nickname }}
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
          <el-descriptions v-if="currentGroup.leader !== currentLogin.username" :column="1"
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
      <!-- 修改群聊信息 -->
      <el-dialog :visible.sync="dialogGroupForm"
                 v-if="currentGroup !== null"
                 title="修改群信息"
                 :destroy-on-close="true"
                 :show-close="false"
                 style="height: 400px">
        <el-card class="edit-group-info">
          <el-form :model="currentGroup" label-position="left">
            <el-form-item label="群名" label-width="80px">
              <el-input :value="currentGroup.groupName" maxlength="9" show-word-limit clearable
                        v-model="currentGroup.groupName"></el-input>
            </el-form-item>
            <el-form-item label="群描述" label-width="80px">
              <el-input type="textarea" :value="currentGroup.groupDescribe"
                        maxlength="30" show-word-limit
                        :autosize="{ minRows: 2, maxRows: 2}"
                        v-model="currentGroup.groupDescribe">
              </el-input>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card class="edit-submit">
          <el-button @click="dialogGroupForm = false" type="info" plain>取消修改</el-button>
          <el-divider/>
          <el-button @click="editGroupInfo(currentGroup)" type="primary" plain>确认修改</el-button>
        </el-card>
      </el-dialog>
    </div>
  </div>

</template>

<script>
import Card from "@/components/chat/Card";
import Message from "@/components/chat/Message";
import TextInput from "@/components/chat/TextInput";
import Friend from "@/components/chat/Friend";
import {mapState} from "vuex";

export default {
  name: "Home",
  head: {
    title: "IM-CHAT",
    meta: [
      {name: 'description', content: 'IM-CHAT'}
    ]
  },
  data() {
    return {
      dialogGroupForm: false,
    }
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
              friendNotice.sender = this.currentLogin.username;
              friendNotice.receiver = user.username;
              friendNotice.title = "解除好友关系";
              friendNotice.flag = 1;
              friendNotice.del = 1;
              friendNotice.add = 0;
              friendNotice.time = new Date().format("yyyy-MM-dd hh:mm:ss");
              this.$store.state.stomp.send('/ws/friend/send', {}, JSON.stringify(friendNotice));
              this.getRequest('/friend/list?username=' + this.currentLogin.username).then(friendList => {
                this.$store.commit('INIT_FRIEND_LIST', friendList)
              })
              this.$router.go(0)
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
      this.$confirm('您将要退出群聊, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.postRequest('/group/quit?gid=' + group.gid + "&username=" + this.currentLogin.username).then(resp => {
          this.refreshGroupList();
          // 发送通知给群主;
          let notice = {};
          notice.confirm = 1;
          notice.verified = 1;
          notice.quit = 1;
          notice.join = 0;
          notice.dismiss = 0;
          notice.forceQuit = 0;
          notice.flag = 1;
          notice.title = "用户已退出群聊";
          notice.sender = this.currentLogin.username;
          notice.receiver = this.currentGroup.leader;
          notice.gid = this.currentGroup.gid;
          notice.time = new Date().format("yyyy-MM-dd hh:mm:ss");
          this.$store.state.stomp.send('/ws/group/send', {}, JSON.stringify(notice));
          this.$store.commit('removeCurrent');
          this.$router.go(0)
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消操作'
        });
      });
    },
    forceQuitUser(username) {
      let message = '是否将用户 ' + username + '移出群聊？';
      this.$confirm(message, '提示', {
        confirmButtonText: '移出',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.postRequest('/group/force-quit?gid=' + this.currentGroup.gid + "&username=" + username).then(resp => {
          this.refreshGroupList();
          // 发送通知给用户
          let notice = {};
          notice.confirm = 1;
          notice.verified = 1;
          notice.quit = 0;
          notice.join = 0;
          notice.dismiss = 0;
          notice.forceQuit = 1;
          notice.flag = 1;
          notice.title = "您已被移出群聊";
          notice.avatar = this.currentLogin.avatar;
          notice.nickname = this.currentLogin.nickname;
          notice.sender = this.currentLogin.username;
          notice.receiver = username;
          notice.groupName = this.currentGroup.groupName;
          notice.gid = this.currentGroup.gid;
          notice.time = new Date().format("yyyy-MM-dd hh:mm:ss");
          this.$store.state.stomp.send('/ws/group/send', {}, JSON.stringify(notice));
          this.$router.go(0)
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消操作'
        });
      });
    },
    dismissGroup(group) {
      let message = '是否解散群聊 ' + group.groupName + ' ，此操作不可取消？';
      this.$confirm(message, '提示', {
        confirmButtonText: '确定解散',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.postRequest('/group/dismiss?gid=' + group.gid).then(resp => {
          this.refreshGroupList();
          // 发送通知给用户
          let notice = {};
          notice.confirm = 1;
          notice.verified = 1;
          notice.quit = 0;
          notice.join = 0;
          notice.dismiss = 1;
          notice.forceQuit = 0;
          notice.flag = 1;
          notice.sender = group.leader;
          notice.gid = group.gid;
          notice.groupName = group.groupName;
          notice.members = group.members;
          notice.title = "群聊已被群主解散";
          notice.time = new Date().format("yyyy-MM-dd hh:mm:ss");
          this.$store.state.stomp.send('/ws/group/send', {}, JSON.stringify(notice));
          this.$router.go(0)
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消操作'
        });
      });
    },
    editGroupInfo(groupInfo) {
      this.dialogGroupForm = false;
      groupInfo.time = groupInfo.createTime;
      groupInfo.createTime = null;
      this.postRequest('/group/edit', groupInfo).then(resp => {
        this.refreshGroupList();
      })
    },
    refreshGroupList() {
      this.getRequest('/group/list?username=' + this.currentLogin.username).then(groupList => {
        this.$store.commit('INIT_GROUP_LIST', groupList)
      })
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

.edit-group-info {
  display: inline-block;
  width: 70%;
  height: 165px;
}

.edit-group-info textarea {
  resize: none;
  font-family: "HarmonyOS_Sans_Regular", sans-serif;
}

.el-textarea .el-input__count {
  height: 27px;
  background: none !important;
}

.edit-submit {
  display: inline-block;
  width: 27%;
  height: 100%;
  margin-left: 15px;
  text-align: center;
}

.edit-submit button {
  margin: 11px 0;
  height: 100%;
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