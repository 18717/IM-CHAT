<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
export default {
  head: {
    titleTemplate(title) {
      title = typeof title === "function" ? title(this.$store) : title;
      return title ? `IM-CHAT 即时通讯系统 - ${title}` : 'IM-CHAT 即时通讯系统';
    }
  },
  name: 'App',
}
</script>

<style>
body {
  font-family: "HarmonyOS_Sans_Regular", sans-serif;
  height: 90%;
}

/*滚动条整体样式*/
.scrollbar::-webkit-scrollbar, ::-webkit-scrollbar {
  width: 5px;
  height: 0;
}

.scrollbar::-webkit-scrollbar-thumb, ::-webkit-scrollbar-thumb {
  /*滚动条里面小方块*/
  border-radius: 3px;
  -moz-border-radius: 3px;
  -webkit-border-radius: 3px;
  background-color: #c3c3c3;
}

.scrollbar::-webkit-scrollbar-track, ::-webkit-scrollbar-track {
  background-color: transparent;
}
</style>

