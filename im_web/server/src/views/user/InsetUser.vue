<template>
  <el-main style="padding-bottom: 0">
    <h4>添加用户</h4>
    <div style="width: 800px; margin: 0 auto;padding-top: 0">
      <el-form :model="user" status-icon :rules="rules" ref="user" label-width="100px">

        <el-form-item label="头像" prop="avatar" style="height: 100px;">
          <el-upload
              class="avatar-uploader"
              action="/common/upload/file"
              :data="param"
              :headers="headers"
              :on-success="uploadSuccess"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
          >
            <img v-if="user.avatar" :src="user.avatar" class="avatar" title="点击上传头像" alt="点击上传头像">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>

        <el-form-item label="用户名" prop="username">
          <el-input type="text" v-model="user.username" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input type="text" v-model="user.nickname" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio v-model="user.gender" label="男">男</el-radio>
          <el-radio v-model="user.gender" label="女">女</el-radio>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="user.email"></el-input>
        </el-form-item>
        <el-form-item label="账户状态">
          <el-radio v-model="user.disable" :label="1">启用</el-radio>
          <el-radio v-model="user.disable" :label="0">禁用</el-radio>
        </el-form-item>
        <el-form-item label="备注">
          <el-radio v-model="user.admin" :label="1">管理员</el-radio>
          <el-radio v-model="user.admin" :label="0">普通用户</el-radio>
        </el-form-item>
      </el-form>
      <el-row type="flex" justify="center" :gutter="100">
        <el-col :span="8">
          <el-button @click="restUserInfo('user')" type="info" size="medium" plain style="width: 270px">重 置</el-button>
        </el-col>
        <el-col :span="8">
          <el-button @click="doAddUser('user')" type="primary" size="medium" plain style="width: 270px">添 加</el-button>
        </el-col>
      </el-row>
    </div>
  </el-main>
</template>

<script>

export default {
  head: {
    title: "添加用户",
    meta: [
      {name: 'description', content: '添加用户'}
    ]
  },
  name: "InsetUser",
  data() {

    var checkUsername = (rule, value, callback) => {
      this.getRequest('/common/check/username/' + this.user.username).then(resp => {
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
      user: {
        avatar: '',
        username: '',
        nickname: '',
        gender: '',
        email: '',
        admin: '',
        disable: '',
      },
      /* 上传参数 */
      param: {type: 'avatar'},
      /* 校验规则 */
      rules: {
        username: [
          {required: true, message: '请输入用户名', trigger: 'blur'},
          {validator: checkUsername, trigger: 'blur'},
          {min: 6, max: 9, message: '长度在 6 到 9 个字符', trigger: 'blur'}
        ],
        nickname: [
          {required: true, message: '请输入昵称', trigger: 'blur'},
          {min: 3, max: 7, message: '长度在 3 到 7 个字符', trigger: 'blur'}
        ],
        email: [
          {required: true, message: '请输入邮箱地址', trigger: 'blur'},
          {type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change']}
        ],

      },
    };

  },

  methods: {
    /* 判断用户上传头像的格式 */
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
    /* 上传文件到七牛云 */
    uploadSuccess(response) {
      this.user.avatar = response.obj;
    },
    /* 添加用户 */
    doAddUser(formName) {

      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$confirm('确定要添加用户吗？').then(_ => {
            this.postRequest('/server/create/user', this.user).then(resp => {
              if (resp.code === 200) {
                this.$router.replace('/list');
              }
            })
          }).catch(_ => {
          });
        } else {
          console.log('error submit!!');
          return false;
        }
      });

    },
    /* 重置表单 */
    restUserInfo(formName) {
      this.$refs[formName].resetFields();
    }
  }
}
</script>

<style scoped>

.avatar-uploader {
  height: 102px;
}

.avatar-uploader-icon {
  font-size: 22px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
  border: 1px dashed #8c939d;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader-icon:hover {
  border: 1px dashed #409EFF;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
  border-radius: 5px;
}

</style>
