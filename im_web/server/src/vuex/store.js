import Vue from 'vue'
import Vuex from 'vuex'
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

Vue.use(Vuex)

new Date();

const store = new Vuex.Store({
    state: {
        // 与服务器的连接
        stomp: null,
    },

    mutations: {},

    actions: {
        connect(context) {
            // 连接websocket
            console.log("websocket初始化连接")
            context.state.stomp = Stomp.over(new SockJS('/ws/server'));
            let token = window.sessionStorage.getItem('token');
            context.state.stomp.connect({'Auth-Token': token}, success => {
                console.log("WebSocket连接成功")
            }, error => {
                console.log("WebSocket连接失败")
            })
        },
        disconnect(context) {
            context.stomp.disconnect(() => {
                console.log("websocket已关闭连接")
            })
        },
    }
})

export default store;