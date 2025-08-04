package com.example.cloudfour.peopleofdelivery.unit.domain.store.service.command;

import com.example.cloudfour.peopleofdelivery.domain.region.entity.Region;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.StoreCategory;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreException;
import com.example.cloudfour.peopleofdelivery.domain.store.service.command.StoreCommandService;
import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import com.example.cloudfour.peopleofdelivery.domain.region.repository.RegionRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreCategoryRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
import com.example.cloudfour.peopleofdelivery.fixtures.Factory;
import com.example.cloudfour.peopleofdelivery.global.auth.userdetails.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class StoreCommandServiceTest {
    @Mock private UserRepository userRepository;
    @InjectMocks private StoreCommandService storeCommandService;
    @Mock private StoreRepository storeRepository;
    @Mock private RegionRepository regionRepository;
    @Mock private StoreCategoryRepository storeCategoryRepository;

            
    @Test
    @DisplayName("가게 생성 실패 - 권한 없는 유저(CUSTOMER, RIDER)")
    void createStore_fail_no_permission() {
        for (Role role : new Role[]{Role.CUSTOMER, Role.RIDER}) {
            User user = Factory.createMockUserWithRole(role);
            CustomUserDetails userDetails = new CustomUserDetails(user);
                        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            StoreRequestDTO.StoreCreateRequestDTO request = StoreRequestDTO.StoreCreateRequestDTO.builder()
                    .name("김밥천국")
                    .category("분식")
                    .build();

            assertThatThrownBy(() -> storeCommandService.createStore(request, userDetails))
                    .isInstanceOf(StoreException.class)
                    .hasMessage("가게에 접근할 수 있는 권한이 없습니다.");
        }
    }

    @Test
    @DisplayName("가게 생성 실패 - 중복된 이름")
    void createStore_fail_duplicate_name() {
        User user = Factory.createMockUserWithRole(Role.OWNER);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user)); 
        StoreRequestDTO.StoreCreateRequestDTO request = StoreRequestDTO.StoreCreateRequestDTO.builder()
                .name("치킨나라")
                .category("치킨")
                .build();

        when(storeRepository.existsByName("치킨나라")).thenReturn(true);

        assertThatThrownBy(() -> storeCommandService.createStore(request, userDetails))
                .isInstanceOf(StoreException.class)
                .hasMessage("이미 등록된 가게입니다.");
    }

    @Test
    @DisplayName("가게 생성 성공 - 카테고리 명이 이미 존재하는 경우 기존 카테고리 재사용")
    void createStore_use_existing_category() {
        User user = Factory.createMockUserWithRole(Role.OWNER);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Region region = Factory.createMockRegion();
        ReflectionTestUtils.setField(region, "id", UUID.randomUUID());
        StoreCategory existCategory = StoreCategory.builder().category("치킨").build();
        ReflectionTestUtils.setField(existCategory, "id", UUID.randomUUID());

        StoreRequestDTO.StoreCreateRequestDTO request = StoreRequestDTO.StoreCreateRequestDTO.builder()
                .name("치킨나라")
                .address("서울 종로구")
                .storePicture("chicken.jpg")
                .phone("02-1111-2222")
                .content("치킨 맛집")
                .minPrice(15000)
                .deliveryTip(2000)
                .operationHours("10:00 - 22:00")
                .closedDays("화요일")
                .regionId(region.getId())
                .category("치킨")
                .build();

        when(regionRepository.findById(region.getId())).thenReturn(Optional.of(region));
        when(storeCategoryRepository.findByCategory("치킨")).thenReturn(Optional.of(existCategory));
        when(storeRepository.existsByName(request.getName())).thenReturn(false);
        when(storeRepository.save(any(Store.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StoreResponseDTO.StoreCreateResponseDTO response = storeCommandService.createStore(request, userDetails);

        assertThat(response.getName()).isEqualTo("치킨나라");
        verify(storeCategoryRepository).findByCategory("치킨");
    }

    @Test
    @DisplayName("가게 생성 성공 - 카테고리 명이 없을 경우 새로 생성")
    void createStore_create_new_category_if_not_exists() {
        User user = Factory.createMockUserWithRole(Role.OWNER);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Region region = Factory.createMockRegion();
        ReflectionTestUtils.setField(region, "id", UUID.randomUUID());
        String newCategoryName = "족발";

        StoreRequestDTO.StoreCreateRequestDTO request = StoreRequestDTO.StoreCreateRequestDTO.builder()
                .name("족발킹")
                .address("서울 노원구")
                .storePicture("jokbal.jpg")
                .phone("02-5555-1111")
                .content("족발 전문점")
                .minPrice(18000)
                .deliveryTip(3000)
                .operationHours("11:00 - 22:00")
                .closedDays("일요일")
                .regionId(region.getId())
                .category(newCategoryName)
                .build();

        when(regionRepository.findById(region.getId())).thenReturn(Optional.of(region));
        when(storeCategoryRepository.findByCategory(newCategoryName)).thenReturn(Optional.empty());
        when(storeCategoryRepository.save(any(StoreCategory.class)))
                .thenAnswer(invocation -> {
                    StoreCategory c = invocation.getArgument(0);
                    ReflectionTestUtils.setField(c, "id", UUID.randomUUID());
                    return c;
                });
        when(storeRepository.existsByName(request.getName())).thenReturn(false);
        when(storeRepository.save(any(Store.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StoreResponseDTO.StoreCreateResponseDTO response = storeCommandService.createStore(request, userDetails);

        assertThat(response.getName()).isEqualTo("족발킹");
        verify(storeCategoryRepository).save(any(StoreCategory.class));
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"MASTER", "OWNER"})
    @DisplayName("가게 생성 성공 - 권한 있는 유저")
    void createStore_success_with_permission(Role role) {
        User user = Factory.createMockUserWithRole(role);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Region region = Factory.createMockRegion();
        ReflectionTestUtils.setField(region, "id", UUID.randomUUID());
        StoreCategory category = StoreCategory.builder().category("분식").build();
        ReflectionTestUtils.setField(category, "id", UUID.randomUUID());

        StoreRequestDTO.StoreCreateRequestDTO request = StoreRequestDTO.StoreCreateRequestDTO.builder()
                .name("분식천국")
                .address("서울 중구 명동")
                .storePicture("bunsik.jpg")
                .phone("02-3333-4444")
                .content("명동 분식집")
                .minPrice(7000)
                .deliveryTip(1500)
                .operationHours("09:00 - 21:00")
                .closedDays("수요일")
                .regionId(region.getId())
                .category("분식")
                .build();

        when(regionRepository.findById(region.getId())).thenReturn(Optional.of(region));
        when(storeCategoryRepository.findByCategory("분식")).thenReturn(Optional.of(category));
        when(storeRepository.existsByName(request.getName())).thenReturn(false);
        when(storeRepository.save(any(Store.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StoreResponseDTO.StoreCreateResponseDTO response = storeCommandService.createStore(request, userDetails);

        assertThat(response.getName()).isEqualTo("분식천국");
        assertThat(response.getCategory()).isEqualTo("분식");
        assertThat(response.getAddress()).isEqualTo("서울 중구 명동");
    }

            
    @Test
    @DisplayName("가게 수정 실패 - 권한 없는 유저")
    void updateStore_fail_noPermission() {
        User storeOwner = Factory.createMockUserWithRole(Role.OWNER);
        User otherUser = Factory.createMockUserWithRole(Role.CUSTOMER);

        UUID storeOwnerId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        ReflectionTestUtils.setField(storeOwner, "id", storeOwnerId);
        ReflectionTestUtils.setField(otherUser, "id", otherUserId);

        CustomUserDetails otherUserDetails = new CustomUserDetails(otherUser);
        StoreCategory category = StoreCategory.builder().category("치킨").build();
        Store store = Store.builder()
                .id(UUID.randomUUID())
                .name("김밥천국")
                .user(storeOwner)
                .storeCategory(category)
                .build();

        StoreRequestDTO.StoreUpdateRequestDTO request = StoreRequestDTO.StoreUpdateRequestDTO.builder()
                .name("수정불가가게")
                .category("치킨")
                .build();

        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));

        assertThatThrownBy(() -> storeCommandService.updateStore(store.getId(), request, otherUserDetails))
                .isInstanceOf(StoreException.class)
                .hasMessage("가게에 접근할 수 있는 권한이 없습니다.");
    }

    @Test
    @DisplayName("가게 수정 실패 - 중복된 가게 이름")
    void updateStore_fail_duplicateName() {
        User user = Factory.createMockUserWithRole(Role.MASTER);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        StoreCategory category = StoreCategory.builder().category("한식").build();
        Store store = Store.builder()
                .id(UUID.randomUUID())
                .name("김밥천국")
                .user(user)
                .storeCategory(category)
                .build();

        StoreRequestDTO.StoreUpdateRequestDTO request = StoreRequestDTO.StoreUpdateRequestDTO.builder()
                .name("이미존재하는가게")
                .address("서울 강남구 역삼동 456-78")
                .category("한식")
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
        when(storeRepository.existsByName("이미존재하는가게")).thenReturn(true);

        assertThatThrownBy(() -> storeCommandService.updateStore(store.getId(), request, userDetails))
                .isInstanceOf(StoreException.class)
                .hasMessage("이미 등록된 가게입니다.");
    }

    @Test
    @DisplayName("가게 수정 성공 - 카테고리명 변경(신규)")
    void updateStore_success_change_category_to_new() {
                User user = Factory.createMockUserWithRole(Role.OWNER);
        UUID userId = UUID.randomUUID();
        ReflectionTestUtils.setField(user, "id", userId);

                CustomUserDetails userDetails = new CustomUserDetails(user);

                StoreCategory oldCategory = StoreCategory.builder().category("분식").build();
        ReflectionTestUtils.setField(oldCategory, "id", UUID.randomUUID());
        Store store = Store.builder()
                .id(UUID.randomUUID())
                .name("분식왕")
                .user(user)
                .storeCategory(oldCategory)
                .build();

                String newCategoryName = "족발";
        StoreRequestDTO.StoreUpdateRequestDTO request = StoreRequestDTO.StoreUpdateRequestDTO.builder()
                .name("족발왕")
                .address("서울 영등포구")
                .category(newCategoryName)
                .build();

                when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
        when(storeRepository.existsByName("족발왕")).thenReturn(false);
        when(storeCategoryRepository.findByCategory(newCategoryName)).thenReturn(Optional.empty());
        when(storeCategoryRepository.save(any(StoreCategory.class)))
                .thenAnswer(invocation -> {
                    StoreCategory c = invocation.getArgument(0);
                    ReflectionTestUtils.setField(c, "id", UUID.randomUUID());
                    return c;
                });
        when(storeRepository.save(any(Store.class))).thenAnswer(invocation -> invocation.getArgument(0));

                StoreResponseDTO.StoreUpdateResponseDTO response = storeCommandService.updateStore(store.getId(), request, userDetails);

        assertThat(response.getName()).isEqualTo("족발왕");
        assertThat(response.getCategory()).isEqualTo("족발");
        verify(storeCategoryRepository).save(any(StoreCategory.class));
    }


    @Test
    @DisplayName("가게 수정 성공 - 카테고리명 변경(기존)")
    void updateStore_success_change_category_to_existing() {
                User user = Factory.createMockUserWithRole(Role.OWNER);
        UUID userId = UUID.randomUUID();
        ReflectionTestUtils.setField(user, "id", userId);

                CustomUserDetails userDetails = new CustomUserDetails(user);

                StoreCategory oldCategory = StoreCategory.builder().category("분식").build();
        ReflectionTestUtils.setField(oldCategory, "id", UUID.randomUUID());
        StoreCategory existCategory = StoreCategory.builder().category("치킨").build();
        ReflectionTestUtils.setField(existCategory, "id", UUID.randomUUID());

        Store store = Store.builder()
                .id(UUID.randomUUID())
                .name("분식왕")
                .user(user)
                .storeCategory(oldCategory)
                .build();

        StoreRequestDTO.StoreUpdateRequestDTO request = StoreRequestDTO.StoreUpdateRequestDTO.builder()
                .name("치킨왕")
                .address("서울 동작구")
                .category("치킨")
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
        when(storeRepository.existsByName("치킨왕")).thenReturn(false);
        when(storeCategoryRepository.findByCategory("치킨")).thenReturn(Optional.of(existCategory));
        when(storeRepository.save(any(Store.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StoreResponseDTO.StoreUpdateResponseDTO response = storeCommandService.updateStore(store.getId(), request, userDetails);

        assertThat(response.getName()).isEqualTo("치킨왕");
        assertThat(response.getCategory()).isEqualTo("치킨");
        verify(storeCategoryRepository).findByCategory("치킨");
    }


    @Test
    @DisplayName("가게 수정 실패 - 존재하지 않는 가게")
    void updateStore_fail_storeNotFound() {
        User user = Factory.createMockUserWithRole(Role.MASTER);
        UUID userId = UUID.randomUUID();
        ReflectionTestUtils.setField(user, "id", userId);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        UUID storeId = UUID.randomUUID();

        StoreRequestDTO.StoreUpdateRequestDTO request = StoreRequestDTO.StoreUpdateRequestDTO.builder()
                .name("새로운김밥천국")
                .address("서울 강남구 역삼동 456-78")
                .category("한식")
                .build();

                when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> storeCommandService.updateStore(storeId, request, userDetails))
                .isInstanceOf(StoreException.class)
                .hasMessage("가게를 찾을 수 없습니다.");
    }



            
    @Test
    @DisplayName("가게 삭제 성공 - 마스터")
    void deleteStore_success_master() {
        User master = Factory.createMockUserWithRole(Role.MASTER);
        CustomUserDetails masterDetails = new CustomUserDetails(master);
        StoreCategory category = StoreCategory.builder().category("족발").build();
        Store store = Store.builder()
                .id(UUID.randomUUID())
                .name("족발킹")
                .user(master)
                .storeCategory(category)
                .build();

        when(userRepository.findById(master.getId())).thenReturn(Optional.of(master));
        when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
        when(storeRepository.existsByStoreAndUser(store.getId(), master.getId())).thenReturn(true);
        when(storeRepository.save(store)).thenReturn(store);

        storeCommandService.deleteStore(store.getId(), masterDetails);

        verify(storeRepository).save(store);
        assertThat(store.getIsDeleted()).isTrue();
        assertThat(store.getDeletedAt()).isNotNull();
    }



    @Test
    @DisplayName("가게 삭제 성공 - 가게 주인(OWNER)")
    void deleteStore_success_owner() {
        User owner = Factory.createMockUserWithRole(Role.OWNER);
        CustomUserDetails ownerDetails = new CustomUserDetails(owner);
        StoreCategory category = StoreCategory.builder().category("족발").build();
        Store store = Store.builder()
                .id(UUID.randomUUID())
                .name("족발킹")
                .user(owner)
                .storeCategory(category)
                .build();

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
        when(storeRepository.existsByStoreAndUser(store.getId(), owner.getId())).thenReturn(true);
        when(storeRepository.save(store)).thenReturn(store);

        storeCommandService.deleteStore(store.getId(), ownerDetails);

        verify(storeRepository).save(store);
        assertThat(store.getIsDeleted()).isTrue();
        assertThat(store.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("가게 삭제 실패 - 권한 없음(다른 OWNER)")
    void deleteStore_fail_no_permission() {
        User storeOwner = Factory.createMockUserWithRole(Role.OWNER);
        User otherUser = Factory.createMockUserWithRole(Role.OWNER);
        CustomUserDetails otherDetails = new CustomUserDetails(otherUser);
        StoreCategory category = StoreCategory.builder().category("족발").build();
        Store store = Store.builder()
                .id(UUID.randomUUID())
                .name("족발킹")
                .user(storeOwner)
                .storeCategory(category)
                .build();

        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));

        assertThatThrownBy(() -> storeCommandService.deleteStore(store.getId(), otherDetails))
                .isInstanceOf(StoreException.class)
                .hasMessage("가게에 접근할 수 있는 권한이 없습니다.");
    }

    @Test
    @DisplayName("가게 삭제 실패 - 존재하지 않는 가게")
    void deleteStore_fail_store_not_found() {
        User master = Factory.createMockUserWithRole(Role.MASTER);
        CustomUserDetails masterDetails = new CustomUserDetails(master);
        UUID storeId = UUID.randomUUID();

        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> storeCommandService.deleteStore(storeId, masterDetails))
                .isInstanceOf(StoreException.class)
                .hasMessage("가게를 찾을 수 없습니다.");
    }
}
