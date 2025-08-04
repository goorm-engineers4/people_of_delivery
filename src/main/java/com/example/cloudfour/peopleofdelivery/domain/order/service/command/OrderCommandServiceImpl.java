package com.example.cloudfour.peopleofdelivery.domain.order.service.command;

import com.example.cloudfour.peopleofdelivery.domain.cart.entity.Cart;
import com.example.cloudfour.peopleofdelivery.domain.cart.exception.CartErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.cart.exception.CartException;
import com.example.cloudfour.peopleofdelivery.domain.cart.repository.CartRepository;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.entity.CartItem;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.exception.CartItemException;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.repository.CartItemRepository;
import com.example.cloudfour.peopleofdelivery.domain.order.converter.OrderConverter;
import com.example.cloudfour.peopleofdelivery.domain.order.converter.OrderItemConverter;
import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.Order;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.OrderItem;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderStatus;
import com.example.cloudfour.peopleofdelivery.domain.order.exception.OrderErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.order.exception.OrderException;
import com.example.cloudfour.peopleofdelivery.domain.order.repository.OrderItemRepository;
import com.example.cloudfour.peopleofdelivery.domain.order.repository.OrderRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreException;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserException;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandServiceImpl {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public OrderResponseDTO.OrderCreateResponseDTO createOrder(OrderRequestDTO.OrderCreateRequestDTO orderCreateRequestDTO,UUID cartId, User user) {
        userRepository.findById(user.getId()).orElseThrow(()->new UserException(UserErrorCode.NOT_FOUND));
        Cart cart = cartRepository.findById(cartId).orElseThrow(()->new CartException(CartErrorCode.NOT_FOUND));
        Store findStore = storeRepository.findById(cart.getId()).orElseThrow(()->new StoreException(StoreErrorCode.NOT_FOUND));
        List<CartItem> cartItems = cartItemRepository.findAllByCartId(cartId,user.getId());
        if(cartItems.isEmpty()) {
            throw new CartItemException(CartErrorCode.NOT_FOUND);
        }
        Integer totalPrice = 0;
        for (CartItem cartItem : cartItems) {
            totalPrice += cartItem.getPrice();
        }
        Order order = OrderConverter.toOrder(orderCreateRequestDTO,totalPrice);
        order.setStore(findStore);
        order.setUser(user);
        orderRepository.save(order);
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> OrderItemConverter.CartItemtoOrderItem(cartItem, order)).toList();
        orderItemRepository.saveAll(orderItems);
        cartRepository.delete(cart);
        return OrderConverter.toOrderCreateResponseDTO(order);
    }

    public OrderResponseDTO.OrderUpdateResponseDTO updateOrder(OrderRequestDTO.OrderUpdateRequestDTO orderUpdateRequestDTO, UUID orderId, User user) {
        userRepository.findById(user.getId()).orElseThrow(()->new UserException(UserErrorCode.NOT_FOUND));
        if(!orderRepository.existsByOrderIdAndUserId(orderId, user.getId())) {
            throw new OrderException(OrderErrorCode.UNAUTHORIZED_ACCESS);
        }
        Order order = orderRepository.findById(orderId).orElseThrow(()->new OrderException(OrderErrorCode.NOT_FOUND));
        OrderStatus prev_orderStatus = order.getStatus();
        order.updateOrderStatus(orderUpdateRequestDTO.getNewStatus());
        orderRepository.save(order);
        return OrderConverter.toOrderUpdateResponseDTO(order,prev_orderStatus);
    }

    public void deleteOrder(UUID orderId, User user) {
        userRepository.findById(user.getId()).orElseThrow(()->new UserException(UserErrorCode.NOT_FOUND));
        if(!orderRepository.existsByOrderIdAndUserId(orderId, user.getId())) {
            throw new OrderException(OrderErrorCode.UNAUTHORIZED_ACCESS);
        }
        Order order = orderRepository.findById(orderId).orElseThrow(()->new OrderException(OrderErrorCode.NOT_FOUND));
        order.softDelete();
    }
}
