package com.dev.BiteNowAPI.service;

import com.dev.BiteNowAPI.entity.OrderEntity;
import com.dev.BiteNowAPI.io.OrderRequest;
import com.dev.BiteNowAPI.io.OrderResponse;
import com.dev.BiteNowAPI.repository.CartRepository;
import com.dev.BiteNowAPI.repository.OrderRepository;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserService userService;
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public OrderServiceImpl(OrderRepository orderRepository, CartRepository cartRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userService = userService;
    }

    @Override
    public OrderResponse createOrderWithPayment(OrderRequest request) {

        try {
            String loggedInUserId = userService.findByUserId();

            OrderEntity order = convertToEntity(request);
            order.setUserId(loggedInUserId);
            order.setPaymentStatus("PENDING");
            order.setOrderStatus("CREATED");

            order = orderRepository.save(order);

            Stripe.apiKey = stripeSecretKey;

            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl("http://localhost:3000/payment-success?orderId=" + order.getId())
                            .setCancelUrl("http://localhost:3000/payment-failed?orderId=" + order.getId())
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L)
                                            .setPriceData(
                                                    SessionCreateParams.LineItem.PriceData.builder()
                                                            .setCurrency("lkr")
                                                            .setUnitAmount((long) (order.getAmount() * 100))
                                                            .setProductData(
                                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                            .setName("Food Order - BiteNow")
                                                                            .build()
                                                            )
                                                            .build()
                                            )
                                            .build()
                            )
                            .putMetadata("orderId", order.getId())
                            .build();

            Session session = Session.create(params);

            order.setStripeOrderId(session.getId());
            orderRepository.save(order);

            OrderResponse response = convertToResponse(order);
            response.setCheckoutUrl(session.getUrl());

            return response;

        } catch (Exception e) {
            // You can later replace this with a custom exception
            throw new RuntimeException("Failed to create order and initiate payment", e);
        }
    }

    @Override
    public void verifyPayment(String payload, String sigHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Invalid Stripe webhook signature", e);
        }

        // Ignore unneeded events
        if (event.getType().equals("checkout.session.completed")) {
            handlePaymentSuccess(event);
        }
    }


    private OrderEntity convertToEntity(OrderRequest request) {
        return OrderEntity.builder()
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .userAddress(request.getUserAddress())
                .amount(request.getAmount())
                .orderedItems(request.getOrderItems())
                .build();
    }

    private OrderResponse convertToResponse(OrderEntity newOrder) {
        return OrderResponse.builder()
                .id(newOrder.getId())
                .userAddress(newOrder.getUserAddress())
                .phoneNumber(newOrder.getPhoneNumber())
                .email(newOrder.getEmail())
                .amount(newOrder.getAmount())
                .paymentStatus(newOrder.getPaymentStatus())
                .stripeOrderId(newOrder.getStripeOrderId())
                .orderStatus(newOrder.getOrderStatus())
                .build();
    }

    @Transactional
    private void handlePaymentSuccess(Event event) {

        Session session = null;

        if (event.getDataObjectDeserializer().getObject().isPresent()) {
            session = (Session) event.getDataObjectDeserializer().getObject().get();
        } else {
            String rawJson = event.getData().getObject().toJson();
            session = Session.GSON.fromJson(rawJson, Session.class);
        }

        if (session == null || session.getId() == null) {
            throw new RuntimeException("Unable to deserialize checkout session");
        }

        Optional<OrderEntity> optionalOrder =
                orderRepository.findByStripeOrderId(session.getId());

        if (optionalOrder.isEmpty()) return;

        OrderEntity order = optionalOrder.get();

        if ("PAID".equals(order.getPaymentStatus())) {
            return;
        }

        order.setPaymentStatus("PAID");
        order.setOrderStatus("CONFIRMED");
        orderRepository.save(order);

        cartRepository.deleteByUserId(order.getUserId());
    }
}

