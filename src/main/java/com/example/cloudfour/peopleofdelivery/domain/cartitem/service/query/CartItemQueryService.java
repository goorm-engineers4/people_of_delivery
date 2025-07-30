package com.example.cloudfour.peopleofdelivery.domain.cartitem.service.query;

import com.example.cloudfour.peopleofdelivery.domain.cartitem.dto.CartItemResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CartItemQueryService {

    public CartItemResponseDTO.CartItemListResponseDTO getCartItemById(UUID cartItemId, User user) {
        return null;
    }
}
