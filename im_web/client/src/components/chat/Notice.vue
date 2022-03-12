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
            <i class="el-icon-edit-outline"></i> <span>服务器公告</span>
          </template>
          <li>与现实生活一致：习惯的语言和概念；</li>
          <li>在界面中计样式、图标和文本、。</li>
        </el-collapse-item>

        <el-collapse-item>
          <template slot="title">
            <i class="el-icon-edit-outline"></i><span>通知</span>
          </template>
          <!--<li v-if="notices" v-for="notice in notices['friend']" @click="friendConfirm(notice)">-->
          <!--  {{ notice.content.length > 18 ? notice.content.substr(0, 18) : notice.content }}-->
          <!--</li>-->
          <li>与现实生活一致：习惯的语言和概念；</li>
          <li>在界面中计样式、图标和文本、。</li>
        </el-collapse-item>
      </el-collapse>


    </el-main>
    <!--

      <el-dialog
          title="好友验证"
          :visible.sync="dialogVisible"
          width="30%"
          :before-close="handleClose">
        <span>{{ this.notice.content }}</span>
        <span slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
    </span>
      </el-dialog>
    -->


  </div>
</template>

<script>
import {mapState} from "vuex";

export default {
  name: "Notice",
  data() {
    return {
      dialogVisible: false,
      notice: null,
      notices: null,
    }
  },

  watch: {
    noticeList: function () {
      if (!this.noticeList.isEmpty()) {
        this.notices = this.noticeList;
      }
      console.log("notice")
    }
  },

  computed: mapState([
    'noticeList',
  ]),

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
    }
  }
}
</script>

<style>
h5 {
  padding: 0;
  margin: 0;
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
</style>