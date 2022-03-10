import Vue from 'vue'
import App from './App.vue'
import router from './router'
import VueMeta from 'vue-meta';
import ElementUI, {Message} from 'element-ui';
import axios from "axios";

Vue.use(ElementUI);
Vue.use(VueMeta, {
  keyName: 'head',
});

Vue.prototype.$axios = axios;

Vue.config.productionTip = false

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
