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
      return title;
    }
  },
  name: 'App',
}
</script>

<style>
body {
  font-family: "HarmonyOS_Sans_Regular", sans-serif;
}
</style>
