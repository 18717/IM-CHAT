<template>
  <div>
    <textarea v-model="markdown"></textarea>
    <div v-html="markdownToHtml"></div>
  </div>
</template>

<script>
import {marked} from 'marked';
export default {
  name: "Test",
  data() {
    return {
      markdown:  "## Hello World",
    }
  },
  computed: {
    markdownToHtml(){
      console.log(this.markdown)
      return marked(this.markdown);
    }
  }
}
</script>

<style>

</style>