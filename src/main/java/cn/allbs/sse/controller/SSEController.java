package cn.allbs.sse.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
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
    public SseEmitter streamEvents() {
        SseEmitter emitter = new SseEmitter();
        executorService.execute(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    // 发送事件数据到客户端
                    emitter.send("Event " + i);
                    // 模拟每秒发送一个事件
                    Thread.sleep(1000);
                }
                // 发送完毕，关闭连接
                emitter.complete();
            } catch (IOException | InterruptedException e) {
                // 发生错误，关闭连接
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }
}
