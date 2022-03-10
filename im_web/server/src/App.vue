<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
export default {
  head: {
    titleTemplate(title) {
      return title;
    }
  },
  name: 'App',
}
</script>

<style>

</style>

