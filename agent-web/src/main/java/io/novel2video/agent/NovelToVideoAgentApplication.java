package io.novel2video.agent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Novel to Video Agent - Main Application
 *
 * An intelligent agent system that transforms novels into videos
 * using agentscope-java ReActAgent with MySQL-based Skills repository.
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@MapperScan("io.novel2video.agent.mapper")
public class NovelToVideoAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(NovelToVideoAgentApplication.class, args);
    }
}
