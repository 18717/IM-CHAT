<template>
  <el-main>
    <el-card id="notice-card">
      <div slot="header">
        <h3>推送消息</h3>
      </div>
      <div class="text">
        <el-form ref="form" label-position="left" :model="infoData" label-width="80px">
          <el-form-item style="display: inline-block; width: 30%" label="推送对象">
            <el-select v-model="infoData.noticeType" @change="setReceiveUser" placeholder="请选择推送对象">
              <el-option label="指定用户（慎用，不友好体验）" value="checked"></el-option>
              <el-option label="全部用户" value="all"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item
              style="display: inline-block"
              label="选中用户"
              v-if="infoData.noticeType === 'checked'">
            <el-select
                v-model="checkedUser"
                @change="setReceiveUser"
                multiple
                collapse-tags
                style="margin: 0; padding: 0"
                placeholder="请选择要推送的用户">
              <el-option
                  v-for="item in userList"
                  :value="item">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item style="display: inline-block; width: 30%">
            <el-button style="border: 0" type="success" plain @click="sendNotice">推送消息 <i
                class="el-icon-s-promotion"></i></el-button>
          </el-form-item>
          <el-form-item label="标题">
            <el-input v-model="infoData.title"></el-input>
          </el-form-item>
          <el-form-item label="文件链接">
            <el-upload
                v-if="infoData.fileName === ''"
                action="/common/upload/file"
                :data="param"
                :headers="headers"
                :on-success="uploadSuccess"
                :show-file-list="true"
                :before-upload="beforeAvatarUpload">
              <el-button size="small" type="primary" plain style="border: 0">
                上传文件 <i class="el-icon-upload2"></i>
              </el-button>
              <span style="font-size: 12px; color: red"> <b>（上传的文件大小不能超过5MB）</b> </span>
            </el-upload>
            <li v-else class="file-list">
              <div class="t1"><i class="el-icon-document-checked"></i> {{ infoData.fileName }}</div>
              <div class="t2">
                <el-button type="text" size="medium" icon="el-icon-circle-close" plain @click="clearFile()"></el-button>
              </div>
            </li>
          </el-form-item>
          <el-form-item label="推送内容" style="height: 270px !important;">
            <mavon-editor
                v-model="infoData.content"
            />
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </el-main>
</template>

<script>
import {mavonEditor} from "mavon-editor";
import "mavon-editor/dist/css/index.css";
import {Message} from "element-ui";

export default {
  name: "Notice",
  head: {
    title: "推送消息",
    meta: [
      {name: 'description', content: '推送消息'}
    ]
  },
  data() {
    return {
      headers: {Authorization: window.sessionStorage.getItem('token')},
      // 上传文件参数
      param: {type: 'notice_file'},
      infoData: {noticeType: '', sender: '', receiver: '', title: '', fileUrl: '', content: '', fileName: ''},
      userList: [],
      checkedUser: [],
      user: {},
    }
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.getRequest('/server/get/usernames').then(resp => {
        this.userList = resp;
      })
      this.user = JSON.parse(window.sessionStorage.getItem('login-user'));
    },
    setReceiveUser() {
      let usernames = "";
      if (this.infoData.noticeType === 'all') {
        this.infoData.receiver = 'all';
      } else {
        let userList = this.checkedUser;
        usernames = "," + userList[0] + ",";
        for (let i = 1; i < userList.length; i++) {
          usernames = usernames + userList[i] + ",";
        }
        this.infoData.receiver = usernames;
      }
    },
    uploadSuccess(response) {
      this.infoData.fileUrl = response.data;
    },
    beforeAvatarUpload(file) {
      const isLt5M = file.size / 1024 / 1024 < 5;
      if (!isLt5M) {
        this.$message.error('上传文件的大小不能超过5MB');
        return false;
      }
      this.infoData.fileName = file.name;
      return isLt5M;
    },
    sendNotice() {
      var noticeServer = this.infoData;
      if (noticeServer.noticeType === '') {
        Message.error('请选择推送对象');
      } else if (noticeServer.noticeType === 'checked' || noticeServer.noticeType === 'all') {
        if (noticeServer.receiver === '') {
          Message.error('请选择接收的用户');
        } else if (noticeServer.title === '') {
          Message.error("请输入标题！")
        } else if (noticeServer.content === '') {
          Message.error("请输入内容！")
        } else {
          noticeServer.sender = this.user.username;
          noticeServer.time = new Date().format("yyyy-MM-dd hh:mm:ss");
          this.$store.state.stomp.send('/ws/server/notice', {}, JSON.stringify(noticeServer));
          Message.success('推送成功！');
          this.infoData = {};
          this.clearFile();
        }
      } else {
        Message.error("非法参数！")
      }
    },
    clearFile() {
      this.infoData.fileName = '';
      this.infoData.fileUrl = '';
    },
  },
  components: {
    mavonEditor
  },
}
</script>

<style>
h3 {
  margin: 0;
  padding: 0;
}

.file-list {
  width: 50%;
  border-radius: 5px;
  list-style: none;
}

.file-list:hover {
  background: #eaeaea;
}

.file-list .t1 {
  display: inline-block;
  width: 65%;
  height: 40px;
}

.file-list .t2 {
  display: inline-block;
  width: 30%;
  text-align: right;
}

.file-list .t2 button {
  border: 0;
  background: none !important;
  color: #8c939d;
}

#notice-card .el-card__body {
  height: 490px;
}

.scroll-style {
  height: 270px;
}

.v-note-panel {
  max-height: 270px;
}
</style>