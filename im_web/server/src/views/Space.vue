<template>
  <el-main>
    <!-- 个人信息 -->
    <el-card class="m-user" shadow="hover">
      <div slot="header" class="clearfix">
        <span>个人信息</span>
        <el-button @click="dialog = true" style="float: right; padding: 3px 0" type="text">修改信息</el-button>
      </div>
      <el-row>
        <el-col :span="12">
          <el-row>
            <el-col style="margin-bottom: 10px">UID: {{ user.uid }}</el-col>
            <el-col style="margin-bottom: 10px">用户名: {{ user.username }}</el-col>
            <el-col style="margin-bottom: 10px">昵称: {{ user.nickname }}</el-col>
            <el-col style="margin-bottom: 10px">性别: {{ user.gender }}</el-col>
            <el-col style="margin-bottom: 10px">邮箱: {{ user.email }}</el-col>
            <el-col style="margin-bottom: 10px">备注: {{ user.admin ? '管理员' : '普通用户' }}</el-col>
            <el-col style="margin-bottom: 10px">账户状态: {{ user.enabled ? '禁用' : '启用' }}</el-col>
            <el-col style="margin-bottom: 10px">客户端登录状态: {{ user.login ? '在线' : '离线' }}</el-col>
            <el-col style="margin-bottom: 10px">注册时间: {{ user.createTime }}</el-col>
            <el-col style="margin-bottom: 10px">更新时间: {{ user.updateTime }}</el-col>
          </el-row>
        </el-col>
        <el-col :span="12" style="text-align: right; padding-right: 20px">
          <el-image
              style="width: 150px; height: 150px; border-radius: 5px;"
              :src="user.avatar"
          >
          </el-image>
        </el-col>
      </el-row>


    </el-card>
    <!-- 修改信息  -->
    <el-drawer
        title="修改个人信息"
        :visible.sync="dialog"
        :before-close="handleClose"
        direction="rtl"
        custom-class="demo-drawer"
        ref="drawer"
        :close-on-press-escape="true"
        size="37%"
    >
      <div class="demo-drawer__content">
        <el-form :model="user" :rules="rules" ref="ruleForm" status-icon>

          <el-form-item label="头像" :label-width="formLabelWidth" style="height: 102px">
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
          <el-form-item label="UID" :label-width="formLabelWidth">
            <el-input v-model="user.uid" autocomplete="off" disabled></el-input>
          </el-form-item>
          <el-form-item label="用户名" :label-width="formLabelWidth">
            <el-input v-model="user.username" autocomplete="off" disabled></el-input>
          </el-form-item>
          <el-form-item label="昵称" :label-width="formLabelWidth" prop="nickname">
            <el-input v-model="user.nickname" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="性别" :label-width="formLabelWidth">
            <el-select v-model="user.gender" placeholder="请选择性别">
              <el-option label="男" value="男"></el-option>
              <el-option label="女" value="女"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="邮箱" :label-width="formLabelWidth" prop="email">
            <el-input v-model="user.email" autocomplete="off"></el-input>
          </el-form-item>

        </el-form>
        <div class="demo-drawer__footer">
          <el-row type="flex" justify="space-around">
            <el-col :span="9">
              <el-button style="width: 100%" @click="cancelForm">取 消</el-button>
            </el-col>
            <el-col :span="9">
              <el-button style="width: 100%" type="primary" @click="submitEditInfo('ruleForm')" :loading="loading">
                {{ loading ? '提交中 ...' : '确 定' }}
              </el-button>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-drawer>
  </el-main>
</template>

<script>

export default {
  name: "Space",
  head: {
    title: "个人中心",
    meta: [
      {name: 'description', content: '首页'}
    ]
  },
  data() {
    return {
      headers: {Authorization: window.sessionStorage.getItem('token')},
      dialog: false,
      loading: false,
      formLabelWidth: '80px',
      user: {},
      // 上传文件参数
      param: {type: 'avatar'},
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
    this.refreshLoginInfo();
  },
  methods: {
    // 刷新登录信息
    refreshLoginInfo() {
      this.getRequest('/server/login/info').then(resp => {
        this.user = resp;
      })
    },
    // 判断用户上传头像的格式
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
    // 上传头像
    uploadSuccess(response) {
      this.user.avatar = response.obj;
    },
    // 提交修改数据的表单
    submitEditInfo(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.loading) {
            return;
          }
          this.$confirm('确定要提交表单吗？').then(_ => {
            console.log(this.user)
            this.loading = true;
            this.timer = setTimeout(() => {
              this.putRequest('/server/update/user', this.user).then(resp => {
                this.refreshLoginInfo();
              })
              // 动画关闭需要一定的时间
              setTimeout(() => {
                this.loading = false;
                this.dialog = false;
              }, 400);
            }, 1000);
          }).catch(_ => {
          });
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    // 关闭修改信息抽屉
    cancelForm() {
      this.refreshLoginInfo()
      this.loading = false;
      this.dialog = false;
      clearTimeout(this.timer);
    },
    // 关闭确认模态框
    handleClose(done) {
      this.$confirm('数据将不会被保存，确认关闭？')
          .then(_ => {
            done();
          })
          .catch(_ => {
          });
    },
  }
}
</script>

<style>
.m-user {
  width: 700px;
  margin: 5em auto 0;
  padding-bottom: 20px;
  border: 1px #eaeaea solid;
}

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