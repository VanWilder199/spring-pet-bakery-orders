package buloshnaya.orders.controller;

import buloshnaya.orders.model.Order;
import buloshnaya.orders.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final OrderService orderService;

    public AdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PutMapping("/orders/{userId}")
    public ResponseEntity<Order> updateOrderByAdmin(
            @PathVariable("userId") UUID userId,
            @RequestBody Order order
    ) {
        Order updatedOrder = orderService.updateOrderByAdmin(userId, order);
        return ResponseEntity.ok(updatedOrder);
    }
}
