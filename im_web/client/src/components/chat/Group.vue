<template>
  <div id="group">
    <!-- 搜索框 -->
    <el-header class="chat-friendsList-header">
      <el-input placeholder="搜索" clearable prefix-icon="el-icon-search" size="mini"
                style="width: 170px; padding-right: 5px">
      </el-input>
      <el-button size="mini" @click="dialogVisible = true">添加/创建</el-button>
    </el-header>

    <!-- 群列表 -->
    <el-main class="group-list">

      <div v-for="group in groupList"
           @click="checkedGroup(group)">
        <el-button class="group-item" :class="{ activeItem: currentGroup ? group.gid === currentGroup.gid : false }">
          <div style="width: 10%; display: inline-block; text-align: left"><i class="el-icon-cloudy"></i></div>
          <div style="width: 90%; display: inline-block; text-align: left">
            <el-badge :is-dot="isDot[group.gid]">
              <p>{{ group.groupName }}</p>
            </el-badge>
          </div>
        </el-button>
      </div>

    </el-main>

    <!-- 搜群/建群 -->
    <el-dialog
        :before-close="handleClose"
        style="background: rgba(58,58,58,1)"
        :visible.sync="dialogVisible"
        width="80%">
      <el-row slot="title">
        <el-col :span="3">
          <el-button @click="switchFlag = true" type="primary" size="medium" plain
                     style="border: 0">
            <i class="el-icon-search"></i> 搜索群聊
          </el-button>
        </el-col>
        <el-col :span="3">
          <el-button @click="switchFlag=false" type="primary" size="medium" plain
                     style="border: 0">
            <i class="el-icon-circle-plus-outline"></i> 创建群聊
          </el-button>
        </el-col>
      </el-row>
      <!-- 搜群 -->
      <el-card v-if="switchFlag">
        <el-row>
          <el-col :span="3" style="line-height: 40px;"><h3><i class="el-icon-search"></i> 搜索群聊</h3></el-col>
          <el-col :span="18">
            <el-input v-model="query.content"
                      @keydown.enter.native="doSearch('search')"
                      @clear="doSearch('clear')"
                      clearable
                      placeholder="请输入关键词">
              <el-select v-model="query.condition" slot="prepend" placeholder="请选择" style="width: 90px">
                <el-option label="GID" value="gid"></el-option>
                <el-option label="群名" value="groupName"></el-option>
                <el-option label="群主名" value="masterUsername"></el-option>
              </el-select>
              <el-button slot="append" icon="el-icon-search" @click="doSearch('search')">搜索</el-button>
            </el-input>
          </el-col>
        </el-row>
        <div v-if="groupData != null" class="group-container">
          <el-card
              v-for="(group, index) in groupData" :key="index"
              class="group-card"
              shadow="hover">
            <el-row slot="header" class="clearfix">
              <el-col :span="18" style="line-height: 40px">
                <b><i class="el-icon-cloudy"></i>&emsp;{{ group.groupName }}</b>
              </el-col>
              <el-col :span="6" style="text-align: right">
                <el-button type="text" @click="sendJoinGroupRequest(group)">申请加群</el-button>
              </el-col>
            </el-row>

            <el-row style="max-height: 80px; height: auto !important; padding-bottom: 16px">
              <el-col :span="6">
                <el-tag size="small">群描述:</el-tag>
              </el-col>
              <el-col :span="18" class="group-desc">{{ group.groupDescribe }}</el-col>
            </el-row>
            <el-row>
              <el-tag size="small">GID：{{ group.gid }}</el-tag>
            </el-row>
            <el-row>
              <el-tag size="small">群主：{{ group.masterUsername }}</el-tag>
            </el-row>
            <el-row>
              <el-col :span="8">
                <el-tag size="small">成员：{{ group.memberNum }}/10</el-tag>
              </el-col>
              <el-col :span="16">
                <el-tag size="small">创建时间：{{ group.createTime }}</el-tag>
              </el-col>
            </el-row>

          </el-card>
        </div>
        <el-empty style="height: 386px" v-else image="http://cdn.lrxya.icu/im_file/nodatatip.png"></el-empty>
        <!--  分页  -->
        <el-pagination
            @current-change="currentChange"
            background
            :page-size="pageSize"
            layout="prev, pager, next, jumper, ->, total"
            :total="total">
        </el-pagination>
      </el-card>
      <!-- 建群 -->
      <div v-else>
        <el-card v-if="isFound == null" style="height: 498px">
          <el-row style="height: auto !important;padding-top: 7%">
            <el-col :span="11" :offset="1">
              <h4>建群注意事项</h4>
              <p>1.遵纪守法，好好学习</p>
              <p>2.早睡早起，好好吃饭</p>
              <p>3.认真工作，好好花钱</p>
              <p>本项目地址：<a href="https://github.com/LL-102127/IM-CHAT.git" target="_blank">https://github.com/LL-102127/IM-CHAT.git</a>
              </p>
              <p></p>
            </el-col>
            <el-col :span="12">
              <el-form
                  style="width: 500px"
                  :model="groupInfo"
                  :rules="rules"
                  ref="regInfo"
                  label-width="80px"
                  status-icon>
                <el-form-item label="群名称" prop="groupName">
                  <el-tooltip class="item" effect="dark" content="群名称" placement="right">
                    <el-input v-model="groupInfo.groupName" placeholder="请输入群名称" autocomplete="off" maxlength="9"
                              show-word-limit></el-input>
                  </el-tooltip>
                </el-form-item>
                <el-form-item label="群描述" prop="groupDescribe">
                  <el-tooltip class="item" effect="dark" content="描述群的用途" placement="right">
                    <el-input
                        v-model="groupInfo.groupDescribe"
                        placeholder="(可忽略)"
                        autocomplete="off"
                        maxlength="30" show-word-limit></el-input>
                  </el-tooltip>
                </el-form-item>
                <el-form-item label="验证码" prop="code">
                  <el-tooltip class="item" effect="dark" content="验证码" placement="right">
                    <el-row type="flex" justify="space-between">
                      <el-col :span="12">
                        <el-input type="text" auto-complete="false" v-model="groupInfo.code"
                                  placeholder="输入验证码"></el-input>
                      </el-col>
                      <el-col :span="11">
                        <el-button v-if="captchaUrl == null" @click="updateCaptcha" style="width: 100%">获取验证码
                        </el-button>
                        <img v-else :src="captchaUrl" @click="updateCaptcha" alt="验证码"
                             style="margin-left: 11px;border-radius: 5px">
                      </el-col>
                    </el-row>
                  </el-tooltip>
                </el-form-item>
                <el-form-item>
                  <el-row type="flex" justify="center" :gutter="20">
                    <el-col :span="8">
                      <el-button type="info" @click="dialogVisible = false;" plain style="border: 0; width: 100%">取消
                      </el-button>
                    </el-col>
                    <el-col :span="8">
                      <el-button type="warning" @click="resetForm('groupInfo')" plain style="border: 0; width: 100%">重置
                      </el-button>
                    </el-col>
                    <el-col :span="8">
                      <el-button type="primary" @click="toFound('groupInfo')" plain style="border: 0; width: 100%">创建
                      </el-button>
                    </el-col>
                  </el-row>
                </el-form-item>
              </el-form>
            </el-col>
          </el-row>
        </el-card>
        <div v-else>
          <el-card v-if="isFound">
            <el-result icon="success" title="创建成功！">
              <template slot="extra">
                <el-descriptions class="info" :column="1">
                  <el-descriptions-item label="本群群号（GID）">{{ foundGroupRequest.gid }}</el-descriptions-item>
                  <el-descriptions-item label="已创建的群聊个数">{{ foundGroupRequest.foundNum }}</el-descriptions-item>
                  <el-descriptions-item label="已加入的群聊个数">{{ foundGroupRequest.joinNum }}</el-descriptions-item>
                  <el-descriptions-item label="还可创建群聊个数">{{
                      foundGroupRequest.remainderFoundNum
                    }}
                  </el-descriptions-item>
                  <el-descriptions-item label="还可加入群聊个数">{{ foundGroupRequest.remainderJoinNum }}</el-descriptions-item>
                </el-descriptions>
              </template>
            </el-result>
          </el-card>
          <el-card v-else>
            <el-result icon="error" title="创建失败！">
              <template slot="extra">
                <el-descriptions class="info" :column="1">
                  <el-descriptions-item label="已创建的群聊个数">{{ foundGroupRequest.foundNum }}</el-descriptions-item>
                  <el-descriptions-item label="已加入的群聊个数">{{ foundGroupRequest.joinNum }}</el-descriptions-item>
                  <el-descriptions-item label="还可创建群聊个数">{{
                      foundGroupRequest.remainderFoundNum
                    }}
                  </el-descriptions-item>
                  <el-descriptions-item label="还可加入群聊个数">{{ foundGroupRequest.remainderJoinNum }}</el-descriptions-item>
                </el-descriptions>
              </template>
            </el-result>
          </el-card>
        </div>
      </div>

    </el-dialog>
  </div>
</template>

<script>
import {Message} from "element-ui";
import {mapState} from "vuex";

export default {
  name: "Group",
  data() {
    return {
      dialogVisible: false,
      // 群查询参数
      query: {condition: 'gid', content: ' '},
      // 分页参数
      total: 0,
      currentPage: 1,
      pageSize: 3,
      // 搜群和建群切换
      switchFlag: true,
      // 群信息
      groupInfo: {},
      captchaUrl: null,
      // 查询结果
      groupData: null,
      // 创建群后的响应信息
      foundGroupRequest: {},
      isFound: null,

      rules: {
        groupName: [
          {required: true, message: '群名不能为空', trigger: 'blur'},
          {min: 1, max: 9, message: '长度不能大于9个字符', trigger: 'blur'},
        ],
        groupDescribe: [
          {min: 1, max: 30, message: '长度不能大于30个字符', trigger: 'blur'},
        ],
        code: [
          {required: true, message: '验证码不能为空', trigger: 'blur'},
        ],
      }
    }
  },
  computed: mapState([
    'currentLogin',
    'currentGroup',
    'groupList',
    'isDot',
  ]),
  methods: {

    // 当前选中的群聊
    checkedGroup(group) {
      this.$store.commit('changeCurrentGroup', group);
    },

    // 搜索
    doSearch(params) {
      if (params === 'search') {
        if (this.query.content == null) {
          Message.error("请输入搜索关键字")
        } else {
          this.getRequest('/group/search?currentPage=' + this.currentPage + '&size=' + this.pageSize + '&' + this.query.condition + '=' + this.query.content).then(resp => {
            this.groupData = resp.data;
            this.total = resp.total;
          })
        }
      } else if (params === 'clear') {
        this.data = null;
      }


    },
    handleClose(done) {
      done();
      this.data = null;
      this.query.content = null;
      this.isFound = null;
      this.groupInfo = {};
      this.foundGroupRequest = {};
    },
    // 申请加群
    sendJoinGroupRequest(group) {
      let groupParams = {};
      this.$prompt('入群验证信息', '申请入群', {
        confirmButtonText: '发送请求',
        cancelButtonText: '取消',
      })
          .then(({value}) => {
            // 发送入群申请
            groupParams.avatarUrl = this.currentLogin.avatar;
            groupParams.senderNickname = this.currentLogin.nickname;
            groupParams.senderUsername = this.currentLogin.username;
            groupParams.gender = this.currentLogin.gender;
            groupParams.receiverUsername = group.masterUsername;
            groupParams.groupName = group.groupName;
            groupParams.gid = group.gid;
            groupParams.title = "入群申请";
            groupParams.content = value;
            groupParams.sendTime = new Date().format("yyyy-MM-dd hh:mm:ss");
            groupParams.businessType = 'join';
            console.log(groupParams)
            this.$store.state.stomp.send('/ws/group/send', {}, JSON.stringify(groupParams));
            this.$message({
              type: 'success',
              message: '已向群聊【' + group.groupName + '】发送加入申请！'
            });
          })
          .catch(() => {
            this.$message({
              type: 'info',
              message: '取消操作'
            });
          });
    },
    // 分页
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.doSearch('search')
    },
    resetForm() {
      this.captchaUrl = null;
      this.groupInfo = {};
    },
    toFound() {
      this.groupInfo.masterUsername = this.currentLogin.username;
      this.postRequest('/group/found', this.groupInfo).then(resp => {
        if (resp) {
          this.foundGroupRequest = resp.obj;
          console.log(this.foundGroupRequest)
          if (this.foundGroupRequest.gid) {
            this.isFound = true;
          } else {
            this.isFound = false;
            Message.error("创建失败！")
          }
        } else {
          this.groupInfo.code = '';
        }
      })
    },
    updateCaptcha() {
      this.captchaUrl = '/captcha/group/found?username=' + this.currentLogin.username + '?time=' + new Date();
    },
  }
}
</script>

<style>

h3 {
  margin: 0;
  padding: 0;
}

a {
  text-decoration: none;
  color: #409EFF;
}

.group-container {
  display: flex;
  justify-content: space-around;
  margin-top: 50px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

#group .el-dialog__header {
  padding-bottom: 0;
}

#group .el-dialog__body {
  padding-top: 0;
  padding-bottom: 15px;
}

.group-container .el-card__header {
  padding-top: 10px;
  padding-bottom: 10px;
}

.group-container .dialog__body {
  padding-top: 0;
}

.group-card {
  border-color: #409EFF !important;
  width: 330px;
  margin-bottom: 25px;
}

.group-container .group-desc {
  background-color: #ecf5ff;
  display: inline-block;
  height: auto;
  padding: 0 10px;
  line-height: 20px;
  font-size: 12px;
  color: #409EFF;
  border: 1px solid #d9ecff;
  border-radius: 4px;
  /*box-sizing: border-box;*/
  /*white-space: nowrap;*/
}

.info {
  width: 30%;
  margin: 0 auto;
}

.info .el-descriptions-item {
  text-align: center !important;
  display: inline-block;
}

/* 搜索 */
.chat-friendsList-header {
  height: 60px;
  line-height: 60px;
  border-right: 1px rgba(89, 89, 89, 0.3) solid;
  padding-right: 5px;
  border-radius: 3px;
}

/* 群列表 */
.group-list {
  width: 100%;
  height: 478px;
  border-top: 1px rgba(89, 89, 89, 0.3) solid;
  border-right: 1px rgba(89, 89, 89, 0.3) solid;
  border-radius: 3px;
}

.group-item {
  width: 100%;
  padding: 0 20px 0 20px !important;
  background: none !important;
  border: 0 !important;
}

.group-item:hover {
  background-color: rgba(89, 89, 89, 0.3) !important;
}

.el-collapse {
  border: 0 !important;
}

.el-collapse-item__wrap {
  border: 0 !important;
  background: none !important;
}

.el-collapse-item__header {
  background: none !important;
  border: 0 !important;
}

.el-collapse-item__content {
  padding: 0 !important;
}

.el-collapse-item__content li {
  list-style: none;
  height: 36px;
  line-height: 36px;
  margin-bottom: 5px;
  margin-top: 5px;
  padding-left: 22px;
  border-radius: 3px;
}

.activeItem {
  background-color: rgba(89, 89, 89, 0.3) !important;
}
</style>