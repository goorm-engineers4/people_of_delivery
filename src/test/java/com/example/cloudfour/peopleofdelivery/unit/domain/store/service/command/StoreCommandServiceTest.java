package com.example.cloudfour.peopleofdelivery.unit.domain.store.service.command;

import com.example.cloudfour.peopleofdelivery.unit.domain.TestFixtureFactory;
import com.example.cloudfour.peopleofdelivery.domain.region.repository.RegionRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.StoreCategory;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreCategoryRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
//import com.example.cloudfour.peopleofdelivery.domain.user.enums.LoginType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreCommandServiceTest {

    @InjectMocks
    private StoreCommandService storeCommandService;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private StoreCategoryRepository storeCategoryRepository;


    @Test
    @DisplayName("가게 생성 실패 - 중복된 이름")
    void createStore_fail_duplicate_name() {

        // given
        User user = TestFixtureFactory.createMockUserWithAll();
        StoreCategory category = user.getStores().get(0).getStoreCategory();

        StoreRequestDTO.StoreCreateRequestDTO request = StoreRequestDTO.StoreCreateRequestDTO.builder()
                .name("맛있는치킨") // 이미 존재하는 이름
                .address("서울 강남구 역삼동 123-45")
                .storePicture("https://cdn.example.com/store.jpg")
                .phone("02-123-4567")
                .content("정성 가득한 김밥집")
                .minPrice(12000)
                .deliveryTip(3000)
                .operationHours("10:00 - 22:00")
                .closedDays("일요일")
                .regionId(null)
                .storeCategoryId(category.getId())
                .build();

        // when
        when(storeRepository.existsByName("김밥천국")).thenReturn(true); // mocking: 동일한 이름 존재

        // then
        assertThatThrownBy(() -> storeCommandService.createStore(request, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 가게 이름입니다.");
    }



    @Test // 존재하지 않는 카테고리를 요청하여 가게를 생성하려고 할때.
    @DisplayName("가게 생성 실패 - 존재하지 않는 가게 카테고리")
    void createStore_fail_invalid_category() {

        // given
        User user = TestFixtureFactory.createMockUserWithAll(); // 사용자 mock
        UUID invalidCategoryId = UUID.randomUUID(); // 존재하지 않는 카테고리 ID

        StoreRequestDTO.StoreCreateRequestDTO request = StoreRequestDTO.StoreCreateRequestDTO.builder()
                .name("김밥천국")
                .address("서울 강남구 역삼동 123-45")
                .storePicture("https://cdn.example.com/store.jpg")
                .phone("02-123-4567")
                .content("정성 가득한 김밥집")
                .minPrice(12000)
                .deliveryTip(3000)
                .operationHours("10:00 - 22:00")
                .closedDays("일요일")
                .regionId(null)  // 지역 없이 생성
                .storeCategoryId(invalidCategoryId)
                .build();

        // when
        when(storeCategoryRepository.findById(invalidCategoryId)).thenReturn(Optional.empty()); // mocking: 없는 카테고리 ID를 조회하면 빈 Optional 반환

        // when & then
        assertThatThrownBy(() -> storeCommandService.createStore(request, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 카테고리를 찾을 수 없습니다.");
    }



    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"CUSTOMER", "RIDER"})
    @DisplayName("가게 생성 실패 - 가게 생성 권한없는 유저")
    void createStore_fail_no_permission(Role role) {
        // given
        User user = TestFixtureFactory.createMockUserWithRole(role); // ->  권한 없는 유저
        StoreCategory category = TestFixtureFactory.createMockStoreCategory();

        StoreRequestDTO.StoreCreateRequestDTO request = StoreRequestDTO.StoreCreateRequestDTO.builder()
                .name("김밥천국")
                .address("서울 강남구 역삼동 123-45")
                .storePicture("https://cdn.example.com/store.jpg")
                .phone("02-123-4567")
                .content("정성 가득한 김밥집")
                .minPrice(12000)
                .deliveryTip(3000)
                .operationHours("10:00 - 22:00")
                .closedDays("일요일")
                .regionId(null)
                .storeCategoryId(UUID.randomUUID()) // 굳이 의미 없음
                .build();

        // when & then
        assertThatThrownBy(() -> storeCommandService.createStore(request, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가게를 생성할 권한이 없습니다.");
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"MASTER", "OWNER"})
    @DisplayName("가게 생성 성공 - MASTER 유저")
    void createStore_success_with_master_role(Role role) {
        // given
        User user = TestFixtureFactory.createMockUserWithRole(role); // -> 가게 생성 가능 권한
        StoreCategory category = TestFixtureFactory.createMockStoreCategory();

        StoreRequestDTO.StoreCreateRequestDTO request = StoreRequestDTO.StoreCreateRequestDTO.builder()
                .name("김밥천국")
                .address("서울 강남구 역삼동 123-45")
                .storePicture("https://cdn.example.com/store.jpg")
                .phone("02-123-4567")
                .content("정성 가득한 김밥집")
                .minPrice(12000)
                .deliveryTip(3000)
                .operationHours("10:00 - 22:00")
                .closedDays("일요일")
                .regionId(null)
                .storeCategoryId(category.getId())
                .build();

        when(storeCategoryRepository.findById(category.getId())).thenReturn(Optional.of(category)); //mocking
        when(storeRepository.existsByName(request.getName())).thenReturn(false);
        when(storeRepository.save(any(Store.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        StoreResponseDTO.StoreCreateResponseDTO response = storeCommandService.createStore(request, user);

        // then
        assertThat(response.getName()).isEqualTo("김밥천국");
    }


}

