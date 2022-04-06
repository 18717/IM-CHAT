import Vue from 'vue'
import Vuex from 'vuex'
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import {convertTime, getRequest} from "@/api/api";
import {Notification} from 'element-ui';

Vue.use(Vuex)

new Date();

const store = new Vuex.Store({
    state: {
        // 消息列表
        msgList: {},
        // 群消息列表
        groupMsgList: {},
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
            currentUser.createTime = convertTime(currentUser.createTime);
            currentUser.updateTime = convertTime(currentUser.updateTime);
            Vue.set(state.isDot, currentUser.username + '#' + state.currentLogin.username, false);
        },
        changeCurrentGroup(state, currentGroup) {
            if (currentGroup != null) {
                state.chatType = 'public';
            } else {
                state.chatType = null;
            }
            state.currentGroup = currentGroup;
            state.currentGroup.createTime = convertTime(state.currentGroup.createTime);
            Vue.set(state.isDot, currentGroup.gid, false);
        },
        closeInfo(state) {
            state.showInfo = false;
        },
        showInfo(state) {
            state.showInfo = !state.showInfo;
        },
        // 取消当前所有的选中
        removeCurrent(state) {
            state.showInfo = false;
            state.currentUser = null;
            state.currentGroup = null;
        },
        addMessage(state, msg) {
            let key = state.currentLogin.username + '#' + msg.receiver;
            let msgArr = state.msgList[key];
            if (!msgArr) {
                Vue.set(state.msgList, key, []);
            }
            state.msgList[key].push({
                sender: msg.sender,
                receiver: msg.receiver,
                content: msg.content,
                contentType: msg.contentType,
                fileUrl: msg.fileUrl,
                sendTime: msg.time,
                self: msg.self,
            })
        },
        addGroupMessage(state, msg) {
            let key = msg.gid;
            let msgArr = state.groupMsgList[key];
            if (!msgArr) {
                Vue.set(state.groupMsgList, key, []);
            }
            state.groupMsgList[key].push({
                avatar: msg.avatar,
                nickname: msg.nickname,
                sender: msg.sender,
                contentType: msg.contentType,
                content: msg.content,
                fileUrl: msg.fileUrl,
                time: msg.time,
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
            if (obj.verified === true && obj.confirm === true) {
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
            obj.nickname = obj.user.nickname;
            obj.sendTime = obj.time;
            obj.flagTime = obj.time;
            obj.avatar = obj.user.avatar;
            state.noticeList[key].push(obj);
        },
        addNoticeGroup(state, notice) {
            let obj = JSON.parse(notice);
            if (obj.verified === true) {
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
            obj.nickname = obj.user.nickname;
            obj.avatar = obj.user.avatar;
            obj.gender = obj.user.gender;
            obj.groupName = obj.groupInfo.groupName;
            state.noticeList[key].push(obj);
        },
        // 解除好友关系时执行
        delFriend(state) {
            state.showInfo = false;
            state.currentUser = null;
            getRequest('/message/friend?username=' + state.currentLogin.username).then(msgList => {
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
            state.currentLogin.createTime = convertTime(state.currentLogin.createTime);
            state.currentLogin.updateTime = convertTime(state.currentLogin.updateTime)
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
                    if (loginUsername === msg.receiver) {
                        key = loginUsername + '#' + msg.sender;
                    } else {
                        key = loginUsername + '#' + msg.receiver;
                    }

                    if (!state.msgList[key]) {
                        Vue.set(state.msgList, key, []);
                    }

                    // 添加消息
                    state.msgList[key].push({
                        avatar: msg.user.avatar,
                        sender: msg.sender,
                        receiver: msg.receiver,
                        content: msg.content,
                        contentType: msg.contentType,
                        fileUrl: msg.fileUrl,
                        sendTime: msg.time,
                        self: msg.self,
                    })
                }
            }
            console.log("初始化历史消息记录完成")
        },
        INIT_HISTORY_GROUP_MSG(state, data) {
            if (data) {
                for (var key in data) {
                    Vue.set(state.groupMsgList, key, []);
                    let msgList = data[key];
                    for (var i = 0; i < msgList.length; i++) {
                        // 添加消息
                        state.groupMsgList[key].push({
                            avatar: msgList[i].user.avatar,
                            nickname: msgList[i].user.nickname,
                            sender: msgList[i].sender,
                            contentType: msgList[i].contentType,
                            content: msgList[i].content,
                            fileUrl: msgList[i].fileUrl,
                            time: msgList[i].time,
                            self: msgList[i].self,
                        })
                    }
                }
            }
            console.log("初始化群消息完成")
        },
        INIT_NOTICE_SERVER(state, data) {
            if (data) {
                let key = 'server';
                Vue.set(state.noticeList, key, []);
                for (let i = 0; i < data.length; i++) {
                    let notice = data[i];
                    state.noticeList[key].push({
                        nickname: notice.user.nickname,
                        title: notice.title,
                        fileName: notice.fileName,
                        fileUrl: notice.fileUrl,
                        content: notice.content,
                        time: notice.time,
                        pushNum: notice.pushNum,
                    })
                }
            }
            console.log("初始化系统通知完成")
        },
        INIT_NOTICE_FRIEND(state, data) {
            if (data) {
                let key = 'friend';
                Vue.set(state.noticeList, key, []);
                for (let i = 0; i < data.length; i++) {
                    let notice = data[i];
                    state.noticeList[key].push({
                        nickname: notice.user.nickname,
                        sender: notice.user.username,
                        avatar: notice.user.avatar,
                        gender: notice.user.gender,
                        add: notice.add,
                        del: notice.del,
                        confirm: notice.confirm,
                        title: notice.title,
                        content: notice.content,
                        sendTime: notice.time,
                        flagTime: notice.time,
                        verified: notice.verified,
                        flag: notice.flag,
                    })
                }
            }
            console.log("初始化好友通知完成")
        },
        INIT_NOTICE_GROUP(state, data) {
            if (data) {
                let key = 'group';
                Vue.set(state.noticeList, key, []);
                for (let i = 0; i < data.length; i++) {
                    let notice = data[i];
                    state.noticeList[key].push({
                        sender: notice.sender,
                        receiver: notice.receiver,
                        title: notice.title,
                        content: notice.content,
                        gid: notice.gid,
                        confirm: notice.confirm,
                        join: notice.join,
                        quit: notice.quit,
                        forceQuit: notice.forceQuit,
                        dismiss: notice.dismiss,
                        verified: notice.verified,
                        flag: notice.flag,
                        time: notice.time,
                        flagTime: notice.time,
                        result: notice.result,
                        avatar: notice.user.avatar,
                        nickname: notice.user.nickname,
                        gender: notice.user.gender,
                        groupName: notice.groupInfo.groupName,
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
                    if (context.state.currentUser === null ||
                        context.state.currentUser.username !== receiveMsg.sender) {
                        if (receiveMsg.contentType === 'img') {
                            Notification.info({
                                title: '【' + receiveMsg.user.nickname + '】发来一个图片',
                                message: receiveMsg.content.length > 10 ?
                                    receiveMsg.content.substr(0, 10) : receiveMsg.content,
                                position: 'bottom-right'
                            });
                        } else if (receiveMsg.contentType === 'file') {
                            Notification.info({
                                title: '【' + receiveMsg.user.nickname + '】发来一个文件',
                                message: receiveMsg.content.length > 10 ?
                                    receiveMsg.content.substr(0, 10) : receiveMsg.content,
                                position: 'bottom-right'
                            });
                        } else {
                            Notification.info({
                                title: '【' + receiveMsg.user.nickname + '】发来一条新消息',
                                message: receiveMsg.content.length > 10 ?
                                    receiveMsg.content.substr(0, 10) : receiveMsg.content,
                                position: 'bottom-right'
                            });
                        }
                        Vue.set(context.state.isDot, receiveMsg.sender + '#' + receiveMsg.receiver, true);
                    }
                    receiveMsg.self = 0;
                    receiveMsg.receiver = receiveMsg.sender;
                    context.commit('addMessage', receiveMsg);
                });
                context.state.stomp.subscribe('/user/queue/group/chat', msg => {
                    let receiveMsg = JSON.parse(msg.body);
                    if (context.state.currentGroup === null || context.state.currentGroup.gid !== receiveMsg.gid) {
                        Vue.set(context.state.isDot, receiveMsg.gid, true);
                    }
                    receiveMsg.nickname = receiveMsg.user.nickname;
                    receiveMsg.avatar = receiveMsg.user.avatar;
                    receiveMsg.self = 0;
                    context.commit('addGroupMessage', receiveMsg);
                });
                context.state.stomp.subscribe('/topic/server', notice => {
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
                // 初始化历史消息、通知、好友列表、群列表
                getRequest('/client/login/info').then(loginInfo => {
                    context.commit('INIT_CURRENTUSER', loginInfo.data)
                }).then(() => {
                    getRequest('/friend/list?username=' + context.state.currentLogin.username).then(friendList => {
                        context.commit('INIT_FRIEND_LIST', friendList)
                    })
                    getRequest('/group/list?username=' + context.state.currentLogin.username).then(groupList => {
                        context.commit('INIT_GROUP_LIST', groupList)
                    }).then(() => {
                        getRequest('/message/friend?username=' + context.state.currentLogin.username).then(msgList => {
                            context.commit('INIT_HISTORY_MSG', msgList)
                        })
                        getRequest('/message/group?username=' + context.state.currentLogin.username).then(msgList => {
                            context.commit('INIT_HISTORY_GROUP_MSG', msgList)
                        })
                        getRequest('/notice/server?username=' + context.state.currentLogin.username).then(noticeList => {
                            context.commit('INIT_NOTICE_SERVER', noticeList)
                        })
                        getRequest('/notice/friend?username=' + context.state.currentLogin.username).then(noticeList => {
                            context.commit('INIT_NOTICE_FRIEND', noticeList)
                        })
                        getRequest('/notice/group?username=' + context.state.currentLogin.username).then(noticeList => {
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