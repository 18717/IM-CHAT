<template>
  <el-main>
    <h4>用户列表</h4>
    <!-- 搜索框 -->
    <div style="width: 800px; margin: 0 auto">
      <el-input v-model="query.content"
                @keydown.enter.native="initUserList"
                @clear="initUserList"
                @blur="initUserList"
                clearable
                placeholder="请输入关键词">
        <el-select v-model="query.condition" slot="prepend" placeholder="请选择" style="width: 90px">
          <el-option label="UID" value="uid"></el-option>
          <el-option label="昵称" value="nickname"></el-option>
          <el-option label="邮箱" value="email"></el-option>
        </el-select>
        <el-button slot="append" icon="el-icon-search" @click="initUserList">搜索</el-button>
      </el-input>
    </div>

    <!-- 用户信息 -->
    <div class="user-container">
      <el-card class="user-card" v-for="(user, index) in users" :key="index" shadow="hover">
        <div slot="header" class="clearfix">
          <el-row>
            <el-col :span="12" style="line-height: 40px"><i class="el-icon-user-solid"></i> {{ user.nickname }}</el-col>
            <el-col :span="12">
              <el-dropdown trigger="click" placement="right" style="float: right; padding: 3px 0">
                <el-button type="text">更多操作</el-button>
                <el-dropdown-menu slot="dropdown">
                  <span @click="showEditUser(user)"><el-dropdown-item icon="el-icon-edit"
                                                                      style="color: #0000ff">编辑信息</el-dropdown-item></span>
                  <span @click="showSendMsgUser(user)"><el-dropdown-item icon="el-icon-message"
                                                                         style="color: #008000">发送消息</el-dropdown-item></span>
                  <span @click="showRestPsw(user)"><el-dropdown-item icon="el-icon-refresh"
                                                                     style="color: #ffA500">重置密码</el-dropdown-item></span>
                  <span @click="showDelUser(user)"><el-dropdown-item icon="el-icon-delete"
                                                                     style="color: #ff0000">删除用户 </el-dropdown-item></span>
                </el-dropdown-menu>
              </el-dropdown>
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
            <div>更新时间：{{ user.updateTime }}</div>
            <div>备注：
              <el-tag size="small">
                {{ user.admin ? '管理员' : '普通用户' }}
              </el-tag>
            </div>
            <div>登录状态：
              <el-tag size="small" :type="user.login ? 'success' : 'danger'">
                {{ user.login ? '在线' : '离线' }}
              </el-tag>
            </div>
            <div>账号禁用状态：
              <el-tag size="small" :type=" user.enabled ? 'danger' : 'success' ">
                {{ user.enabled ? '禁用' : '正常使用' }}
              </el-tag>
            </div>
            <div>账号注销状态：
              <el-tag size="small" :type="user.deleted ? 'danger' : 'success'">
                {{ user.deleted ? '已注销' : '正常使用' }}
              </el-tag>
            </div>

          </el-col>
          <el-col :span="5" class="img-container">
            <img class="user-avatar" :src="user.avatar" :alt="user.nickname" :title="user.nickname">
          </el-col>
        </el-row>
      </el-card>
    </div>

    <!--  分页  -->
    <el-pagination
        @current-change="currentChange"
        background
        :page-size="page.pageSize"
        layout="prev, pager, next, jumper, ->, total"
        :total="page.total">
    </el-pagination>
    <!-- Edit -->
    <el-dialog
        :visible.sync="dialogEdit"
        width="45%">
        <el-form :model="temUser" status-icon :rules="rules" ref="tempUser" label-width="100px">
          <el-form-item label="头像" style="height: 102px">
            <el-upload
                class="avatar-uploader"
                action="/common/upload/file"
                :data="param"
                :headers="headers"
                :on-success="uploadSuccess"
                :show-file-list="false"
                :before-upload="beforeAvatarUpload"
            >
              <img v-if="temUser.avatar" :src="temUser.avatar" class="avatar" title="点击上传头像" alt="点击上传头像">
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-form-item>
          <el-form-item label="昵称" prop="nickname">
            <el-input type="text" v-model="temUser.nickname" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="temUser.email"></el-input>
          </el-form-item>
          <el-form-item label="性别">
            <el-radio v-model="temUser.gender" label="男">男</el-radio>
            <el-radio v-model="temUser.gender" label="女">女</el-radio>
          </el-form-item>
          <el-form-item label="备注">
            <el-radio v-model="temUser.admin" :label="true">管理员</el-radio>
            <el-radio v-model="temUser.admin" :label="false">普通用户</el-radio>
          </el-form-item>
          <el-form-item label="账户状态">
            <el-radio v-model="temUser.enabled" :label="false">启用</el-radio>
            <el-radio v-model="temUser.enabled" :label="true">禁用</el-radio>
          </el-form-item>
          <el-form-item label="注销状态">
            <el-radio v-model="temUser.deleted" :label="true">是</el-radio>
            <el-radio v-model="temUser.deleted" :label="false">否</el-radio>
          </el-form-item>
        </el-form>
     <el-row gutter="20">
       <el-col :span="12"><el-button style="width: 100%" @click="dialogEdit = false" type="info" size="medium" plain>取 消</el-button></el-col>
       <el-col :span="12"><el-button style="width: 100%" @click="doEditUser('tempUser')" type="primary" size="medium" plain>确 定</el-button></el-col>
     </el-row>

    </el-dialog>
    <!-- Del -->
    <el-dialog
        title="删除用户"
        :visible.sync="dialogDel"
        width="30%">
      <span>是否永久删除该用户 【{{ temUser.nickname }}】 此操作无法撤销！</span>
      <span slot="footer" class="dialog-footer">
                  <el-button @click="dialogDel = false" type="info" size="medium" plain>取 消</el-button>
                  <el-button @click="doDelUser" type="danger" size="medium" plain>删 除</el-button>
          </span>
    </el-dialog>
    <!-- SendMessage -->
    <el-dialog
        title="发送消息"
        :visible.sync="dialogSend"
        width="50%">
      <el-card>
        <el-form ref="form" label-position="left" :model="infoData" label-width="80px">
          <el-form-item label="标题">
            <el-input v-model="infoData.title"></el-input>
          </el-form-item>
          <el-form-item label="文件链接">
            <el-upload
                v-if="infoData.fileName === ''"
                action="/common/upload/file"
                :data="param2"
                :headers="headers"
                :on-success="uploadSuccess2"
                :show-file-list="true"
                :before-upload="beforeAvatarUpload2">
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
          <el-form-item label="推送内容">
            <el-input
                type="textarea"
                :rows="3"
                placeholder="请输入内容"
                maxlength="30"
                v-model="infoData.content">
            </el-input>
          </el-form-item>
        </el-form>
      </el-card>
      <span slot="footer" class="dialog-footer">
          <el-button @click="dialogSend = false" type="info" size="medium" plain>取 消</el-button>
          <el-button @click="doSendMsg" type="success"
                     size="medium" plain>{{ 'To：' + temUser.nickname }}</el-button>
        </span>
    </el-dialog>
    <!-- RestPassword -->
    <el-dialog
        title="重置密码"
        :visible.sync="dialogRestPsw"
        width="30%">
      <span>是否重置该用户 【{{ temUser.nickname }}】 的密码?</span>
      <span slot="footer" class="dialog-footer">
                  <el-button @click="dialogRestPsw = false" type="info" size="medium" plain>取 消</el-button>
                  <el-button @click="doResetPwd" type="danger" size="medium" plain>重 置</el-button>
          </span>
    </el-dialog>
  </el-main>
</template>

<script>

import {convertTime} from "../../../client/src/api/api";

export default {
  head: {
    title: "用户列表",
    meta: [
      {name: 'description', content: '用户列表'}
    ]
  },
  name: "ListUser",
  data() {
    return {
      headers: {Authorization: window.sessionStorage.getItem('token')},
      dialogEdit: false,
      dialogDel: false,
      dialogSend: false,
      dialogOff: false,
      dialogRestPsw: false,
      users: [],
      temUser: {nickname: '',},
      // 分页参数
      page: {total: 0, currentPage: 1, pageSize: 8,},
      // 查询参数
      query: {condition: 'uid', content: ''},
      // 上传参数
      param: {type: 'avatar'},
      // 通知参数
      infoData: {push: '', receiveUsername: '', title: '', fileUrl: '', content: '', fileName: ''},
      param2: {type: 'notice_file'},
      // 校验规则
      rules: {
        email: [
          {required: true, message: '请输入邮箱地址', trigger: 'blur'},
          {type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change']}
        ],
        nickname: [
          {required: true, message: '请输入昵称', trigger: 'blur'},
          {min: 3, max: 7, message: '长度在 3 到 7 个字符', trigger: 'blur'}
        ],
      },
    };
  },
  mounted() {
    this.initUserList();
  },
  methods: {
    // 初始化用户列表
    initUserList() {
      this.getRequest('/server/search/page/?currentPage=' + this.page.currentPage + '&size=' + this.page.pageSize + '&' + this.query.condition + '=' + this.query.content).then(resp => {
        if (resp) {
          for (var i = 0; i< resp.data.length; i++) {
            resp.data[i].createTime = convertTime(resp.data[i].createTime);
            resp.data[i].updateTime = convertTime(resp.data[i].updateTime);
          }
          this.users = resp.data;
          this.page.total = resp.total;
        }
      })
    },
    // 分页
    currentChange(currentPage) {
      this.page.currentPage = currentPage;
      this.initUserList();
    },
    // 编辑用户
    showEditUser(user) {
      this.dialogEdit = true;
      this.temUser = user;
    },
    doEditUser() {
      this.putRequest('/server/update/user', this.temUser).then(resp => {
        this.initUserList();
      })
      this.dialogEdit = false;
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg';
      const isLt1M = file.size / 1024 / 1024 < 1;

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG/JPEG 格式!');
        return false;
      }
      if (!isLt1M) {
        this.$message.error('上传头像图片大小不能超过 1MB!');
        return false;
      }
      return isJPG && isLt1M;
    },
    uploadSuccess(response) {
      this.temUser.avatar = response.obj;
    },
    // 删除用户
    showDelUser(user) {
      this.dialogDel = true;
      this.temUser = user;
    },
    doDelUser() {
      this.deleteRequest('/server/delete/real/' + this.temUser.username).then(resp => {
        this.initUserList();
      })
      this.dialogDel = false;
    },
    // 发送消息给指定用户
    showSendMsgUser(user) {
      this.dialogSend = true;
      this.temUser = user;
      this.infoData.push = 'checked';
      this.infoData.receiveUsername = "," + user.username + ",";
    },
    doSendMsg() {
      this.infoData.pushTime = new Date().format("yyyy-MM-dd hh:mm:ss");
      this.$store.state.stomp.send('/ws/server/notice', {}, JSON.stringify(this.infoData));
      this.$message.success('发送成功！');
      this.dialogSend = false;
      this.infoData = {push: '', receiveUsername: '', title: '', fileUrl: '', content: '', fileName: ''};
    },
    clearFile() {
      this.infoData.fileName = '';
      this.infoData.fileUrl = '';
    },
    beforeAvatarUpload2(file) {
      const isLt5M = file.size / 1024 / 1024 < 5;
      if (!isLt5M) {
        this.$message.error('上传文件的大小不能超过5MB');
        return false;
      }
      this.infoData.fileName = file.name;
      return isLt5M;
    },
    uploadSuccess2(response) {
      this.infoData.fileUrl = response.obj;
    },
    doForcedOffline() {
      this.putRequest('/server/forced/offline/' + this.temUser.username).then(resp => {
        this.initUserList();
      })
      this.dialogOff = false
    },
    // 重置密码
    showRestPsw(user) {
      this.dialogRestPsw = true;
      this.temUser = user;
    },
    doResetPwd() {
      this.putRequest('/server/update/password/' + this.temUser.username).then(resp => {
        if (resp.code === 200) {
          this.initUserList();
        }
      })
      this.dialogRestPsw = false;
    },
    /* 取消操作确认框 */
    handleClose(done) {
      this.$confirm('数据将不会被保存，确认关闭？').then(_ => {
        this.dialogEdit = false;
        this.initUserList();
        done();
      }).catch(_ => {
      });
    },
  },
}
</script>

<style>
.el-pagination {
  text-align: right;
  padding: 10px 0 0 0;
}

.el-textarea textarea {
  font-family: "HarmonyOS_Sans_Regular", sans-serif;
  resize: none;
  overflow: auto;
}

.avatar-uploader .avatar-uploader-icon:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
  border: 1px #8c939d dashed;
  border-radius: 5px;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
  border-radius: 5px;
}

.user-container {
  display: flex;
  justify-content: space-around;
  margin-top: 70px;
  flex-wrap: wrap;
}

.user-card {
  width: 300px;
  margin-bottom: 25px;
}

.user-card .el-card__header {
  padding: 7px 20px;
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
  margin-bottom: 5px;
}


</style>
