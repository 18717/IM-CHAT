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
      return title ? 'IM-CHAT 即时通讯系统' : `IM-CHAT 即时通讯系统 - ${title}`;
    }
  },
  name: 'App',
}
</script>

<style>
body {
  font-family: "HarmonyOS_Sans_Regular",sans-serif;
}
</style>

