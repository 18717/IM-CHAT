// noinspection DuplicatedCode

import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI, {Message} from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import VueMeta from 'vue-meta';
import {postRequest} from "@/api/api";
import {getRequest} from "@/api/api";
import {putRequest} from "@/api/api";
import {deleteRequest} from "@/api/api";
import store from "./vuex/store";

Vue.prototype.postRequest = postRequest;
Vue.prototype.getRequest = getRequest;
Vue.prototype.putRequest = putRequest;
Vue.prototype.deleteRequest = deleteRequest;
Vue.config.productionTip = false;

Vue.use(ElementUI);
Vue.use(VueMeta, {
    keyName: 'head',
});

router.beforeEach(((to, from, next) => {
    if (window.sessionStorage.getItem('token') != null) {
        next();
    } else {
        if (to.path === '/' || to.path === '/login' || to.path === '/reg' || to.path === '/forget') {
            next();
        } else {
            Message.error('非常抱歉，请先登录！');
            next('/?redirect=' + to.path);
        }
    }
    next();
}));

new Vue({
    router, store,
    render: h => h(App)
}).$mount('#app')
