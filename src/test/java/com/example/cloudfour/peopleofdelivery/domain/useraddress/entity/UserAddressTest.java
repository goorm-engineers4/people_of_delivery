package com.example.cloudfour.peopleofdelivery.domain.useraddress.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserAddressTest {

    @Test
    public void test(){
        //UserAddress userAddress = new UserAddress();

        UserAddress userAddress = UserAddress.builder()
                .id(UUID.randomUUID()) //다른 곳에서 id를 생성할 수 있음 -> 문제 발생
                .build();
    }
}