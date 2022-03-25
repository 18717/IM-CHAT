<template>
  <div v-if="chatType !== null" id="textinput">
    <div class="chat-plug">
      <el-col :span="1">
        <el-button type="text"><i class="el-icon-setting"></i></el-button>
      </el-col>
      <el-col :span="1">
        <el-form>
          <el-upload
              class="upload"
              :headers="headers"
              action="/common/upload/file"
              :data="param"
              :on-success="sendFile"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload">
            <el-button slot="trigger" type="text"><i class="el-icon-folder"></i></el-button>
          </el-upload>
        </el-form>
      </el-col>
      <el-col :span="1">
        <el-button type="text"><i class="el-icon-time"></i></el-button>
      </el-col>
    </div>
    <div class="text-div">
      <textarea class="scrollbar" placeholder="按 Ctrl + Enter 发送" v-model="content" v-on:keyup="addMessage"></textarea>
    </div>
    <div class="btn-div">
      <el-button v-on:click="addMessage('send')" type="success" size="mini" plain>发送【S】</el-button>
    </div>
  </div>
</template>

<script>
import {mapState} from "vuex";
import {Message} from "element-ui";

export default {
  name: "TextInput",
  data() {
    return {
      headers: {Authorization: window.sessionStorage.getItem('token')},
      content: '',
      filename: null,
      param: {type: 'file'},
      loginInfo: null,
    }
  },
  computed: mapState([
    'currentLogin',
    'currentUser',
    'currentGroup',
    'chatType',
  ]),
  mounted() {
    this.loginInfo = JSON.parse(window.sessionStorage.getItem('login-user'));
  },
  methods: {
    addMessage(e) {
      let msgObj = {};
      if (e.ctrlKey && e.keyCode === 13 || e === 'send') {
        if (this.chatType == null) {
          Message.error("请选择发送消息的对象！")
        } else if (this.content.length === 0) {
          Message.error("请输入需要发送的内容！")
        } else if (this.chatType === 'private') {
          msgObj.avatar = this.currentLogin.avatar;
          msgObj.receiveUsername = this.currentUser.username;
          msgObj.messageContentType = "text"
          msgObj.content = this.content;
          msgObj.fileUrl = "";
          msgObj.sendTimeStr = new Date().format("yyyy-MM-dd hh:mm:ss");
          msgObj.self = 1;
          this.$store.commit('addMessage', msgObj);
          this.$store.state.stomp.send('/ws/client/chat', {}, JSON.stringify(msgObj));
        } else if (this.chatType === 'public') {
          msgObj.avatar = this.currentLogin.avatar;
          msgObj.sendNickname = this.currentLogin.nickname;
          msgObj.sendUsername = this.currentLogin.username;
          msgObj.groupMaster = this.currentGroup.masterUsername;
          msgObj.gid = this.currentGroup.gid;
          msgObj.messageContentType = 'text';
          msgObj.content = this.content;
          msgObj.fileUrl = '';
          msgObj.sendTimeStr = new Date().format("yyyy-MM-dd hh:mm:ss");
          msgObj.self = 1;
          this.$store.commit('addGroupMessage', msgObj);
          this.$store.state.stomp.send('/ws/group/chat', {}, JSON.stringify(msgObj));
        }
        this.content = "";
      }
    },
    /* 判断发送的文件大小 */
    beforeAvatarUpload(file) {
      this.filename = file.name;
      const isLt5M = file.size / 1024 / 1024 < 5;
      if (!isLt5M) {
        this.$message.error('发送的文件大小不能超过 5MB!');
        return false;
      }
      let isIMG = ['image/jpeg', 'image/gif', 'image/bmp', 'image/png'].includes(file.type);
      if (isIMG) {
        this.param.type = "img";
      } else {
        this.param.type = "file";
      }
      return isLt5M;
    },
    // 发送文件信息
    sendFile(response) {
      let sendInfo = {};
      if (this.chatType === null) {
        Message.error("请选择接收文件的对象")
      } else if (this.chatType === 'private') {
        sendInfo.receiveUsername = this.currentUser.username;
        if (this.param.type === "img") {
          sendInfo.messageContentType = "img";
          sendInfo.content = this.filename;
        } else {
          sendInfo.messageContentType = "file";
          sendInfo.content = this.filename;
        }
        sendInfo.fileUrl = response.obj;
        sendInfo.sendTimeStr = new Date().format("yyyy-MM-dd hh:mm:ss");
        sendInfo.self = 1;
        this.$store.state.stomp.send('/ws/client/chat', {}, JSON.stringify(sendInfo));
        this.$store.commit('addMessage', sendInfo)
      } else if (this.chatType === 'public') {
        sendInfo.avatar = this.currentLogin.avatar;
        sendInfo.sendNickname = this.currentLogin.nickname;
        sendInfo.sendUsername = this.currentLogin.username;
        sendInfo.groupMaster = this.currentGroup.masterUsername;
        sendInfo.gid = this.currentGroup.gid;
        if (this.param.type === 'img') {
          sendInfo.messageContentType = 'img';
        } else {
          sendInfo.messageContentType = 'file';
          sendInfo.content = this.filename;
        }
        sendInfo.fileUrl = response.obj;
        sendInfo.sendTimeStr = new Date().format("yyyy-MM-dd hh:mm:ss");
        sendInfo.self = 1;
        this.$store.state.stomp.send('/ws/group/chat', {}, JSON.stringify(sendInfo));
        this.$store.commit('addGroupMessage', sendInfo);
      }
    },
  },
}
</script>

<style>
#textinput {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 100%;
  height: 30%;
  border-top: solid 1px #DDD;
}

#textinput .chat-plug {
  width: 100%;
  height: 18px;
  padding: 0 5px;
  /*border-bottom: 1px #DDD solid;*/
}

.upload button {
  margin: 0;
  padding: 0;
  border: 0;
  width: 18px;
  height: 18px;
}

.el-upload__input {
  display: none;
}

.chat-plug .el-col {
  height: 18px;
  line-height: 18px;
  margin: 0;
  padding: 0;
}

.chat-plug button {
  margin: 0;
  padding: 0;
  border: 0;
  width: 18px;
  height: 18px;
}

.chat-plug i {
  color: #8c939d;
}

.chat-plug i:hover {
  color: #f0acf7;
}

#textinput .text-div {
  width: 100%;
  height: 112px;
}

.text-div textarea {
  background: none;
  width: 97%;
  height: 100%;
  border: none;
  outline: none;
  resize: none;
  margin: 0;
  padding-left: 10px;
  padding-right: 8px;
  padding-top: 10px;
  font-size: 16px;
  font-family: unset;
}

#textinput .btn-div {
  text-align: right;
  padding: 0 20px 5px 0;
}

.btn-div button {
  border: 0;
}
</style>