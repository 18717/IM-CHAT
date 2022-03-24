<template>
  <div id="message">
    <div v-if="chatType === 'private'" class="chat-user">
      <div v-if="currentUser" class="message-header">
        <div class="username-div">
          <el-header><b>{{ currentUser.nickname }}</b> ( {{ currentUser.username }} )</el-header>
        </div>
        <div class="btn-div">
          <div class="close-btn">
            <el-button @click="closeCurrentUser" icon="el-icon-close" size="mini"></el-button>
          </div>
          <div class="show-btn">
            <el-button type="text" @click="showUserInfo(currentUser)" icon="el-icon-more" size="mini"></el-button>
          </div>
        </div>

      </div>
      <div class="message-info scrollbar" v-scroll-bottom="msgList">
        <ul v-if="currentUser">
          <li v-for="msg in msgList[currentLogin.username + '#' + currentUser.username]">
            <p class="time" style="text-align: center;margin: 15px 0">
              <span style="background-color: #dcdcdc;">{{ msg.sendTime }}</span>
            </p>

            <div v-if="msg.self !== 1" class="main no-self">
              <div style="height: 50%">
                <img class="avatar" :src="msg.avatar" alt="">
              </div>
              <div style="height: 50%">
                <img v-if="msg.messageContentType === 'img'" :src="msg.fileUrl" alt=""
                     style="width: 100px; height: 100px; border-radius: 3px">
                <div v-else class="text">
                  <span>{{ msg.content }}</span>
                  <a v-if="msg.messageContentType === 'file'" :href="msg.fileUrl">
                    <el-button size="mini" style="background: none; border: 0"><i class="el-icon-folder"></i>【点击下载】
                    </el-button>
                  </a>
                </div>
              </div>
            </div>
            <div v-else class="main self">
              <div style="height: 50%">
                <img class="avatar" :src="currentLogin.avatar" alt="">
              </div>
              <div style="height: 50%">
                <img v-if="msg.messageContentType === 'img'" :src="msg.fileUrl" alt=""
                     style="width: 100px; height: 100px; border-radius: 3px">
                <div v-else class="text">
                  <div>{{ msg.content }}</div>
                  <a v-if="msg.messageContentType === 'file'" :href="msg.fileUrl">
                    <el-button size="mini" style="background: none; border: 0"><i class="el-icon-folder"></i>【点击下载】
                    </el-button>
                  </a>
                </div>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>

    <div v-if="chatType === 'public'" class="chat-group">
      <div v-if="currentGroup" class="message-header">
        <div class="username-div">
          <el-header><b>{{ currentGroup.groupName }}</b>( {{ currentGroup.memberNum }} )</el-header>
        </div>
        <div class="btn-div">
          <div class="close-btn">
            <el-button @click="closeCurrentGroup" icon="el-icon-close" size="mini"></el-button>
          </div>
          <div class="show-btn">
            <el-button type="text" @click="showGroupInfo(currentGroup)" icon="el-icon-more" size="mini"></el-button>
          </div>
        </div>
      </div>
      <div class="message-info scrollbar" v-scroll-bottom="groupMsgList">
        <ul v-if="currentGroup">
          <li v-for="msg in groupMsgList[currentGroup.gid]">
            <div v-if="msg.self !== 1" class="main no-self">
              <div style="height: 50%">
                <img class="avatar" :src="msg.avatar" alt="">
                <p v-if="currentGroup.masterUsername !== msg.sendUsername" class="time">
                  <span>{{ msg.sendNickname }}</span> <span>{{ msg.sendTime }}</span>
                </p>
                <p v-else class="time">
                  <span class="master-tag">群主</span><span>{{ msg.sendNickname }}</span> <span>{{ msg.sendTime }}</span>
                </p>
              </div>
              <div style="height: 50%">
                <img v-if="msg.messageContentType === 'img'" :src="msg.fileUrl" alt=""
                     style="width: 100px; height: 100px; border-radius: 3px">
                <div v-else class="text">
                  <span>{{ msg.content }}</span>
                  <a v-if="msg.messageContentType === 'file'" :href="msg.fileUrl">
                    <el-button size="mini" style="background: none; border: 0"><i class="el-icon-folder"></i>【点击下载】
                    </el-button>
                  </a>
                </div>
              </div>
            </div>
            <div v-else class="main self">
              <div style="height: 50%">
                <img class="avatar" :src="msg.avatar" alt="">
                <p v-if="currentGroup.masterUsername !== msg.sendUsername" class="time">
                  <span>{{ msg.sendTime }}</span><span>{{ msg.sendNickname }}</span>
                </p>
                <p v-else class="time">
                  <span>{{ msg.sendTime }}</span><span>{{ msg.sendNickname }}</span> <span class="master-tag">群主</span>
                </p>
              </div>
              <div style="height: 50%">
                <img v-if="msg.messageContentType === 'img'" :src="msg.fileUrl" alt=""
                     style="width: 100px; height: 100px; border-radius: 3px">
                <div v-else class="text">
                  <span>{{ msg.content }}</span>
                  <a v-if="msg.messageContentType === 'file'" :href="msg.fileUrl">
                    <el-button size="mini" style="background: none; border: 0"><i class="el-icon-folder"></i>【点击下载】
                    </el-button>
                  </a>
                </div>
              </div>
            </div>
          </li>
        </ul>
      </div>
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
    'currentLogin',
    'msgList',
    'groupMsgList',
    'currentUser',
    'currentGroup',
    'chatType',
  ]),
  mounted() {
    // this.initUser();
    // this.refreshHistoryMsg();
  },
  methods: {

    initUser() {
      this.getRequest('/client/login/info').then(resp => {
        this.loginInfo = resp;
      })
    },
    closeCurrentUser() {
      this.$store.commit('changeCurrentUser', null)
    },
    showUserInfo() {
      this.$store.commit('showInfo', true)
    },
    closeCurrentGroup() {
      this.$store.commit('changeCurrentGroup', null)
    },
    showGroupInfo() {
      this.$store.commit('showInfo', true)
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

.message-header .username-div {
  width: 50%;
}

/* 按钮 */
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

.close-btn button:hover {
  background-color: #ec7874;
  color: #FFFFFF;
}

.show-btn {
  padding-right: 15px;
}

.show-btn button:hover {
  background: none;
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
  /*text-align: right;*/
  margin: 7px 0;
}

.message-info .time .master-tag {
  background-color: #fff;
  height: auto;
  padding: 0 5px;
  font-size: 12px;
  color: #409EFF;
  border: 1px solid #d9ecff;
  border-radius: 4px;
}

.message-info .time span {
  display: inline-block;
  padding: 0 5px;
  font-size: 13px;
  color: #8c939d;
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

.message-info .no-self {
  text-align: left;
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