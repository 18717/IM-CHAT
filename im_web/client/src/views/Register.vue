<template>
  <el-main class="reg-box">
    <!-- 注册表单 -->
    <el-card v-if="regOkInfo === ''" class="reg">
      <div slot="header">
        <h3>注册 IM-CHAT 账号</h3>
      </div>
      <el-form :model="regInfo" :rules="rules" ref="regInfo" label-width="80px" status-icon>
        <el-form-item label="头像" style="height: 102px">
          <el-tooltip effect="dark" content="社交头像" placement="right">
            <el-upload
                class="avatar-uploader"
                action="/common/upload/file"
                :data="param"
                :headers="headers"
                :on-success="uploadSuccess"
                :show-file-list="false"
                :before-upload="beforeAvatarUpload"
            >
              <img v-if="regInfo.avatar" :src="regInfo.avatar" class="avatar" title="点击上传头像" alt="点击上传头像">
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-tooltip>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-tooltip class="item" effect="dark" content="用户名作为登录账号" placement="right">
            <el-input v-model="regInfo.username" placeholder="请输入用户名" autocomplete="off"></el-input>
          </el-tooltip>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-tooltip class="item" effect="dark" content="账号登录密码" placement="right">
            <el-input type="password" v-model="regInfo.password" placeholder="请输入密码" autocomplete="off"></el-input>
          </el-tooltip>
        </el-form-item>
        <el-form-item label="确认密码" prop="checkPwd">
          <el-tooltip class="item" effect="dark" content="再次输入密码" placement="right">
            <el-input type="password" v-model="regInfo.checkPwd" placeholder="再次输入密码" autocomplete="off"></el-input>
          </el-tooltip>
        </el-form-item>
        <el-form-item label="社交昵称" prop="nickname">
          <el-tooltip class="item" effect="dark" content="社交昵称" placement="right">
            <el-input v-model="regInfo.nickname" placeholder="请输入社交昵称" autocomplete="off"></el-input>
          </el-tooltip>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="regInfo.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-tooltip class="item" effect="dark" content="用于找回密码" placement="right">
            <el-input type="email" v-model="regInfo.email" placeholder="请输入邮箱" autocomplete="off"></el-input>
          </el-tooltip>
        </el-form-item>
        <el-form-item>
          <el-row type="flex" justify="center">
            <el-col :span="6">
              <router-link to="/">
                <el-button type="info" @click="resetForm('regInfo')" plain style="border: 0">取消注册</el-button>
              </router-link>
            </el-col>
            <el-col :span="6">
              <el-button type="warning" @click="resetForm('regInfo')" plain style="border: 0">重置</el-button>
            </el-col>
            <el-col :span="6">
              <el-button type="primary" @click="toReg('regInfo')" plain style="border: 0">立即注册</el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
    </el-card>
    <!-- 注册成功的反馈信息 -->
    <el-card v-else class="reg-ok" shadow="hover">
      <el-result icon="success" title="感谢您注册IM-CHAT账号">
        <template slot="extra">
          <el-descriptions class="info" :column="1">
            <el-descriptions-item label="UID">{{ regOkInfo.uid }}</el-descriptions-item>
            <el-descriptions-item label="登录账号">{{ regOkInfo.username }}</el-descriptions-item>
          </el-descriptions>
          <p style="color: red">登录账号是登录的唯一凭证，请牢记您的登录账号！</p>
          <router-link to="/">
            <el-button type="primary" size="medium" plain style="border: 0">前往登录</el-button>
          </router-link>
        </template>
      </el-result>
    </el-card>
  </el-main>
</template>

<script>

import {Message} from "element-ui";

export default {
  head: {
    title: "注册IM-CHAT账号",
    meta: [
      {name: 'description', content: '注册IM-CHAT账号'}
    ]
  },
  name: "Register",

  data() {

    var validatePass = (rule, value, callback) => {
      if (value === '') {
        return callback(new Error('密码不能为空'));
      } else {
        if (this.regInfo.checkPwd !== '') {
          this.$refs.regInfo.validateField('checkPwd');
        }
        callback();
      }
    };
    var validatePass2 = (rule, value, callback) => {
      if (value === '') {
        return callback(new Error('请再次输入密码'));
      } else if (value !== this.regInfo.password) {
        return callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    var checkUsername = (rule, value, callback) => {
      this.getRequest('/common/check/username/' + this.regInfo.username).then(resp => {
        if (resp) {
          callback(new Error("用户名已经存在"));
        } else {
          if (/[\u4E00-\u9FA5]/g.test(value)) {
            callback(new Error('用户名不能含有中文'));
          } else {
            callback();
          }
        }
      })
    };

    return {
      headers: {Authorization: window.sessionStorage.getItem('token')},
      regOkInfo: '',
      checkResult: false,
      regInfo: {
        avatar: '',
        username: '',
        password: '',
        checkPwd: '',
        nickname: '',
        email: '',
        gender: '',

      },
      // 上传文件参数
      param: {type: 'avatar'},
      rules: {
        email: [
          {required: true, message: '请输入邮箱地址', trigger: 'blur'},
          {type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change']}
        ],
        nickname: [
          {required: true, message: '请输入社交昵称', trigger: 'blur'},
          {min: 3, max: 7, message: '长度在 3 到 7 个字符', trigger: 'blur'}
        ],
        password: [
          {required: true, validator: validatePass, trigger: 'blur'}
        ],
        checkPwd: [
          {required: true, validator: validatePass2, trigger: 'blur'}
        ],
        username: [
          {required: true, message: '用户名不能为空', trigger: 'blur'},
          {validator: checkUsername, trigger: 'blur'},
          {min: 6, max: 9, message: '长度在 6 到 9 个字符', trigger: 'blur'},
        ],
      }
    };

  },

  methods: {

    toReg(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.postRequest('/client/register', this.regInfo).then(resp => {
            this.regOkInfo = resp.data;
          })
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },

    // 判断用户上传头像的格式和大小
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
    //上传文件
    uploadSuccess(response) {
      if (response.code !== 200) {
        Message.error(response.message)
      } else {
        this.regInfo.avatar = response.data;
      }
    },
    // 重置
    resetForm(formName) {
      this.$refs[formName].resetFields();
      this.regInfo.avatar = '';
    },

  },

}
</script>

<style>

.reg-box {
  width: 800px;
  margin: 0 auto;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
  border-radius: 5px;
}

.avatar-uploader .avatar-uploader-icon:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 26px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px !important;
  text-align: center;
  border: 1px #8c939d dashed;
  border-radius: 3px;
}

.reg-ok {
  display: block !important;
  margin: 10% auto 0;
  border: 3px;
  border-radius: 3px;
}
.reg-ok .info {
  width: 30%;
  margin: 0 auto;
}

</style>
