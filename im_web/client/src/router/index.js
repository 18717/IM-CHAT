import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from "@/views/Login";
import Home from "@/views/Home";
import Register from "@/views/Register";
import Forget from "@/views/Forget";
import Friend from "@/components/chat/Friend";
import Group from "@/components/chat/Group";
import Notice from "@/components/chat/Notice";
import Setting from "@/components/chat/Setting";
import Test from "@/views/Test";

Vue.use(VueRouter)

// 解决报错
const originalPush = VueRouter.prototype.push
const originalReplace = VueRouter.prototype.replace
// push
VueRouter.prototype.push = function push(location, onResolve, onReject) {
    // console.log(onReject + onResolve)
    // if (onResolve || onReject) return originalPush.call(this, location, onResolve, onReject)
    return originalPush.call(this, location).catch(err => {
        // console.log(err)
        originalPush.call(this, location, onResolve, onReject)
    });
}
// replace
VueRouter.prototype.replace = function push(location, onResolve, onReject) {
    // console.log(onReject + onResolve)
    // if (onResolve || onReject) return originalReplace.call(this, location, onResolve, onReject)
    return originalReplace.call(this, location).catch(err => {
        // console.log(err)
        originalPush.call(this, location, onResolve, onReject)
    })
}


const routes = [
    {
        path: '/reg',
        name: '客户端注册',
        component: Register
    },
    {
        path: '/',
        name: '客户端登录',
        component: Login
    },
    {
        path: '/login',
        name: '客户端登录',
        component: Login
    },
    {
        path: '/forget',
        name: '忘记密码',
        component: Forget
    },
    {
        path: '/home',
        name: '首页',
        component: Home,
        children: [
            {path: "/home", name: "好友", component: Friend},
            {path: "/friend", name: "好友", component: Friend},
            {path: "/group", name: "群组", component: Group},
            {path: "/notice", name: "通知", component: Notice},
            {path: "/setting", name: "设置", component: Setting},
        ]
    },
]

const router = new VueRouter({
    routes
})

export default router
