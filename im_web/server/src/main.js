import Vue from 'vue'
import App from './App.vue'
import router from './router'
import VueMeta from 'vue-meta';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import axios from "axios";
import {deleteRequest, getRequest, postRequest, putRequest} from "@/api/api";

Vue.prototype.postRequest = postRequest;
Vue.prototype.getRequest = getRequest;
Vue.prototype.putRequest = putRequest;
Vue.prototype.deleteRequest = deleteRequest;
Vue.prototype.$axios = axios;
Vue.config.productionTip = false;
Vue.use(ElementUI);
Vue.use(VueMeta, {
  keyName: 'head',
});


new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
