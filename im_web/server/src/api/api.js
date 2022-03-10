import {Message} from 'element-ui';
import router from "@/router";
import axios from "axios";

// 请求拦截器
axios.interceptors.request.use(config => {
    // 如果存在token，请求将携带这个token
    if (window.sessionStorage.getItem('tokenStr') != null) {
        config.headers['Authorization'] = window.sessionStorage.getItem('tokenStr');
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