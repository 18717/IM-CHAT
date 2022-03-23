<template>
  <div id="sys_notice">

    <el-header class="chat-set-header">
      <h5><i class="el-icon-chat-dot-round"></i>&emsp; 通知中心</h5>
    </el-header>

    <!-- 功能列 -->
    <el-main class="chat-set-main scrollbar">
      <el-collapse accordion>
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

              <el-col :span="18"><b>{{ notice.sendNickname }}</b> {{ notice.title.substr(0, 6) }}</el-col>
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
            <li v-for="notice in noticeList['group']" @click="showGroupNotice(notice)">
              <el-col :span="18" v-if="notice.flag === null"><b>{{ notice.senderNickname }}</b> {{ notice.title }}</el-col>
              <el-col :span="18" v-else>{{ notice.title }}</el-col>
              <el-col :span="6"><el-tag size="small">{{ notice.sendTime.substr(5, 5) }}</el-tag></el-col>
            </li>
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
        :visible.sync="dialogFriendNotice"
        width="30%">
      <span slot="title">{{ noticeFriend.title }}</span>
      <!-- 拒绝确认以及原因 -->
      <el-dialog
          class="friend-notice-dialog"
          :show-close="false"
          :visible.sync="dialogRefuseFriend"
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
          <el-button type="danger" plain @click="refuseFriend()" style="border: 0; width: 100%">确定拒绝</el-button>
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
          <el-col :span="12"><el-button type="danger" plain @click="dialogRefuseFriend = true"
                                        style="border: 0; width: 100%">拒 绝</el-button></el-col>
          <el-col :span="12"><el-button type="primary" plain @click="consentFriend()"
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
        :visible.sync="dialogFriendNotice"
        width="30%">
      <span slot="title">{{ noticeFriend.title }}</span>

      <el-card v-if="noticeFriend.add === 1" class="friend-notice">

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

      <el-card v-else-if="noticeFriend.del === 1" class="friend-notice">
        <el-row>
          <el-col :span="4"><img style="width: 40px; height: 40px; border-radius: 3px"
                                 :src="noticeFriend.avatarUrl" alt=""></el-col>
          <el-col :span="20">
            <b>{{ noticeFriend.sendNickname }}</b> 已和您解除好友关系
          </el-col>
        </el-row>
        <el-divider/>
        <el-row>
          <el-col :span="6">
            <el-tag size="medium">解除日期</el-tag>
          </el-col>
          <el-col :span="18">{{ noticeFriend.sendTime }}</el-col>
        </el-row>
      </el-card>

      <el-card v-else class="friend-notice">
        异常错误，请联系管理员
        <el-divider/>
      </el-card>
    </el-dialog>

    <!-- 群通知 -->
    <el-dialog
        v-if="noticeGroup.flag === null"
        :show-close="false"
        :visible.sync="dialogGroupNotice"
        width="30%">
      <span slot="title">{{ noticeGroup.title }}</span>
      <el-card>
        <el-row>
          <el-col :span="4"><img style="width: 40px; height: 40px; border-radius: 3px"
                                 :src="noticeGroup.avatarUrl" alt=""></el-col>
          <el-col :span="20">
            <b>{{ noticeGroup.senderNickname }}</b> 申请加入群聊，是否同意？
          </el-col>
        </el-row>
        <el-divider/>
        <el-row>
          <el-col :span="6">
            <el-tag size="medium">群名</el-tag>
          </el-col>
          <el-col :span="18">{{ noticeGroup.groupName }}</el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-tag size="medium">性别</el-tag>
          </el-col>
          <el-col :span="18">{{ noticeGroup.gender }}</el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-tag size="medium">备注信息</el-tag>
          </el-col>
          <el-col :span="18">{{ noticeGroup.content }}</el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-tag size="medium">申请日期</el-tag>
          </el-col>
          <el-col :span="18">{{ noticeGroup.sendTime }}</el-col>
        </el-row>
      </el-card>
      <span slot="footer" v-if="noticeGroup.verified === null">
        <el-row type="flex" justify="center" :gutter="20">
          <el-col :span="12"><el-button type="danger" plain @click="dialogRefuseGroup = true"
                                        style="border: 0; width: 100%">拒 绝</el-button></el-col>
          <el-col :span="12"><el-button type="primary" plain @click="consentGroup()"
                                        style="border: 0; width: 100%">同 意</el-button></el-col>
        </el-row>
      </span>
      <span slot="footer" v-else-if="noticeGroup.verified === 1">
        <el-button v-if="noticeGroup.confirm === 1" type="primary" disabled plain
                   style="width: 100%; border: 0">已同意</el-button>
        <el-button v-else-if="noticeGroup.confirm === 0" type="danger" disabled plain
                   style="width: 100%; border: 0">已拒绝</el-button>
        <el-button v-else type="danger" disabled plain style="width: 100%; border: 0">未知错误</el-button>
      </span>
      <span slot="footer" v-else>
        <el-button type="danger" disabled plain style="width: 100%; border: 0">未知错误</el-button>
      </span>

      <!-- 拒绝确认以及原因 -->
      <el-dialog
          class="friend-notice-dialog"
          :show-close="false"
          :visible.sync="dialogRefuseGroup"
          append-to-body
          width="30%">
        <el-card class="friend-notice">
          <el-row>
            拒绝 <b>{{ noticeGroup.senderNickname }}</b> 的入群申请
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
                  show-word-limit>
              </el-input>
            </el-col>
          </el-row>
        </el-card>

        <span slot="footer">
        <el-row type="flex" justify="center" :gutter="20">
          <el-button type="danger" plain @click="refuseGroup()" style="border: 0; width: 95%">确定拒绝</el-button>
        </el-row>
      </span>
      </el-dialog>
    </el-dialog>
    <!-- 群反馈通知 -->
    <el-dialog
        v-else
        :show-close="false"
        :visible.sync="dialogGroupNotice"
        width="30%">
      <span slot="title">{{ noticeGroup.title }}</span>

      <el-card>

        <el-row>
          <el-col :span="4"><img style="width: 40px; height: 40px; border-radius: 3px"
                                 :src="noticeGroup.avatarUrl" alt=""></el-col>
          <el-col :span="20">
            <b>{{ noticeGroup.senderNickname }}</b>
            <span v-if="noticeGroup.confirm === 0">&nbsp;已拒绝你的入群申请！</span>
            <span v-else>&nbsp;已同意你的入群申请！</span>
          </el-col>
        </el-row>
        <el-divider/>
        <el-row>
          <el-col :span="6">
            <el-tag size="medium">群名</el-tag>
          </el-col>
          <el-col :span="18">{{ noticeGroup.groupName }}</el-col>
        </el-row>
        <el-row v-if="noticeGroup.confirm === 0">
          <el-col :span="6">
            <el-tag size="medium">拒绝原因</el-tag>
          </el-col>
          <el-col :span="18" v-if="noticeGroup.content != null">{{ noticeGroup.content }}</el-col>
          <el-col :span="18" v-else style="color: #8c939d">( 空 )</el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-tag size="medium">反馈日期</el-tag>
          </el-col>
          <el-col :span="18">{{ noticeGroup.sendTime }}</el-col>
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
      dialogFriendNotice: false,
      dialogRefuseFriend: false,
      dialogGroupNotice: false,
      dialogRefuseGroup: false,
      noticeFriend: {},
      resultFriendNotice: {},
      noticeGroup: {},
      resultGroupNotice: {},
      text: '',
    }
  },

  computed: {
    ...mapState({
      noticeList: 'noticeList',
      friendList: 'friendList',
      currentLogin: 'currentLogin',
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
      this.dialogFriendNotice = true;
      this.noticeFriend = notice;
      if (notice.add === 1) {
        let user = this.currentLogin;
        this.resultFriendNotice.sendUsername = user.username;
        this.resultFriendNotice.sendNickname = user.nickname;
        this.resultFriendNotice.receiveUsername = notice.sendUsername;
        this.resultFriendNotice.avatarUrl = user.avatar;
        this.resultFriendNotice.flagTime = notice.flagTime;
        this.resultFriendNotice.businessType = 'add';
      } else if (notice.del === 1) {

      }
    },
    refuseFriend() {
      this.dialogRefuseFriend = false;
      let notice = this.noticeFriend;
      let resultFriendNotice = this.resultFriendNotice;
      notice.confirm = 0;
      notice.verified = 1;
      resultFriendNotice.confirm = 0;
      resultFriendNotice.verified = 1;
      resultFriendNotice.result = false;
      resultFriendNotice.flag = 1;
      resultFriendNotice.comment = this.text;
      resultFriendNotice.sendTime = new Date().format("yyyy-MM-dd hh:mm:ss");
      this.$store.state.stomp.send('/ws/friend/send', {}, JSON.stringify(resultFriendNotice));
      this.$message.error("已拒绝 " + resultFriendNotice.receiveUsername + "的好友请求")
      this.dialogFriendNotice = false;
    },
    consentFriend() {
      let notice = this.noticeFriend;
      let resultFriendNotice = this.resultFriendNotice;
      notice.confirm = 1;
      notice.verified = 1;
      resultFriendNotice.confirm = 1;
      resultFriendNotice.verified = 1;
      resultFriendNotice.result = true;
      resultFriendNotice.flag = 1;
      resultFriendNotice.sendTime = new Date().format("yyyy-MM-dd hh:mm:ss");
      this.$store.state.stomp.send('/ws/friend/send', {}, JSON.stringify(resultFriendNotice));
      this.$message.success("已同意 " + resultFriendNotice.receiveUsername + "的好友请求")
      this.getRequest('/friend/list?username=' + this.currentLogin.username).then(friendList => {
        this.$store.commit('INIT_FRIEND_LIST', friendList)
      })
      this.dialogFriendNotice = false;
    },

    showGroupNotice(notice) {
      this.noticeGroup = notice;
      if (notice.join) {
        let user = this.currentLogin;
        this.resultGroupNotice.senderUsername = user.username;
        this.resultGroupNotice.senderNickname = user.nickname;
        this.resultGroupNotice.avatarUrl = user.avatar;
        this.resultGroupNotice.receiverUsername = notice.senderUsername;
        this.resultGroupNotice.receiverNickname = notice.senderNickname;
        this.resultGroupNotice.flagTime = notice.flagTime;
        this.resultGroupNotice.groupName = notice.groupName;
        this.resultGroupNotice.gid = notice.gid;
        this.resultGroupNotice.businessType = 'join';
        console.log(this.resultGroupNotice)
      } else if (notice.quit) {

      } else if (notice.forceQuit) {

      }
      this.dialogGroupNotice = true;
    },
    refuseGroup() {
      let notice = this.noticeGroup;
      let resultGroupNotice = this.resultGroupNotice;
      notice.confirm = 0;
      notice.verified = 1;
      resultGroupNotice.confirm = 0;
      resultGroupNotice.flag = 0;
      resultGroupNotice.title = "已拒绝你的入群申请";
      resultGroupNotice.content = this.text;
      resultGroupNotice.sendTime = new Date().format("yyyy-MM-dd hh:mm:ss");
      this.$message.error("已拒绝 " + resultGroupNotice.receiverNickname + " 的入群申请")
      console.log(resultGroupNotice)
      this.$store.state.stomp.send('/ws/group/send', {}, JSON.stringify(resultGroupNotice));
      this.dialogRefuseGroup = false;
    },
    consentGroup() {
      let notice = this.noticeGroup;
      let resultGroupNotice = this.resultGroupNotice;
      notice.confirm = 1;
      notice.verified = 1;
      resultGroupNotice.confirm = 1;
      resultGroupNotice.verified = 1;
      resultGroupNotice.flag = 1;
      resultGroupNotice.title = "已同意你的入群申请";
      resultGroupNotice.sendTime = new Date().format("yyyy-MM-dd hh:mm:ss");
      this.$store.state.stomp.send('/ws/group/send', {}, JSON.stringify(resultGroupNotice));
      this.$message.success("已同意 " + resultGroupNotice.receiverNickname + " 的入群申请")
      this.getRequest('/group/list?username=' + this.currentLogin.username).then(groupList => {
        this.$store.commit('INIT_GROUP_LIST', groupList)
      })
      this.dialogFriendNotice = false;
    }
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