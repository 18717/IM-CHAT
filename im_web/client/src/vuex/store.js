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
        // 消息列表
        msgList: {},
        // 通知列表
        noticeList: {},
        // 好友列表
        friendList: {},
        // 群聊列表
        groupList: [],
        // 当前登录用户
        currentLogin: null,
        // 是否显示当前选中聊天的对象信息
        showInfo: false,
        // 聊天类型
        chatType: null,
        // 当前选中的聊天用户
        currentUser: null,
        // 当前选中的群聊
        currentGroup: null,
        // 服务器连接
        stomp: null,
        // 消息未读标识
        isDot: {},
    },

    mutations: {
        // 设置消息未读小红点
        changeCurrentUser(state, currentUser) {
            if (currentUser != null) {
                state.chatType = 'private';
            } else {
                state.chatType = null;
            }
            state.currentUser = currentUser;
            Vue.set(state.isDot, currentUser.username + '#' + state.currentLogin.username, false);
        },
        changeCurrentGroup(state, currentGroup) {
            if (currentGroup != null) {
                state.chatType = 'public';
            } else {
                state.chatType = null;
            }
            state.currentGroup = currentGroup;
            Vue.set(state.isDot, currentGroup.gid, false);
        },
        closeInfo(state) {
            state.showInfo = false;
        },
        showInfo(state) {
            state.showInfo = !state.showInfo;
        },
        addMessage(state, msg) {
            let key = state.currentLogin.username + '#' + msg.receiveUsername;
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
        addNoticeServer(state, notice) {
            let key = 'server';
            if (!state.noticeList[key]) {
                Vue.set(state.noticeList, key, []);
            }
            state.noticeList[key].push(JSON.parse(notice));
        },
        addNoticeFriend(state, notice) {
            let obj = JSON.parse(notice);
            if (obj.verified === 1 && obj.confirm === 1) {
                if (obj.del === 1) {
                    state.currentUser = null;
                    state.showInfo = false;
                }
                getRequest('/friend/list?username=' + state.currentLogin.username).then(friendList => {
                    if (friendList) {
                        state.friendList = friendList;
                    }
                    window.sessionStorage.setItem('friend-data', JSON.stringify(friendList))
                })
            }
            let key = 'friend';
            if (!state.noticeList[key]) {
                Vue.set(state.noticeList, key, []);
            }
            state.noticeList[key].push(obj);
        },
        addNoticeGroup(state, notice) {
            let obj = JSON.parse(notice);
            if (obj.verified === 1 && obj.confirm === 1) {
                getRequest('/group/list?username=' + state.currentLogin.username).then(groupList => {
                    if (groupList) {
                        state.groupList = groupList;
                    }
                    window.sessionStorage.setItem('group-data', JSON.stringify(groupList))
                })
            }
            let key = 'group';
            if (!state.noticeList[key]) {
                Vue.set(state.noticeList, key, []);
            }
            state.noticeList[key].push(obj);
        },
        // 解除好友关系时执行
        delFriend(state) {
            state.showInfo = false;
            state.currentUser = null;
            getRequest('/message/history/username?username=' + state.currentLogin.username).then(msgList => {
                state.commit('INIT_HISTORY_MSG', msgList)
            })
        },
        // 初始化好友列表
        INIT_FRIEND_LIST(state, data) {
            if (data) {
                state.friendList = data;
            } else {
                console.log("没有拿到好友列表数据")
            }
            window.sessionStorage.setItem('friend-data', JSON.stringify(data))
            console.log("初始化好友列表完成")
        },
        // 初始化群聊列表
        INIT_GROUP_LIST(state, data) {
            if (data) {
                state.groupList = data;
            }
            window.sessionStorage.setItem('group-data', JSON.stringify(data))
            console.log("初始化群聊列表完成")
        },
        // 设置当前登录用户
        INIT_CURRENTUSER(state, data) {
            state.currentLogin = data;
            window.sessionStorage.setItem('login-user', JSON.stringify(data));
            console.log("初始化登录信息完成")
        },
        // 拉取云端历史消息记录
        INIT_HISTORY_MSG(state, data) {
            if (data) {
                for (var i = 0; i < data.length; i++) {
                    let loginUsername = this.state.currentLogin.username;
                    let msg = data[i];
                    let key;
                    if (loginUsername === msg.receiveUsername) {
                        key = loginUsername + '#' + msg.sendUsername;
                    } else {
                        key = loginUsername + '#' + msg.receiveUsername;
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
            console.log("初始化历史消息记录完成")
        },
        INIT_NOTICE_SERVER(state, data) {
            if (data) {
                let key = 'server';
                if (!state.noticeList[key]) {
                    Vue.set(state.noticeList, key, []);
                }
                for (let i = 0; i < data.length; i++) {
                    let notice = data[i];
                    state.noticeList[key].push({
                        title: notice.title,
                        fileName: notice.fileName,
                        fileUrl: notice.fileUrl,
                        content: notice.content,
                        pushTime: notice.pushTime,
                        pushNum: notice.pushNum,
                    })
                }
            }
            console.log("初始化系统通知完成")
        },
        INIT_NOTICE_FRIEND(state, data) {
            if (data) {
                let key = 'friend';
                if (!state.noticeList[key]) {
                    Vue.set(state.noticeList, key, []);
                }
                for (let i = 0; i < data.length; i++) {
                    let notice = data[i];
                    state.noticeList[key].push({
                        title: notice.title,
                        content: notice.content,
                        sendTime: notice.sendTime,
                        add: notice.add,
                        del: notice.del,
                        confirm: notice.confirm,
                        verified: notice.verified,
                        sendUsername: notice.sendUsername,
                        receiveUsername: notice.receiveUsername,
                        sendNickname: notice.sendNickname,
                        avatarUrl: notice.avatarUrl,
                        flag: notice.flag,
                        flagTime: notice.sendTime,
                    })
                }
            }
            console.log("初始化好友通知完成")
        },
        INIT_NOTICE_GROUP(state, data) {
            if (data) {
                let key = 'group';
                if (!state.noticeList[key]) {
                    Vue.set(state.noticeList, key, []);
                }
                for (let i = 0; i < data.length; i++) {
                    let notice = data[i];
                    state.noticeList[key].push({
                        gid: notice.gid,
                        title: notice.title,
                        content: notice.content,
                        sendTime: notice.sendTime,
                        join: notice.join,
                        quit: notice.quit,
                        forceQuit: notice.forceQuit,
                        confirm: notice.confirm,
                        verified: notice.verified,
                        receiverNickname: notice.receiverNickname,
                        avatarUrl: notice.userInfo.avatar,
                        senderNickname: notice.userInfo.nickname,
                        gender: notice.userInfo.gender,
                        senderUsername: notice.senderUsername,
                        receiverUsername: notice.receiverUsername,
                        groupName: notice.groupInfo.groupName,
                        flag: notice.flag,
                        flagTime: notice.sendTime,
                        businessType: notice.businessType,
                    })
                }
            }
            console.log("初始化群通知完成")
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
                // 通知订阅
                context.state.stomp.subscribe('/user/queue/chat', msg => {
                    let receiveMsg = JSON.parse(msg.body);
                    if (context.state.currentUser === null || context.state.currentUser.username !== receiveMsg.sendUsername) {
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
                context.state.stomp.subscribe('/topic/chat', notice => {
                    context.commit('addNoticeServer', notice.body);
                });
                context.state.stomp.subscribe('/user/topic/notice/server', notice => {
                    context.commit('addNoticeServer', notice.body);
                });
                context.state.stomp.subscribe('/user/topic/notice/friend', notice => {
                    context.commit('addNoticeFriend', notice.body);
                });
                context.state.stomp.subscribe('/user/topic/notice/group', notice => {
                    context.commit('addNoticeGroup', notice.body);
                });

                let user;
                getRequest('/client/login/info').then(loginInfo => {
                    user = loginInfo;
                    context.commit('INIT_CURRENTUSER', loginInfo)
                }).then(() => {
                    getRequest('/friend/list?username=' + context.state.currentLogin.username).then(friendList => {
                        context.commit('INIT_FRIEND_LIST', friendList)
                    })
                    getRequest('/group/list?username=' + context.state.currentLogin.username).then(groupList => {
                        context.commit('INIT_GROUP_LIST', groupList)
                    }).then(() => {
                        getRequest('/message/history/username?username=' + context.state.currentLogin.username).then(msgList => {
                            context.commit('INIT_HISTORY_MSG', msgList)
                        })
                        getRequest('/notice-server/history/username?username=' + context.state.currentLogin.username).then(noticeList => {
                            context.commit('INIT_NOTICE_SERVER', noticeList)
                        })
                        getRequest('/notice-friend/history/username?username=' + context.state.currentLogin.username).then(noticeList => {
                            context.commit('INIT_NOTICE_FRIEND', noticeList)
                        })
                        getRequest('/notice-group/history/username?username=' + context.state.currentLogin.username).then(noticeList => {
                            context.commit('INIT_NOTICE_GROUP', noticeList)
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

/*
store.watch(function (state) {
    return state.msgList;
}, function (value, oldValue) {
    // window.sessionStorage.setItem('history-message', JSON.stringify(value));
}, {
    deep: true
})

store.watch(function (state) {
    return state.noticeList;
}, function (value, oldValue) {
    // window.sessionStorage.setItem('notice', JSON.stringify(value));
}, {
    deep: true
})

store.watch(function (state) {
    return state.noticeList;
}, function (value, oldValue) {
    // window.sessionStorage.setItem('friend', JSON.stringify(value));
}, {
    deep: true
})
*/

export default store;