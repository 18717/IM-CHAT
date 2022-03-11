import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from "@/views/Login";
import Home from "@/views/Home";

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
    name: '客户端登录',
    component: Login
  },
  {
    path: '/home',
    name: '首页',
    component: Home
  },
]

const router = new VueRouter({
  routes
})

export default router
