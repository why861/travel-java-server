import axios from 'axios'

// 创建axios的实例
const request = axios.create({
    baseURL: 'http://159.75.169.224:1236/api/travel',
    timeout: 60000,
    headers: {
        'Content-Type': 'application/json'
    }
})


// 封装拦截器
request.interceptors.request.use(
    config => {
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 封装响应拦截器
request.interceptors.response.use(
    response => {
        return response.data.data
    },
    error => {
        return Promise.reject(error)
    }
)

export function post(url, data) {
    return request.post(url, data)
}

export function get(url, params) {
    return request.get(url, { params })
}

// 处理流式接口

export async function fetchStream(url, data, onChunk, onComplete, onError) {
    // 中止的请求控制器
    const controller = new AbortController()

    try {
        // 发起一个请求
        const response = await fetch(`http://159.75.169.224:1236/api/travel/${url}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data),
            signal: controller.signal
        })

        // 获取响应体的可读流读取器
        const reader = response.body.getReader()
        console.log(reader)
        console.log(await reader.read(), 'read')
        // 将二进制数据解码为字符串
        const decoder = new TextDecoder()

        // 流数据解码
        while (true) {
            // done 表示流是否结束
            // value 表示读取到的二进制数据
            const { done, value } = await reader.read()
            if (done) break
            const chunk = decoder.decode(value, { stream: true })
            
            const lines = chunk.split('\n').filter(line => line.trim())

            for (const line of lines) {
                // data: {"type":"chunk","content":"你好"}
                console.log(line)
                try {
                    if (line.startsWith('data:')) {
                        const jsonStr = line.substring(5)
                        if (jsonStr) {
                             const jsonData = JSON.parse(jsonStr)
                            if (jsonData.type === 'chunk') {
                                // 返回分片信息
                                onChunk(jsonData.content)
                            } else if (jsonData.done) {
                                // 返回完成信息
                                onComplete()
                            } else if (jsonData.error) {
                                // 返回错误信息
                                onError(jsonData.error)
                            }
                        }
                    }
                } catch (error) {
                    onError('流式数据解析异常')
                }
            }
        }
        return controller.abort()
    } catch (error) {
        onError(error.message)
    }
}