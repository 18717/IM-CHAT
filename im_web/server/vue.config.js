let proxyObj = {}

proxyObj['/'] = {
    // websocket
    ws: false,
    // 目标地址
    target: 'http://localhost:8081',
    // 发送请求头host会被设置target
    changeOrigin: true,
    // 不重写请求地址
    pathRewrite: {
        '^/': '/'
    }
}

// 请求转发(websocket)
proxyObj['/ws'] = {
    ws:true,
    target: 'ws://localhost:8081'
};


module.exports = {
    devServer: {
        host: 'localhost',
        port: 8082,
        proxy: proxyObj
    }
}