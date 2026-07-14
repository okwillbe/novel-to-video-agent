package io.novel2video.agent.worker;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Worker 启动器
 *
 * 通过配置 task.worker.enabled=true 启用
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "task.worker.enabled", havingValue = "true", matchIfMissing = true)
public class WorkerStarter {

    private final TaskExecutor taskExecutor;

    @PostConstruct
    public void start() {
        log.info("Starting task worker...");
        taskExecutor.startConsuming();
    }
}
