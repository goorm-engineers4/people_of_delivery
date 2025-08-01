package com.example.cloudfour.peopleofdelivery.unit.domain.useraddress.entity;

import com.example.cloudfour.peopleofdelivery.domain.user.entity.UserAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAddressTest {

    @Test
    public void test(){
        //UserAddress userAddress = new UserAddress();

        // ID는 JPA에서 자동 생성되므로 테스트에서 직접 설정하지 않음
        UserAddress userAddress = UserAddress.builder()
                .address("서울시 강남구 역삼동 123-45")
                // .id(UUID.randomUUID()) //다른 곳에서 id를 생성할 수 있음 -> 문제 발생
                .build();

        // 생성된 UserAddress 객체 검증
        assertNotNull(userAddress);
        assertEquals("서울시 강남구 역삼동 123-45", userAddress.getAddress());
    }
}