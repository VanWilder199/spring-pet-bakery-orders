package buloshnaya.orders.controller;

import buloshnaya.orders.filter.SearchFilter;
import buloshnaya.orders.model.Order;
import buloshnaya.orders.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<Order>> getOrders(
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "status", required = false) String status
    ) {
        Page<Order> orders = orderService.searchOrderByFilter(new SearchFilter(
                    size,
                    page,
                    status
        ));
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/orders")
    public ResponseEntity<Order> getOrders(
           @RequestBody @Valid Order order
    ) {
        logger.info("Creating order: {}", order);
        Order savedOrder = orderService.createOrder(order);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }


//    @GetMapping("/orders/{id}")
//    public ResponseEntity<Order> getOrderById(@PathVariable("id") Long id) {
//        Order order = orderService.getOrderById(id);
//        return ResponseEntity.ok(order);
//    }
//
//    @PutMapping("/orders/{id}")
//    public ResponseEntity<Order> updateOrder(
//            @PathVariable("id") Long id,
//            @RequestBody Order order
//    ) {
//        Order updatedOrder = orderService.updateOrder(id, order);
//        return ResponseEntity.ok(updatedOrder);
//    }
//
//    @DeleteMapping("/orders/{id}")
//    public ResponseEntity<Void> deleteOrder(@PathVariable("id") Long id) {
//        orderService.deleteOrder(id);
//        return ResponseEntity.noContent().build();
//    }
}
