import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import Login from "@/views/Login";

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: '登录页',
    component: Login
  },
  {
    path: '/Home',
    name: 'Home',
    component: Home
  },
]

const router = new VueRouter({
  routes
})

export default router
