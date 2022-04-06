import {Message} from 'element-ui';
import router from "@/router";
import axios from "axios";

// 请求拦截器
axios.interceptors.request.use(config => {
    // 如果存在token，请求将携带这个token
    if (window.sessionStorage.getItem('token') != null) {
        config.headers['Authorization'] = window.sessionStorage.getItem('token');
    }
    return config;
}, error => {
    console.log(error)
})

// 响应拦截器
axios.interceptors.response.use(success => {
    // 业务逻辑错误
    if (success.status && success.status === 200) {
        if (success.data.code === 500 || success.data.code === 401 || success.data.code === 403) {
            Message.error({message: success.data.message});
            return;
        }
        if (success.data.message) {
            Message.success({message: success.data.message});
        }
    }
    return success.data;

}, error => {
    if (error.response.code === 504 || error.response.code === 404) {
        Message.error({message: '服务器出错了!'});
    } else if (error.response.code === 403) {
        Message.error({message: '请使用管理员账号登录！'});
    } else if (error.response.code === 401) {
        Message.error({message: '尚未登陆，请先登录!'});
        router.replace('/').then(resp => {
            console.log('看看这个是啥');
        });
    } else {
        if (error.response.data.message) {
            Message.error({message: error.response.data.message});
        } else {
            Message.error({message: '未知错误！'});
        }
    }
    return;
});

export const postRequest = (url, params) => {
    return axios.post(url, params);
}

export const getRequest = (url, params) => {
    return axios.get(url, params);
}

export const putRequest = (url, params) => {
    return axios.put(url, params);
}

export const deleteRequest = (url, params) => {
    return axios.delete(url, params);
}

// 格式化时间
Date.prototype.format = function(fmt) {
    var o = {
        "M+" : this.getMonth()+1,                 //月份
        "d+" : this.getDate(),                    //日
        "h+" : this.getHours(),                   //小时
        "m+" : this.getMinutes(),                 //分
        "s+" : this.getSeconds(),                 //秒
        "q+" : Math.floor((this.getMonth()+3)/3), //季度
        "S"  : this.getMilliseconds()             //毫秒
    };
    if(/(y+)/.test(fmt)) {
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    }
    for(var k in o) {
        if(new RegExp("("+ k +")").test(fmt)){
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        }
    }
    return fmt;
}