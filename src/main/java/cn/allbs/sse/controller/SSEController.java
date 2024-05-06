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
 *
 * @author ChenQi
 * @date 2024/5/6
 */
@RestController
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
