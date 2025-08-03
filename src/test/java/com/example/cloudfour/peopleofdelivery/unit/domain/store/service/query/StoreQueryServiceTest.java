package com.example.cloudfour.peopleofdelivery.unit.domain.store.service.query;

import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.StoreCategory;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.LoginType;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserException;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.service.query.StoreQueryService;
import com.example.cloudfour.peopleofdelivery.global.auth.userdetails.CustomUserDetails;
import com.example.cloudfour.peopleofdelivery.fixtures.Factory;
import com.example.cloudfour.peopleofdelivery.global.entity.BaseEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class StoreQueryServiceTest {

    @Mock private StoreRepository storeRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private StoreQueryService storeQueryService;

    @Test
    @DisplayName("키워드로 전체 가게 조회 성공 (nextCursor 반환)")
    void getAllStores_success() {
                User user = Factory.createMockUserWithRole(Role.MASTER);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        LocalDateTime now = LocalDateTime.now();
        Store store1 = Store.builder()
                .id(UUID.randomUUID())
                .name("김밥천국")
                .build();
        ReflectionTestUtils.setField(store1, "createdAt", now.minusMinutes(1));

        Store store2 = Store.builder()
                .id(UUID.randomUUID())
                .name("치킨나라")
                .build();
        ReflectionTestUtils.setField(store2, "createdAt", now.minusMinutes(2));

        List<Store> stores = List.of(store1, store2);
        Slice<Store> storeSlice = new SliceImpl<>(stores, PageRequest.of(0, 2), true);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(storeRepository.findAllByKeyWord(anyString(), any(), any(Pageable.class))).thenReturn(storeSlice);

        StoreResponseDTO.StoreCursorListResponseDTO response = storeQueryService.getAllStores(
        null, 2, "김밥", userDetails);

        assertThat(response.getStoreList()).hasSize(2);
        assertThat(response.getNextCursor()).isEqualTo(store2.getCreatedAt());
    }

    @Test
    @DisplayName("키워드로 전체 가게 조회 - store가 없으면 nextCursor=null")
    void getAllStores_empty() {
                User user = Factory.createMockUserWithRole(Role.MASTER);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        Slice<Store> storeSlice = new SliceImpl<>(Collections.emptyList(), PageRequest.of(0, 2), false);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(storeRepository.findAllByKeyWord(anyString(), any(), any(Pageable.class))).thenReturn(storeSlice);

        StoreResponseDTO.StoreCursorListResponseDTO response = storeQueryService.getAllStores(
        null, 2, "없는가게", userDetails);

        assertThat(response.getStoreList()).isEmpty();
        assertThat(response.getNextCursor()).isNull();
    }

    @Test
    @DisplayName("키워드로 전체 가게 조회 실패 - 존재하지 않는 유저")
    void getAllStores_userNotFound() {
                CustomUserDetails userDetails = CustomUserDetails.of(UUID.randomUUID(), "fake@email.com", Role.MASTER, LoginType.LOCAL);
        when(userRepository.findById(any())).thenReturn(Optional.empty());

                assertThatThrownBy(() -> storeQueryService.getAllStores(null, 2, "김밥", userDetails))
                .isInstanceOf(UserException.class)
                .hasMessage(UserErrorCode.NOT_FOUND.getMessage());
    }


    @Test
    @DisplayName("카테고리별 가게 조회 성공")
    void getStoresByCategory_success() {
                User user = Factory.createMockUserWithRole(Role.OWNER);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        UUID categoryId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Store store1 = Store.builder()
                .id(UUID.randomUUID())
                .name("분식왕")
                .build();
        ReflectionTestUtils.setField(store1, "createdAt", now.minusMinutes(3));

        List<Store> stores = List.of(store1);
        Slice<Store> storeSlice = new SliceImpl<>(stores, PageRequest.of(0, 1), false);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(storeRepository.findAllByCategoryAndCursor(eq(categoryId), any(), any(Pageable.class)))
                .thenReturn(storeSlice);

        StoreResponseDTO.StoreCursorListResponseDTO response = storeQueryService.getStoresByCategory(
        categoryId, null, 1, userDetails);

        assertThat(response.getStoreList()).hasSize(1);
        assertThat(response.getNextCursor()).isNull();     }

    @Test
    @DisplayName("카테고리별 가게 조회 실패 - 존재하지 않는 유저")
    void getStoresByCategory_userNotFound() {
        CustomUserDetails userDetails =
                CustomUserDetails.of(UUID.randomUUID(), "email", Role.OWNER, LoginType.LOCAL);

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                storeQueryService.getStoresByCategory(UUID.randomUUID(), null, 1, userDetails)
        ).isInstanceOf(UserException.class)
                .hasMessage(UserErrorCode.NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("단일 가게 상세조회 성공")
    void getStoreById_success() throws Exception {
        User user = Factory.createMockUserWithRole(Role.OWNER);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        UUID storeId = UUID.randomUUID();

        StoreCategory category = StoreCategory.builder()
            .category("일식")
            .build();

        java.lang.reflect.Field idField = StoreCategory.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(category, UUID.randomUUID());

        Store store = Store.builder()
            .id(storeId)
            .name("스시천국")
            .storeCategory(category)
            .build();

        Field createdAtField = BaseEntity.class.getDeclaredField("createdAt");
        createdAtField.setAccessible(true);
        createdAtField.set(store, LocalDateTime.now());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        StoreResponseDTO.StoreDetailResponseDTO response = storeQueryService.getStoreById(storeId, userDetails);

        assertThat(response.getName()).isEqualTo("스시천국");
        assertThat(response.getStoreId()).isEqualTo(storeId);
    }



    @Test
    @DisplayName("단일 가게 상세조회 실패 - 유저 없음")
    void getStoreById_userNotFound() {
        CustomUserDetails userDetails =
            CustomUserDetails.of(UUID.randomUUID(), "email", Role.OWNER, LoginType.LOCAL);

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                storeQueryService.getStoreById(UUID.randomUUID(), userDetails)
        ).isInstanceOf(UserException.class)
                .hasMessage(UserErrorCode.NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("단일 가게 상세조회 실패 - 가게 없음")
    void getStoreById_storeNotFound() {
        User user = Factory.createMockUserWithRole(Role.OWNER);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        UUID storeId = UUID.randomUUID();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                storeQueryService.getStoreById(storeId, userDetails)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 가게를 찾을 수 없습니다.");
    }
}
