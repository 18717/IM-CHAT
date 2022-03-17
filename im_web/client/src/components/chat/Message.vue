<template>
  <div id="message">
    <div v-if="currentSession" class="message-header">
      <div class="username-div">
        <el-header><b>{{ currentSession.nickname }}</b></el-header>
      </div>
      <div class="btn-div">
        <el-button @click="changeCurrentSession" icon="el-icon-close" size="mini"></el-button>
      </div>
    </div>
    <div class="message-info scrollbar" v-scroll-bottom="msgList">
      <ul v-if="currentSession">
        <li v-for="msg in msgList[currentUser.username + '#' + currentSession.username]">
          <p class="time">
            <span>{{ msg.sendTime }}</span>
          </p>

          <div class="main" :class="{self:msg.self === '1'}">
            <img class="avatar" :src="msg.self === '1' ? currentUser.avatar : currentSession.avatar" alt="">
            <img v-if="msg.messageContentType === 'img'" :src="msg.fileUrl" alt=""
                 style="width: 100px; height: 100px; border-radius: 3px">
            <p v-else class="text">
              <span>{{ msg.content }}</span>
              <a v-if="msg.messageContentType === 'file'" :href="msg.fileUrl">
                <el-button size="mini" style="background: none; border: 0"><i class="el-icon-folder"></i>【点击下载】
                </el-button>
              </a>
            </p>
          </div>

        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import {mapState} from "vuex";

export default {
  name: "Message",
  data() {
    return {
      loginInfo: '',
    }
  },
  computed: mapState([
    'currentUser',
    'msgList',
    'currentSession',
  ]),
  mounted() {
    // this.initUser();
    // this.refreshHistoryMsg();
  },
  methods: {

    initUser() {
      this.getRequest('/client/login/info').then(resp => {
        this.loginInfo = resp;
        console.log("message")
      })
    },

    changeCurrentSession() {
      this.$store.commit('changeCurrentSession', null)
    },
  },
  filters: {
    time(date) {
      if (date) {
        date = new Date(date);
      }
      return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDay()} ${date.getHours()}:${date.getMinutes()}:${date.getSeconds()}`;
    }
  },
  directives: {/*这个是vue的自定义指令,官方文档有详细说明*/
    // 发送消息后滚动到底部,这里无法使用原作者的方法，也未找到合理的方法解决，暂用setTimeout的方法模拟
    'scroll-bottom'(el) {
      //console.log(el.scrollTop);
      setTimeout(function () {
        el.scrollTop += 9999;
      }, 1)
    }
  }
}
</script>

<style>
/* 消息头部区域 */
.message-header {
  line-height: 60px;
  border-bottom: 1px rgba(93, 93, 93, 0.1) solid;
  display: flex;
}

.message-header > .username-div {
  width: 50%;
}

/* 关闭按钮 */
.message-header .btn-div {
  width: 50%;
  text-align: right;
  line-height: 0;
}

.message-header button {
  background-color: unset;
  border: 0;
  color: grey;
  text-align: right;
}

.message-header button:hover {
  background-color: #ec7874;
  color: #FFFFFF;
}

/* 消息列表区域 */
.message-info {
  padding: 10px;
  height: 295px;
  overflow-y: scroll;
}

.message-info ul {
  list-style-type: none;
  padding: 0;
}

.message-info ul li {
  margin-bottom: 15px;
}

.message-info .time {
  text-align: center;
  margin: 7px 0;
}

.message-info .time span {
  display: inline-block;
  padding: 0 18px;
  font-size: 12px;
  color: #FFF;
  background-color: #dcdcdc;
  border-radius: 2px;
}

.message-info .main {
  background: none !important;
}

.message-info .main .avatar {
  float: left;
  margin: 0 10px 0 0;
  border-radius: 3px;
  width: 30px;
  height: 30px;
}

.message-info .main .text {
  display: inline-block;
  padding: 0 10px;
  max-width: 80%;
  border-radius: 4px;
  line-height: 30px;
  background-color: #fff;
}

.message-info .self {
  text-align: right;
}

.message-info .self .avatar {
  float: right;
  margin: 0 0 0 10px;
  border-radius: 3px;
  width: 30px;
  height: 30px;
}

.message-info .self .text {
  display: inline-block;
  padding: 0 10px;
  max-width: 80%;
  background-color: #b2e281;
  border-radius: 4px;
  line-height: 30px;
}
</style>