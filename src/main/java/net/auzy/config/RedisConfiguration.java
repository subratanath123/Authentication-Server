package net.auzy.config;

import net.auzy.entity.idp.IdpUser;
import net.auzy.entity.user.RedisBaseEntity;
import net.auzy.service.user.RedisNewUserMessageProcessor;
import net.auzy.service.stream.RedisConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;

@Configuration
public class RedisConfiguration {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.username}")
    private String redisUsername;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Autowired
    private RedisConsumerService redisConsumerService;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        config.setUsername(redisUsername);
        config.setPassword(redisPassword);

        return new LettuceConnectionFactory(config);
    }

    @Bean
    public Subscription subscription(RedisConnectionFactory connectionFactory) throws UnknownHostException {
        redisConsumerService.createConsumerGroupIfNotExists(connectionFactory, "new-user", "auth-server-group-1");

        StreamOffset<String> streamOffset = StreamOffset.create("new-user", ReadOffset.lastConsumed());

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String,
                ObjectRecord<String, IdpUser>> options = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofMillis(100))
                .targetType(IdpUser.class)
                .build();

        StreamMessageListenerContainer<String, ObjectRecord<String, IdpUser>> container =
                StreamMessageListenerContainer
                        .create(connectionFactory, options);

        Subscription subscription =
                container.receive(Consumer.from("auth-server-group-1", InetAddress.getLocalHost().getHostName()),
                        streamOffset, newUserMessageProcessor());

        container.start();
        return subscription;
    }

    @Bean
    public StreamListener<String, ObjectRecord<String, IdpUser>> newUserMessageProcessor() {
        return new RedisNewUserMessageProcessor();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        // Configure serializers (e.g., JSON serializer)
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<String, RedisBaseEntity> redisTemplateForBaseEntity(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, RedisBaseEntity> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(RedisBaseEntity.class));

        return redisTemplate;
    }

}