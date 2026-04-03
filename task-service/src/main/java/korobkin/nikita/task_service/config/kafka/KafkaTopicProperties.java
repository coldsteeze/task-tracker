package korobkin.nikita.task_service.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.kafka.topics")
@Getter
@Setter
public class KafkaTopicProperties {

    private String taskCreated;
    private String taskStatusChanged;
}
