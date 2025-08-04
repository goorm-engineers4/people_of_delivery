package com.example.cloudfour.peopleofdelivery.domain.payment.apiclient;

import com.example.cloudfour.peopleofdelivery.domain.payment.dto.TossApproveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TossApiClient {
    private final RestClient restClient;

    public TossApproveResponse approvePayment(String paymentKey, String orderId, int amount) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .headers(h -> h.setBasicAuth(tossSecretKey(), ""))
                .body(Map.of("paymentKey", paymentKey, "orderId", orderId, "amount", amount))
                .retrieve()
                .body(TossApproveResponse.class);
    }

    private String tossSecretKey() {
        return this.secretKey;
    }

    @Value("${toss.secret-key}")
    private String secretKey;
}


