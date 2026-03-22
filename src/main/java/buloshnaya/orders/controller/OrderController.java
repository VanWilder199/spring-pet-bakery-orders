package buloshnaya.orders.controller;

import buloshnaya.orders.annotation.MeasureExecution;
import buloshnaya.orders.filter.SearchFilter;
import buloshnaya.orders.kafka.dto.NotificationType;
import buloshnaya.orders.model.Order;
import buloshnaya.orders.security.UserPrincipal;
import buloshnaya.orders.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @MeasureExecution
    @GetMapping("/orders")
    public ResponseEntity<Page<Order>> searchOrderByFilter(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "status", required = false) NotificationType status
    ) {
        Page<Order> orders = orderService.searchOrderByFilter(userPrincipal,new SearchFilter(
                    size,
                    page,
                    status
        ));
        return ResponseEntity.ok(orders);
    }

    @MeasureExecution
    @GetMapping("admin/orders/{userId}")
    public ResponseEntity<Page<Order>> getOrdersByAdmin(
            @PathVariable("userId") UUID userId,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "status", required = false) NotificationType status
    ) {
        Page<Order> orders = orderService.searchOrderByFilterForAdmin(userId,new SearchFilter(
                size,
                page,
                status
        ));
        return ResponseEntity.ok(orders);
    }

    @MeasureExecution
    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
           @RequestBody @Valid Order order
    ) {
        logger.info("Creating order: {}", order);
        Order savedOrder = orderService.createOrder(userPrincipal, order);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }


    @MeasureExecution
    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("id") UUID id) {
        Order order = orderService.getOrderById(userPrincipal,id);
        return ResponseEntity.ok(order);
    }
 //   TODO  USER can update only List<Product>
    @PutMapping("/orders/{id}")
    public ResponseEntity<Order> updateOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("id") UUID id,
            @RequestBody Order order
    ) {
        Order updatedOrder = orderService.updateOrder(userPrincipal, id, order);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("id") UUID id) {
        orderService.deleteOrder(userPrincipal,id);
        return ResponseEntity.noContent().build();
    }
}
