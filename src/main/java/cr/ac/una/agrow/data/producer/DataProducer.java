package cr.ac.una.agrow.data.producer;

import cr.ac.una.agrow.domain.producer.Producer;
import cr.ac.una.agrow.service.producer.ProducerService;

import java.util.List;

public class DataProducer {

    private static final ProducerService producerService = new ProducerService();

    public static List<Producer> getAllProducers() {
        return producerService.getAllProducers();
    }

    public static List<Producer> getProducersByCity(String city) {
        return producerService.getProducersByCity(city);
    }

    public static Producer getProducer(int id_producer) {
        return producerService.getProducerById(id_producer);
    }

    public static String saveProducer(Producer p) {
        return producerService.saveProducerInternal(p);
    }

    public static String updateProducer(Producer p) {
        return producerService.updateProducerInternal(p);
    }

    public static String deleteProducer(int id_producer) {
        return producerService.deleteProducerInternal(id_producer);
    }
}
