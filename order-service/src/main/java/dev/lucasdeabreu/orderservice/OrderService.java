package dev.lucasdeabreu.orderservice;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@AllArgsConstructor
@Service
public class OrderService {

    private final OrderRepository repository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public Order createOrder(Order order) {
        order.setTransactionId(UUID.randomUUID().toString());
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        publish(order);

        log.debug("Saving an order {}", order);
        return repository.save(order);
    }

    private void publish(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(order);
        log.debug("Publishing an order created event {}", event);
        publisher.publishEvent(event);
    }

    public List<Order> findAll() {
        return repository.findAll();
    }

    @Transactional
    public void updateOrderAsBilled(Long id) {
        updateOrderStatus(id, OrderStatus.BILLED);
    }

    @Transactional
    public void updateOrderAsDone(Long id) {
        updateOrderStatus(id, OrderStatus.DONE);
    }

    private void updateOrderStatus(Long id, OrderStatus status) {
        log.debug("Updating Order {} to {}", id, status);
        Optional<Order> orderOptional = repository.findById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setStatus(status);
            repository.save(order);
        } else {
            log.error("Cannot update Order to {}, Order {} not found", status, id);
        }
    }
}
