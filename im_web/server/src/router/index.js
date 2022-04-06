import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import Login from "@/views/Login";
import Space from "@/views/Space";
import ListUser from "@/views/ListUser";
import InsetUser from "@/views/InsetUser";
import Notice from "@/views/Notice";

Vue.use(VueRouter)
// 解决报错
const originalPush = VueRouter.prototype.push
const originalReplace = VueRouter.prototype.replace
// push
VueRouter.prototype.push = function push (location, onResolve, onReject) {
  // console.log(onReject + onResolve)
  // if (onResolve || onReject) return originalPush.call(this, location, onResolve, onReject)
  return originalPush.call(this, location).catch(err => {
    // console.log(err)
    originalPush.call(this, location, onResolve, onReject)
  });
}
// replace
VueRouter.prototype.replace = function push (location, onResolve, onReject) {
  // console.log(onReject + onResolve)
  // if (onResolve || onReject) return originalReplace.call(this, location, onResolve, onReject)
  return originalReplace.call(this, location).catch(err => {
    // console.log(err)
    originalPush.call(this, location, onResolve, onReject)
  })
}

const routes = [
  {
    path: '/',
    name: '登录',
    component: Login
  },
  {
    path: '/Home',
    name: '首页',
    component: Home,
    children: [
      {path: "/home", name: "首页", component: Space},
      {path: "/space", name: "个人中心", component: Space},
      {path: "/notice", name: "系统通知", component: Notice},
      {path: "/list", name: "用户列表", component: ListUser},
      {path: "/new-user", name: "添加用户", component: InsetUser},
    ]
  },
]

const router = new VueRouter({
  routes
})

export default router
