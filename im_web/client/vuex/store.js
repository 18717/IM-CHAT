import Vue from 'vue'
import Vuex from 'vuex'
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import {getRequest} from "@/api/api";
import {Notification} from 'element-ui';

Vue.use(Vuex)

new Date();

const store = new Vuex.Store({
    state: {
        /* 消息记录 */
        msgList: {},
        noticeList: {},
        /* 好友列表 */
        users: [],
        // 当前登录用户
        currentUser: null,
        // 当前选中的聊天用户
        currentSession: null,
        filterKey: '',
        // 与服务器的连接
        stomp: null,
        // 消息未读
        isDot: {},
    },

    mutations: {
        // 设置消息未读小红点
        changeCurrentSession(state, currentSession) {
            state.currentSession = currentSession;
            Vue.set(state.isDot, currentSession.username + '#' + state.currentUser.username, false);
        },
        // 保存消息内容到 sessionStorage
        addMessage(state, msg) {

            let key = state.currentUser.username + '#' + msg.receiveUsername;
            let msgArr = state.msgList[key];
            if (!msgArr) {
                Vue.set(state.msgList, key, []);
            }

            state.msgList[key].push({
                sendUsername: msg.sendUsername,
                receiveUsername: msg.receiveUsername,
                content: msg.content,
                messageContentType: msg.messageContentType,
                fileUrl: msg.fileUrl,
                sendTime: msg.sendTime,
                self: msg.self,
            })
        },
        // 请求好友列表
        INIT_FRIEND_LIST(state, data) {
            if (data) {
                state.users = data;
            } else {
                console.log("没有拿到好友列表数据")
            }
            window.localStorage.setItem('friend-data', JSON.stringify(data))
            console.log("初始化好友列表")
        },
        // 设置当前登录用户
        INIT_CURRENTUSER(state, data) {
            state.currentUser = data;
            window.localStorage.setItem('login-user', JSON.stringify(data));
            console.log("初始化登录信息")
        },
        // 拉取云端历史消息记录
        INIT_HISTORY_MSG(state, data) {
            if (data) {
                for (var i = 0; i < data.length; i++) {
                    let login_username = this.state.currentUser.username;
                    let msg = data[i];
                    let key;
                    if (login_username === msg.receiveUsername) {
                        key = login_username + '#' + msg.sendUsername;
                    } else {
                        key = login_username + '#' + msg.receiveUsername;
                    }

                    if (!state.msgList[key]) {
                        Vue.set(state.msgList, key, []);
                    }

                    // 添加消息
                    state.msgList[key].push({
                        sendUsername: msg.sendUsername,
                        receiveUsername: msg.receiveUsername,
                        content: msg.content,
                        messageContentType: msg.messageContentType,
                        fileUrl: msg.fileUrl,
                        sendTime: msg.sendTime,
                        self: msg.self,
                    })
                }
            }
            window.localStorage.setItem('history-message', JSON.stringify(state.msgList))
            console.log("初始化消息")
        },
    },

    actions: {
        connect(context) {
            // 连接websocket
            console.log("websocket初始化连接")
            context.state.stomp = Stomp.over(new SockJS('/ws/client'));
            let token = window.sessionStorage.getItem('token');
            context.state.stomp.connect({'Auth-Token': token}, success => {
                console.log("websocket连接成功，正在订阅消息中")
                // 消息订阅
                context.state.stomp.subscribe('/user/queue/chat', msg => {
                    let receiveMsg = JSON.parse(msg.body);
                    if (context.state.currentSession === null || context.state.currentSession.username !== receiveMsg.sendUsername) {
                        if (receiveMsg.messageContentType === 'img') {
                            Notification.info({
                                title: '【' + receiveMsg.sendNickname + '】给你发来一个图片',
                                message: receiveMsg.content.length > 10 ? receiveMsg.content.substr(0, 10) : receiveMsg.content,
                                position: 'bottom-right'
                            });
                        } else if (receiveMsg.messageContentType === 'file') {
                            Notification.info({
                                title: '【' + receiveMsg.sendNickname + '】给你发来一个文件',
                                message: receiveMsg.content.length > 10 ? receiveMsg.content.substr(0, 10) : receiveMsg.content,
                                position: 'bottom-right'
                            });
                        } else {
                            Notification.info({
                                title: '【' + receiveMsg.sendNickname + '】给你发来一条新消息',
                                message: receiveMsg.content.length > 10 ? receiveMsg.content.substr(0, 10) : receiveMsg.content,
                                position: 'bottom-right'
                            });
                        }
                        Vue.set(context.state.isDot, receiveMsg.sendUsername + '#' + receiveMsg.receiveUsername, true);
                    }
                    receiveMsg.self = 0;
                    receiveMsg.receiveUsername = receiveMsg.sendUsername;
                    context.commit('addMessage', receiveMsg);
                });
                // 通知订阅
                context.state.stomp.subscribe('/user/topic/chat', msg => {
                    let receiveMsg = JSON.parse(msg.body);
                    let key;
                    if (receiveMsg.requestType !== "server") {
                        key = "friend";
                    } else {
                        key = receiveMsg.requestType;
                    }

                    if (!context.state.noticeList[key]) {
                        Vue.set(context.state.noticeList, key, []);
                    }
                    context.state.noticeList[key].push(receiveMsg)
                    window.localStorage.setItem('notice', JSON.stringify(context.state.noticeList))
                })

                let user;
                getRequest('/client/login/info').then(loginInfo => {
                    user = loginInfo;
                    context.commit('INIT_CURRENTUSER', loginInfo)
                }).then(() => {
                    getRequest('/friend/list?username=' + context.state.currentUser.username).then(friendList => {
                        context.commit('INIT_FRIEND_LIST', friendList)
                    }).then(() => {
                        getRequest('/message/history/username?username=' + context.state.currentUser.username).then(msgList => {
                            context.commit('INIT_HISTORY_MSG', msgList)
                        })
                    })
                })
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

store.watch(function (state) {
    // 如果没有改变也返回msgList
    return state.msgList;
}, function (val) {
    // 若检测到msgList改变，则将新的msgList覆盖
    console.log("改变")
    window.sessionStorage.setItem('history-message', JSON.stringify(val));
}, {
    deep: true/*开启watch侦听*/
})

export default store;