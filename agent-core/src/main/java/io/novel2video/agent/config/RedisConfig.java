package io.novel2video.agent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis Configuration using Jedis
 *
 * Redis is used for:
 * 1. Task queue (Redis Streams) - replacing waoowaoo's BullMQ
 * 2. Session caching
 * 3. Skill repository cache
 */
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host:redis}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private int port;

    @Value("${spring.data.redis.password:}")
    private String password;

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(50);
        poolConfig.setMaxIdle(10);
        poolConfig.setMinIdle(5);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setJmxEnabled(false);

        if (password != null && !password.isEmpty()) {
            return new JedisPool(poolConfig, host, port, 2000, password);
        }
        return new JedisPool(poolConfig, host, port, 2000);
    }
}
