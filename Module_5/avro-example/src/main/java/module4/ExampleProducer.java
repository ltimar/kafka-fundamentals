package module4;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExampleProducer {

	//private static final Logger LOG = LoggerFactory.getLogger(ExampleProducer.class);

	private static final String BOOTSTRAP_SERVERS = ":9092";
	private static final String CLIENT_ID = "avroCl";
	private final static String TOPIC = "company";
	private static Producer<String, Company> producer;

	public static void main(String[] args) {

		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
		props.put("schema.registry.url","http://localhost:8081");

		producer = new KafkaProducer<>(props);

		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(() -> send(TOPIC), 0, 3, TimeUnit.SECONDS);

		Runtime.getRuntime().addShutdownHook(new Thread(producer::close));
	}

	@SuppressWarnings({ "boxing", "unused" })
	public static void send(String topic) {
		final int number = new Random().nextInt(10);
		Company comp = new Company();
		comp.setTradeNumber(number);
		comp.setRegisteredName("MyCompany");
		ProducerRecord<String, Company> data = new ProducerRecord<>(topic, "key" + number, comp);
		try {
			RecordMetadata meta = producer.send(data).get();
			System.out.println(
					String.format("-- Avro Producer --- key = %s, value = %s => partition = %d, offset= %d"
							,data.key(), data.value(), meta.partition(), meta.offset()));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			producer.flush();
		}
	}

}
