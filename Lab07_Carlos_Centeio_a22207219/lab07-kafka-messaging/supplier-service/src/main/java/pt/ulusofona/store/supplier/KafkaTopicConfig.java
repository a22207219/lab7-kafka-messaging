package pt.ulusofona.store.supplier;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
@Configuration
public class KafkaTopicConfig {
 @Bean NewTopic supplierDeactivatedTopic() {
   return TopicBuilder.name("supplier-deactivated-events").partitions(1).replicas(1).build();
 }
}
