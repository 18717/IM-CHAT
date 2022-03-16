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
          <li v-for="server in noticeList['server']" @click="showNotice(server)">
            <el-col :span="12">{{ server.title.substr(0, 7) }}</el-col>
            <el-col :span="12" style="text-align: right; padding-right: 10px">
              <el-tag size="small">{{ server.pushTime.substr(0, 10) }}</el-tag>
            </el-col>
          </li>
        </el-collapse-item>

        <el-collapse-item>
          <template slot="title">
            <i class="el-icon-edit-outline"></i> <span>好友通知</span>
          </template>
          <li v-for="server in noticeList['server']">
            <el-col :span="12">{{ server.title.substr(0, 7) }}</el-col>
            <el-col :span="12" style="text-align: right; padding-right: 10px">
              <el-tag size="small">{{ server.pushTime.substr(0, 10) }}</el-tag>
            </el-col>
          </li>
        </el-collapse-item>
      </el-collapse>
      <el-divider><i class="el-icon-sunrise"></i></el-divider>

    </el-main>


  </div>
</template>

<script>
import {mapState} from "vuex";
import {marked} from "marked";

export default {
  name: "Notice",
  data() {
    return {
      show: false,
      dialogVisible: false,
      notice: null,
      markdown: "# test",
    }
  },

  computed: {
    ...mapState({
      noticeList: 'noticeList',
      friendList: 'friendList',
    }),
    markdownToHtml() {
      return marked(this.markdown);
    }
  },

  methods: {
    friendConfirm(notice) {
      this.dialogVisible = true;
      this.notice = notice;
    },
    handleClose(done) {
      this.$confirm('确认关闭？')
          .then(_ => {
            done();
          })
          .catch(_ => {
          });
    },
    showNotice(notice) {
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