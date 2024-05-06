
> [!前言]
> 不同于WebSockets提供了双向通信的能力，SSE只支持单向通信。但对于一些场景，如服务器向客户端发送通知或实时更新，SSE是一个简单且有效的选择。
# 说明
单一源事件（SSE）是一种用于实现服务器向客户端推送数据的网络技术。通常Web应用程序是基于请求-响应模式工作的，客户端需要定期向服务器发送请求以获取更新的数据。但是对于需要实时更新的应用，如聊天应用、股票市场更新等，这种轮询的方式效率不高。
SSE技术通过建立一次持久的连接，允许服务器主动向客户端发送数据，而不需要客户端发送请求。这种推送模式能够显著减少网络流量和服务器负载，同时实现实时更新。在SSE中，服务器向客户端发送一系列数据块，每个数据块以"event: "、"data: "和两个换行符开始，并以一个空行结束。客户端通过监听服务器发送的数据块来获取更新。
# 后端实现
以下模拟一个接口请求将每秒饭返回一条数据，持续十秒
```
package cn.allbs.sse.controller;  
  
import org.springframework.http.MediaType;  
import org.springframework.web.bind.annotation.GetMapping;  
import org.springframework.web.bind.annotation.RequestParam;  
import org.springframework.web.bind.annotation.RestController;  
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;  
  
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;  
  
/**  
 * 类 SSEController  
 * * @author ChenQi  
 * @date 2024/5/6  
 */@RestController  
public class SSEController {  
  
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();  
  
    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)  
    public SseEmitter receiveText(@RequestParam String question) {  
        SseEmitter emitter = new SseEmitter();  
        executorService.execute(() -> {  
            try {  
                // 处理接收到的文本内容  
                String processedText = processText(question);  
  
                // 将处理结果逐步发送给客户端  
                for (int i = 1; i <= 10; i++) {  
                    emitter.send(processedText.replace("问题", "回答") + i);  
                    Thread.sleep(1000); // 模拟每秒发送一个字符  
                }  
                emitter.complete(); // 发送完毕，关闭连接  
            } catch (Exception e) {  
                emitter.completeWithError(e); // 发生错误，关闭连接  
            }  
        });  
        return emitter;  
    }  
  
    // 在实际应用中，这里可以添加对文本内容的具体处理逻辑  
    private String processText(String text) {  
        return "这是问题:" + text;  
    }  
}
```
# 前端界面可以随意一点画下即可
```
<!DOCTYPE html>  
<html>  
<head>  
    <title>SSE Demo</title>  
    <meta charset="utf-8">  
    <script type="text/javascript">  
        var eventSource;  
  
        function stopSSE() {  
            if (eventSource) {  
                eventSource.close();  
            }  
        }  
  
        function sendText() {  
            var inputField = document.getElementById("input-field");  
            var text = inputField.value.trim();  
            if (text !== "") {  
                eventSource = new EventSource("/events?question=" + text);  
                eventSource.onmessage = function(event) {  
                    var textField = document.getElementById("result-field");  
                    textField.value += (event.data + ", ");  
                };  
                eventSource.onerror = function(event) {  
                    console.error("EventSource failed:", event);  
                    eventSource.close();  
                };  
            }  
        }  
    </script>  
</head>  
<body>  
<input type="text" id="input-field" placeholder="输入内容">  
<button onclick="sendText()">发送</button><br><br>  
<textarea id="result-field" rows="10" cols="50" readonly></textarea><br><br>  
<button onclick="stopSSE()">Stop SSE</button>  
</body>  
</html>
```
# 访问页面
http://{ip}:{port}/index.html
![image.png](https://nas.allbs.cn:9006/cloudpic/2024/05/c872ec5e4df64ff445475a4fd0444b73.png)
# 实现效果
![recording.gif](https://nas.allbs.cn:9006/cloudpic/2024/05/01629d5c3f96fd113e635ef9fa543d30.gif)
# demo地址
https://github.com/chenqi92/spring-sse.git
