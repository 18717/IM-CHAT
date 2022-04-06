<template>
  <div id="set">
    <el-header class="chat-set-header">
      <h5><i class="el-icon-menu"></i>&emsp;个人中心</h5>
    </el-header>

    <!-- 功能列 -->
    <el-main class="chat-set-main scrollbar">
      <div class="setItem">
        <el-button @click="editInfo = true">
          <i class="el-icon-edit-outline"></i> <span>修改基本信息</span>
        </el-button>
      </div>

      <div class="setItem">
        <el-button @click="editPsw = true">
          <i class="el-icon-key"></i> <span>修改密码</span>
        </el-button>
      </div>

      <div class="setItem">
        <el-button @click="logout">
          <i class="el-icon-circle-close"></i> <span>退出登录</span>
        </el-button>
      </div>
    </el-main>

    <!-- 修改基本信息 -->
    <el-drawer
        title="修改基本信息"
        :visible.sync="editInfo"
        :before-close="handleClose"
        direction="ltr"
        custom-class="drawer"
        ref="drawer"
        :close-on-press-escape="true"
        size="38%"
    >
      <div class="drawer__content">
        <el-form :model="loginInfo" :rules="rules" ref="loginInfo" status-icon>

          <el-form-item label="头像" :label-width="formLabelWidth" style="height: 102px">
            <el-upload
                :headers="headers"
                class="avatar-uploader"
                action="/common/upload/file"
                :data="param"
                :on-success="uploadSuccess"
                :show-file-list="false"
                :before-upload="beforeAvatarUpload"
            >
              <img v-if="loginInfo.avatar" :src="loginInfo.avatar" class="avatar" title="点击上传头像" alt="点击上传头像">
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-form-item>
          <el-form-item label="UID" :label-width="formLabelWidth">
            <el-input v-model="loginInfo.uid" autocomplete="off" disabled></el-input>
          </el-form-item>
          <el-form-item label="用户名" :label-width="formLabelWidth">
            <el-input v-model="loginInfo.username" autocomplete="off" disabled></el-input>
          </el-form-item>
          <el-form-item label="昵称" :label-width="formLabelWidth" prop="nickname">
            <el-input v-model="loginInfo.nickname" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="性别" :label-width="formLabelWidth">
            <el-select v-model="loginInfo.gender" placeholder="请选择性别">
              <el-option label="男" value="男"></el-option>
              <el-option label="女" value="女"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="邮箱" :label-width="formLabelWidth" prop="email">
            <el-input v-model="loginInfo.email" autocomplete="off"></el-input>
          </el-form-item>

        </el-form>
        <div class="drawer__footer">
          <el-row type="flex" justify="space-around">
            <el-col :span="9">
              <el-button style="width: 100%" @click="cancelForm">取 消</el-button>
            </el-col>
            <el-col :span="9">
              <el-button style="width: 100%" type="primary" @click="submitEditInfo('loginInfo')" :loading="loading">
                {{ loading ? '提交中 ...' : '确 定' }}
              </el-button>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-drawer>
    <!-- 修改密码 -->
    <el-dialog
        title="修改密码"
        :visible.sync="editPsw"
        width="30%">

      <el-form :model="pswInfo" status-icon :rules="pswRules" ref="pswInfo" label-width="100px" class="pswForm">
        <el-form-item label="旧密码" prop="old">
          <el-input type="password" v-model="pswInfo.old" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="fresh">
          <el-input type="password" v-model="pswInfo.fresh" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="check">
          <el-input type="password" v-model="pswInfo.check" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button type="info" @click="editPsw = false" plain>取 消</el-button>
        <el-button type="primary" @click="submitEditPsw('pswInfo')" plain>确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import {mapState} from "vuex";
import {Message} from "element-ui";

export default {
  name: "Setting",

  data() {
    var checkOld = (rule, value, callback) => {
      if (/[\u4E00-\u9FA5]/g.test(value)) {
        callback(new Error('密码不能含有中文'));
      } else {
        callback()
      }
    };
    var validateFresh = (rule, value, callback) => {
      if (value === '') {
        return callback(new Error('新密码不能为空'));
      } else {
        if (/[\u4E00-\u9FA5]/g.test(value)) {
          callback(new Error('密码不能含有中文'));
        } else {
          if (this.pswInfo.check !== '') {
            this.$refs.pswInfo.validateField('check');
          }
          callback();
        }
      }
    };
    var validateCheck = (rule, value, callback) => {
      if (value === '') {
        return callback(new Error('请再次输入密码'));
      } else if (value !== this.pswInfo.fresh) {
        return callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      headers: {Authorization: window.sessionStorage.getItem('token')},
      loading: false,
      editInfo: false,
      editPsw: false,
      loginInfo: '',
      pswInfo: {username: '', old: '', fresh: '', check: ''},
      formLabelWidth: '80px',
      /* 上传参数 */
      param: {type: 'avatar'},
      /* 校验规则 */
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
      pswRules: {
        old: [
          {required: true, message: '旧密码不能为空', trigger: 'blur'},
          {validator: checkOld, trigger: 'blur'}
        ],
        fresh: [
          {required: true, validator: validateFresh, trigger: 'blur'}
        ],
        check: [
          {required: true, validator: validateCheck, trigger: 'blur'}
        ],
      }
    }
  },

  mounted() {
    this.initLoginInfo();
  },

  computed: mapState([
    'stomp'
  ]),

  methods: {
    /* 初始化登录信息 */
    initLoginInfo() {
      this.getRequest('/client/login/info').then(resp => {
        if (resp) {
          this.loginInfo = resp;
        }
      })
    },
    /* 退出登录 */
    logout() {
      this.$confirm('此操作将注销登录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.postRequest('/logout/client').then(resp => {
          if (resp.code === 200) {
            this.stomp.disconnect(() => {
              console.log("websocket已关闭连接")
            });
            window.sessionStorage.clear();
            window.localStorage.clear();
            this.$router.replace('/');
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        });
      });
    },

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
    /* 上传文件 */
    uploadSuccess(response) {
      this.loginInfo.avatar = response.obj;
    },
    /* 修改基本信息 */
    submitEditInfo(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.loading) {
            return;
          }
          this.$confirm('确定要修改信息吗？').then(_ => {
            this.loading = true;
            this.timer = setTimeout(() => {
              this.putRequest('/client/update/user', this.loginInfo).then(resp => {
                this.initLoginInfo();
              })
              // 动画关闭需要一定的时间
              setTimeout(() => {
                this.loading = false;
                this.dialog = false;
                this.editInfo = false;
                location.reload();
              }, 400);
            }, 600);
          }).catch(_ => {
          });
        } else {
          return false;
        }
      });
    },
    /* 修改密码 */
    submitEditPsw(formName) {
      this.$refs[formName].validate((valid) => {
        this.pswInfo.username = this.loginInfo.username;
        if (valid) {
          this.putRequest('/client/update/password', this.pswInfo).then(resp => {
            // 重置表单信息
            this.$refs[formName].resetFields();
            if (resp.code === 200) {
              this.postRequest('/logout/client').then(resp => {
                if (resp.code === 200) {
                  this.stomp.disconnect(() => {
                    console.log("websocket已关闭连接")
                  });
                  window.sessionStorage.clear();
                  this.$router.replace('/');
                }
              })
            } else {
              Message.error(resp.message);
            }
          })
        } else {
          return false;
        }
      })
    },
    /* 关闭修改信息抽屉 */
    cancelForm() {
      this.initLoginInfo();
      this.loading = false;
      this.editInfo = false;
      clearTimeout(this.timer);
    },
    /* 关闭确认模态框 */
    handleClose(done) {
      this.$confirm('离开数据将不会被保存，是否关闭？')
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
h5 {
  margin: 0;
  padding: 0;
}

/* 标题 */
.chat-set-header {
  height: 56px;
  border-right: 1px rgba(89, 89, 89, 0.3) solid;
  border-radius: 3px;
  margin-left: 20px;
  padding: 25px 0 0;
  text-align: left;
}

/* 功能列表 */
.chat-set-main {
  height: 478px;
  border-top: 1px rgba(89, 89, 89, 0.3) solid;
  border-right: 1px rgba(89, 89, 89, 0.3) solid;
  border-radius: 3px;
}

/* 选项按钮 */
.setItem button {
  width: 100%;
  background: none;
  border: 0;
  text-align: left;
}

.setItem button:hover {
  background-color: rgba(89, 89, 89, 0.3);
}

.activeItem {
  background-color: rgba(89, 89, 89, 0.3) !important;
}

/*滚动条整体样式*/
.scrollbar::-webkit-scrollbar {
  width: 5px;
  height: 0;
}

.scrollbar::-webkit-scrollbar-thumb {
  /*滚动条里面小方块*/
  border-radius: 3px;
  -moz-border-radius: 3px;
  -webkit-border-radius: 3px;
  background-color: #c3c3c3;
}

.scrollbar::-webkit-scrollbar-track {
  background-color: transparent;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
  border-radius: 5px;
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


</style>