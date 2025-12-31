package com.innowise.order.security;

import com.innowise.order.client.UserClient;
import com.innowise.order.entity.Order;
import com.innowise.order.repository.ItemRepository;
import com.innowise.order.repository.OrderItemRepository;
import com.innowise.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserClient userClient;

    public boolean isOrderOwnerOrAdmin(Long orderId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        if (hasAdminRole(auth)) return true;

        String currentEmail = auth.getName();

        return orderRepository.findById(orderId)
                .map(order -> {
                    var user = userClient.getUserById(order.getUserId());
                    return user != null && user.getEmail().equals(currentEmail);
                })
                .orElse(false);
    }

    public boolean isOrderItemOwnerOrAdmin(Long orderItemId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        if (hasAdminRole(auth)) return true;

        String currentEmail = auth.getName();

        return orderItemRepository.findById(orderItemId)
                .map(orderItem -> {
                    Order order = orderItem.getOrder();
                    if (order == null) return false;
                    var user = userClient.getUserById(order.getUserId());
                    return user != null && user.getEmail().equals(currentEmail);
                })
                .orElse(false);
    }

    public boolean isOwnerOrAdminByEmail(String email) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        if (hasAdminRole(auth)) return true;

        String currentEmail = auth.getName();
        return currentEmail.equalsIgnoreCase(email);
    }

    public boolean isOwnerOrAdminByUserId(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        if (hasAdminRole(auth)) return true;

        String currentEmail = auth.getName();
        var user = userClient.getUserById(userId);

        return user != null && user.getEmail().equalsIgnoreCase(currentEmail);
    }

    private boolean hasAdminRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}