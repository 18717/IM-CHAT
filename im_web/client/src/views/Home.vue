<template>
  <el-container id="chat">
    <el-header class="menu">
      <Card/>
    </el-header>
    <el-container>
      <el-aside class="sidebar">
        <router-view/>
      </el-aside>
      <el-main class="main">
        <Message/>
        <TextInput/>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import Card from "@/components/chat/Card";
import Message from "@/components/chat/Message";
import TextInput from "@/components/chat/TextInput";
import Friend from "@/components/chat/Friend";

export default {
  name: "Home",
  head: {
    title: "IM-CHAT",
    meta: [
      {name: 'description', content: 'IM-CHAT'}
    ]
  },
  created() {
    this.$store.dispatch("connect")
  },
  mounted() {
    this.$forceUpdate();
  },
  components: {
    Card,
    Friend,
    Message,
    TextInput
  },
}
</script>

<style>
#chat {
  margin: 4.5% auto 5.3%;
  width: 970px;
  height: 600px;
  overflow: hidden;
  border-radius: 5px;
}

#chat .sidebar, #chat .main, #chat .menu {
  height: 100%;
  padding: 0;
}

#chat .menu {
  padding: 0;
  background-color: rgba(89, 89, 89, 0.3);
  color: black;
}

#chat .sidebar {
  background: rgba(255, 255, 255, 0.5);
}

#chat .main {
  position: relative;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.5);
}
</style>