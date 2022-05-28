package module4;

import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class JProducer {

	private final static String BOOTSTRAP_SERVERS = ":9092";

	private final static String TOPIC = "company-json";

	private final static String CLIENT_ID = "avro-test";

	public static void main(String[] args) {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaJsonSchemaSerializer.class.getName());
		props.put("schema.registry.url","http://localhost:8081");

		JCompany comp = new JCompany();
		//comp.setTradeNumber(12345);
		comp.setRegisteredName("MyCompany");
		final Producer<String, JCompany> producer = new KafkaProducer<>(props);
		try {
			final ProducerRecord<String, JCompany> data = new ProducerRecord<>(TOPIC, "12345", comp);
			try {
				RecordMetadata meta = producer.send(data).get();
				System.out.printf("lllllllllllllllll key=%s, Company name=%s => partition=%d, offset=%d\n", data.key(), data.value().getRegisteredName(),
						meta.partition(), meta.offset());
			} catch (InterruptedException | ExecutionException e) {
				System.out.printf("Exception %s\n", e.getMessage());
			}
		} finally {
			producer.flush();
			producer.close();
		}
	}
}

