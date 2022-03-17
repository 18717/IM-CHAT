<template>
  <div id="sys_notice">

    <el-header class="chat-set-header">
      <h5><i class="el-icon-chat-dot-round"></i>&emsp; 通知中心</h5>
    </el-header>

    <!-- 功能列 -->
    <el-main class="chat-set-main scrollbar">
      <el-collapse>
        <el-collapse-item>
          <template slot="title">
            <i class="el-icon-edit-outline"></i> <span>服务器通知</span>
          </template>
          <ul style="margin: 0; padding: 0">
            <li v-for="notice in noticeList['server']" @click="showServerNotice(notice)">
              <el-col :span="12">{{ notice.title.substr(0, 7) }}</el-col>
              <el-col :span="12" style="text-align: right; padding-right: 10px">
                <el-tag size="small">{{ notice.pushTime.substr(0, 10) }}</el-tag>
              </el-col>
            </li>
          </ul>
        </el-collapse-item>
        <el-collapse-item>
          <template slot="title">
            <i class="el-icon-edit-outline"></i> <span>好友通知</span>
          </template>
          <ul style="margin: 0; padding: 0">
            <li v-for="notice in noticeList['friend']" @click="showFriendNotice(notice)">

              <el-col :span="18"><b>{{ notice.sendNickname }}</b> {{ notice.title.substr(0, 5) }}</el-col>
              <el-col :span="6" style="text-align: right; padding-right: 10px">
                <el-tag size="small">{{ notice.sendTime.substr(5, 5) }}</el-tag>
              </el-col>
            </li>
          </ul>
        </el-collapse-item>
        <el-collapse-item>
          <template slot="title">
            <i class="el-icon-edit-outline"></i> <span>群通知</span>
          </template>
          <ul style="margin: 0; padding: 0">
            <li>IM-CHAT申请加入群聊</li>
            <li>IM-CHAT退出群聊</li>
          </ul>
        </el-collapse-item>
      </el-collapse>
      <el-divider><i class="el-icon-sunrise"></i></el-divider>
    </el-main>

    <!-- 好友请求通知 -->
    <el-dialog
        v-if="noticeFriend.flag === 0"
        class="friend-notice-dialog"
        :show-close="false"
        :visible.sync="dialogVisible"
        width="30%">
      <span slot="title">{{ noticeFriend.title }}</span>
      <!-- 拒绝确认以及原因 -->
      <el-dialog
          class="friend-notice-dialog"
          :show-close="false"
          :visible.sync="dialogRefuse"
          append-to-body
          width="30%">


        <el-card class="friend-notice">
          <el-row>
            拒绝 <b>{{ noticeFriend.sendNickname }}</b> 的好友请求
          </el-row>
          <el-divider/>
          <el-row>
            <el-col :span="6">
              <el-tag type="danger">拒绝原因</el-tag>
            </el-col>
            <el-col :span="18">
              <el-input
                  type="text"
                  placeholder="(可忽略)"
                  v-model="text"
                  maxlength="15"
                  show-word-limit
              >
              </el-input>
            </el-col>
          </el-row>
        </el-card>

        <span slot="footer">
        <el-row type="flex" justify="center" :gutter="20">
          <el-button type="danger" plain @click="refuse()" style="border: 0; width: 100%">确定拒绝</el-button>
        </el-row>
      </span>
      </el-dialog>
      <el-card class="friend-notice">
        <el-row>
          <el-col :span="4"><img style="width: 40px; height: 40px; border-radius: 3px"
                                 :src="noticeFriend.avatarUrl" alt=""></el-col>
          <el-col :span="20">
            <b>{{ noticeFriend.sendNickname }}</b> 请求添加你为好友，是否同意？
          </el-col>
        </el-row>
        <el-divider/>
        <el-row>
          <el-col :span="6">
            <el-tag size="medium">备注信息</el-tag>
          </el-col>
          <el-col :span="18">{{ noticeFriend.content }}</el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-tag size="medium">请求日期</el-tag>
          </el-col>
          <el-col :span="18">{{ noticeFriend.sendTime }}</el-col>
        </el-row>
      </el-card>
      <span slot="footer" v-if="noticeFriend.verified === 0">
        <el-row type="flex" justify="center" :gutter="20">
          <el-col :span="12"><el-button type="danger" plain @click="dialogRefuse = true"
                                        style="border: 0; width: 100%">拒 绝</el-button></el-col>
          <el-col :span="12"><el-button type="primary" plain @click="consent()"
                                        style="border: 0; width: 100%">同 意</el-button></el-col>
        </el-row>
      </span>
      <span slot="footer" v-else-if="noticeFriend.verified === 1">
        <el-button v-if="noticeFriend.confirm === 1" type="primary" disabled plain
                   style="width: 100%; border: 0">已同意</el-button>
        <el-button v-else-if="noticeFriend.confirm ===0" type="danger" disabled plain
                   style="width: 100%; border: 0">已拒绝</el-button>
        <el-button v-else type="danger" disabled plain style="width: 100%; border: 0">未知错误</el-button>
      </span>
      <span slot="footer" v-else>
        <el-button type="danger" disabled plain style="width: 100%; border: 0">未知错误</el-button>
      </span>
    </el-dialog>
    <!-- 好友反馈通知 -->
    <el-dialog
        v-else
        class="friend-notice-dialog"
        :show-close="false"
        :visible.sync="dialogVisible"
        width="30%">
      <span slot="title">{{ noticeFriend.title }}</span>

      <el-card class="friend-notice">

        <el-row>
          <el-col :span="4"><img style="width: 40px; height: 40px; border-radius: 3px"
                                 :src="noticeFriend.avatarUrl" alt=""></el-col>
          <el-col :span="20">
            <b>{{ noticeFriend.sendNickname }}</b>
            <span v-if="noticeFriend.confirm === 0"> 已拒绝你的好友申请！</span>
            <span v-else>已同意你的好友申请！</span>
          </el-col>
        </el-row>
        <el-divider/>
        <el-row v-if="noticeFriend.confirm === 0">
          <el-col :span="6">
            <el-tag size="medium">拒绝原因</el-tag>
          </el-col>
          <el-col :span="18">{{ noticeFriend.content }}</el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-tag size="medium">反馈日期</el-tag>
          </el-col>
          <el-col :span="18">{{ noticeFriend.sendTime }}</el-col>
        </el-row>
      </el-card>
    </el-dialog>

  </div>
</template>

<script>
import {mapState} from "vuex";
import {marked} from "marked";

export default {
  name: "Notice",
  data() {
    return {
      dialogVisible: false,
      dialogRefuse: false,
      noticeFriend: {},
      resultNotice: {},
      text: '',
    }
  },

  computed: {
    ...mapState({
      noticeList: 'noticeList',
      friendList: 'friendList',
      currentUser: 'currentUser',
    }),
  },

  methods: {
    showServerNotice(notice) {
      let file;
      if (notice.fileName !== "" || notice.fileUrl !== "") {
        file = "<br/><br/><p><strong>附件：</strong><a style='color: #409EFF; text-decoration: none' href=" + notice.fileUrl + ">" + notice.fileName + "</a></p>";
      } else {
        file = '';
      }
      this.$alert(marked(notice.content) + file, {
        dangerouslyUseHTMLString: true
      });
    },
    showFriendNotice(notice) {
      this.dialogVisible = true;
      this.noticeFriend = notice;
      if (notice.add === 1) {
        let user = this.currentUser;
        this.resultNotice.sendUsername = user.username;
        this.resultNotice.sendNickname = user.nickname;
        this.resultNotice.receiveUsername = notice.sendUsername;
        this.resultNotice.avatarUrl = user.avatar;
        this.resultNotice.flagTime = notice.flagTime;
        this.resultNotice.requestType = 'add';
      } else if (notice.del === 1) {

      }
    },
    refuse() {
      this.dialogRefuse = false
      let notice = this.noticeFriend;
      let resultNotice = this.resultNotice;
      notice.confirm = 0;
      notice.verified = 1;
      resultNotice.confirm = 0;
      resultNotice.verified = 1;
      resultNotice.result = false;
      resultNotice.flag = 1;
      resultNotice.comment = this.text;
      resultNotice.sendTime = new Date().format("yyyy-MM-dd hh:mm:ss");
      this.$store.state.stomp.send('/ws/friend/send', {}, JSON.stringify(resultNotice));
      this.$message.error("已拒绝 " + resultNotice.receiveUsername + "的好友请求")
      this.dialogVisible = false;
    },
    consent() {
      let notice = this.noticeFriend;
      let resultNotice = this.resultNotice;
      notice.confirm = 1;
      notice.verified = 1;
      resultNotice.confirm = 1;
      resultNotice.verified = 1;
      resultNotice.result = true;
      resultNotice.flag = 1;
      resultNotice.sendTime = new Date().format("yyyy-MM-dd hh:mm:ss");
      this.$store.state.stomp.send('/ws/friend/send', {}, JSON.stringify(resultNotice));
      this.$message.success("已同意 " + resultNotice.receiveUsername + "的好友请求")
      this.getRequest('/friend/list?username=' + this.currentUser.username).then(friendList => {
        this.$store.commit('INIT_FRIEND_LIST', friendList)
      })
      this.dialogVisible = false;
    },
  },
}
</script>

<style>
h5 {
  padding: 0;
  margin: 0;
}

.el-message-box {
  width: 700px !important;
  height: auto !important;
  max-height: 700px !important;
  overflow-y: auto !important;
  overflow-x: auto !important;
}

.friend-notice-dialog .el-dialog__header {
  padding-left: 20px;
}

.friend-notice-dialog .el-dialog__body {
  padding: 10px 20px 15px;
}

.friend-notice .el-row {
  height: 40px;
  line-height: 40px;
}

.friend-notice .el-divider {
  margin-top: 15px;
  margin-bottom: 15px;
}

/* 标题 */
.chat-set-header {
  height: 60px;
  border-right: 1px rgba(89, 89, 89, 0.3) solid;
  border-radius: 3px;
  margin-left: 20px;
  padding: 25px 0 0;
  text-align: left;
  line-height: 60px;
}

/* 功能列表 */
.chat-set-main {
  height: 478px;
  border-top: 1px rgba(89, 89, 89, 0.3) solid;
  border-right: 1px rgba(89, 89, 89, 0.3) solid;
  border-radius: 3px;
}

/* 选项按钮 */
.el-collapse {
  border: 0;
}

.el-collapse-item__header {
  width: 100%;
  background: none;
  border: 0;
  text-align: left;
}

.el-collapse-item__header span {
  margin-left: 10px;
}

.el-collapse-item__wrap {
  border: 0;
  background: none;
}

.el-collapse-item__content {
  padding: 0;
}

.el-collapse-item__content li {
  list-style: none;
  height: 36px;
  line-height: 36px;
  margin-bottom: 5px;
  margin-top: 5px;
  padding-left: 22px;
  background: none;
  border-radius: 3px;
}

.el-collapse-item__content li:hover {
  cursor: pointer;
  background-color: rgba(89, 89, 89, 0.3);
}

.el-collapse-item__content li:active {
  background-color: rgba(89, 89, 89, 0.3);
}

.setItem button:hover {
  background-color: rgba(89, 89, 89, 0.3);
}

.el-divider__text {
  background: none !important;
}
</style>