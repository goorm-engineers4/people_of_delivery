package com.example.cloudfour.peopleofdelivery.integration;

import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.enums.MenuStatus;
import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.global.auth.dto.AuthRequestDTO;
import com.example.cloudfour.peopleofdelivery.global.auth.entity.VerificationCode;
import com.example.cloudfour.peopleofdelivery.global.auth.repository.VerificationCodeRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.VerificationPurpose;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DisplayName("가게 사장님(OWNER) 역할 통합 테스트")
@ActiveProfiles("test")
class IntegrationOwnerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @LocalServerPort
    private int port;

    private static String accessToken;
    private static UUID storeId;
    private static UUID menuId;
    private static UUID menuOptionId;
    private static String userEmail;
    @Autowired
    private StoreRepository storeRepository;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (accessToken != null) {
            headers.set("Authorization", "Bearer " + accessToken);
        }
        System.out.println("accessToken: "+accessToken);
        return headers;
    }

    private void assertApiSuccess(ResponseEntity<String> response) throws Exception {
        System.out.println("응답 상태: " + response.getStatusCode());
        if (!response.getStatusCode().is2xxSuccessful()) {
            System.out.println("응답 본문: " + response.getBody());
        }

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        assertThat(jsonNode.get("isSuccess").asBoolean()).isTrue();
    }

    private void assertApiFailure(ResponseEntity<String> response, String description) throws Exception {
        System.out.println("실제 응답 상태: " + response.getStatusCode());
        System.out.println("응답 본문: " + response.getBody());
        System.out.println("검증 내용: " + description);

        assertThat(response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()).isTrue();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        assertThat(jsonNode.get("isSuccess").asBoolean()).isFalse();
    }

    private JsonNode getResponseResult(ResponseEntity<String> response) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        return jsonNode.get("result");
    }

    private void performEmailVerification(String email) throws Exception {
        VerificationCode testCode = VerificationCode.builder()
                .email(email)
                .code("123456")
                .purpose(VerificationPurpose.EMAIL_VERIFY)
                .expiryDate(LocalDateTime.now().plusMinutes(30))
                .build();
        verificationCodeRepository.save(testCode);

        String verifyRequestBody = String.format(
                "{\"email\":\"%s\",\"code\":\"%s\"}", email, "123456"
        );
        HttpEntity<String> verifyEntity = new HttpEntity<>(verifyRequestBody, createAuthHeaders());
        ResponseEntity<String> verifyResponse = testRestTemplate.postForEntity(
                baseUrl() + "/auth/email/verify", verifyEntity, String.class
        );

        assertApiSuccess(verifyResponse);
    }

    private void loginAsStoreOwner() throws Exception {
        AuthRequestDTO.LoginRequestDto loginRequest = new AuthRequestDTO.LoginRequestDto(
            "store.owner@test.com", 
            "password123"
        );

        HttpEntity<AuthRequestDTO.LoginRequestDto> loginEntity = 
            new HttpEntity<>(loginRequest, createAuthHeaders());
        ResponseEntity<String> loginResponse = testRestTemplate.postForEntity(
            baseUrl() + "/auth/login", loginEntity, String.class
        );
        
        assertApiSuccess(loginResponse);
        JsonNode loginResult = getResponseResult(loginResponse);
        accessToken = loginResult.get("accessToken").asText();
    }

    // ===== 테스트 메소드 =====

    @Test
    @Order(1)
    @DisplayName("1단계: 사장님 로그인")
    void step01_ownerLogin() throws Exception {
        System.out.println("\n=== 1단계: 사장님 로그인 ===");

        userEmail = "store.owner@test.com";
        AuthRequestDTO.LoginRequestDto loginRequest = new AuthRequestDTO.LoginRequestDto(
                userEmail, "$2a$10$k.ue3dhy6IXJxVcNqaI5.OQY0QYDQBDsWYLQA7QRYwmzWgZqbMZyS"
        );

        HttpEntity<AuthRequestDTO.LoginRequestDto> loginEntity =
                new HttpEntity<>(loginRequest, createAuthHeaders());
        ResponseEntity<String> loginResponse = testRestTemplate.postForEntity(
                baseUrl() + "/auth/login", loginEntity, String.class
        );

        if (!loginResponse.getStatusCode().is2xxSuccessful()) {
            System.out.println("기존 계정 로그인 실패, 새 계정 생성");
            
            userEmail = "integration.owner.test." + System.currentTimeMillis() + "@example.com";
            AuthRequestDTO.RegisterRequestDto registerRequest = new AuthRequestDTO.RegisterRequestDto(
                    userEmail, "통합테스트사장님", "SecurePassword123!@#", "010-1234-5678"
            );

            HttpEntity<AuthRequestDTO.RegisterRequestDto> registerEntity =
                    new HttpEntity<>(registerRequest, createAuthHeaders());
            ResponseEntity<String> registerResponse = testRestTemplate.postForEntity(
                    baseUrl() + "/auth/register/owner", registerEntity, String.class
            );

            assertApiSuccess(registerResponse);
            System.out.println("사장님 계정 생성 성공: " + userEmail);

            performEmailVerification(userEmail);
            System.out.println("이메일 인증 완료");

            loginRequest = new AuthRequestDTO.LoginRequestDto(
                    userEmail, "SecurePassword123!@#"
            );

            loginEntity = new HttpEntity<>(loginRequest, createAuthHeaders());
            loginResponse = testRestTemplate.postForEntity(
                    baseUrl() + "/auth/login", loginEntity, String.class
            );
        }

        assertApiSuccess(loginResponse);
        JsonNode loginResult = getResponseResult(loginResponse);

        if (loginResult != null && loginResult.has("accessToken")) {
            accessToken = loginResult.get("accessToken").asText();
            System.out.println("JWT 토큰 획득 성공");
        } else {
            throw new RuntimeException("JWT 토큰을 받지 못했습니다.");
        }

        System.out.println("1단계 완료\n");
    }

    @Test
    @Order(2)
    @DisplayName("2단계: 가게 생성")
    void step02_createStore() throws Exception {
        System.out.println("\n=== 2단계: 가게 생성 ===");

        StoreRequestDTO.StoreCreateRequestDTO storeCreateRequest = StoreRequestDTO.StoreCreateRequestDTO.builder()
                .name("통합테스트 치킨집")
                .address("서울시 강남구 테스트로 123")
                .phone("02-1234-5678")
                .content("통합테스트용 치킨집입니다.")
                .operationHours("10:00-22:00")
                .closedDays("월요일")
                .minPrice(15000)
                .deliveryTip(3000)
                .storePicture("test-chicken.jpg")
                .category("치킨") // 치킨 카테고리
                .regionId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001")) // 서울시 강남구
                .build();

        HttpEntity<StoreRequestDTO.StoreCreateRequestDTO> storeEntity =
                new HttpEntity<>(storeCreateRequest, createAuthHeaders());
        ResponseEntity<String> storeResponse = testRestTemplate.postForEntity(
                baseUrl() + "/api/stores", storeEntity, String.class
        );

        assertApiSuccess(storeResponse);
        JsonNode storeResult = getResponseResult(storeResponse);
        storeId = UUID.fromString(storeResult.get("storeId").asText());

        System.out.println("가게 생성 완료: " + storeId);
        System.out.println("2단계 완료\n");
    }

    @Test
    @Order(3)
    @DisplayName("3단계: 가게 정보 수정")
    void step03_updateStore() throws Exception {
        System.out.println("\n=== 3단계: 가게 정보 수정 ===");
        StoreRequestDTO.StoreUpdateRequestDTO storeUpdateRequest = StoreRequestDTO.StoreUpdateRequestDTO.builder()
                .name("수정된 통합테스트 치킨집")
                .address("서울시 강남구 수정로 456")
                .category("치킨")
                .build();

        HttpEntity<StoreRequestDTO.StoreUpdateRequestDTO> storeEntity =
                new HttpEntity<>(storeUpdateRequest, createAuthHeaders());
        ResponseEntity<String> storeResponse = testRestTemplate.exchange(
                baseUrl() + "/api/stores/" + storeId,
                HttpMethod.PATCH, storeEntity, String.class
        );
        assertApiSuccess(storeResponse);
        System.out.println("가게 정보 수정 완료: " + storeId);
        System.out.println("3단계 완료\n");
    }

    @Test
    @Order(4)
    @DisplayName("4단계: 메뉴 생성")
    void step04_createMenu() throws Exception {
        System.out.println("\n=== 4단계: 메뉴 생성 ===");

        if (storeId == null) {
            storeId = UUID.fromString("550e8400-e29b-41d4-a716-446655440007");
        }


        MenuRequestDTO.MenuCreateRequestDTO menuCreateRequest = MenuRequestDTO.MenuCreateRequestDTO.builder()
                .name("통합테스트 치킨123")
                .content("통합테스트용 맛있는 치킨")
                .menuPicture("test-menu.jpg")
                .price(20000)
                .category("치킨")
                .status(MenuStatus.판매중)
                .build();

        HttpEntity<MenuRequestDTO.MenuCreateRequestDTO> menuEntity =
                new HttpEntity<>(menuCreateRequest, createAuthHeaders());
        ResponseEntity<String> menuResponse = testRestTemplate.postForEntity(
                baseUrl() + "/api/menus/" + storeId, menuEntity, String.class
        );

        assertApiSuccess(menuResponse);
        JsonNode menuResult = getResponseResult(menuResponse);
        menuId = UUID.fromString(menuResult.get("menuId").asText());

        System.out.println("메뉴 생성 완료: " + menuId);
        System.out.println("4단계 완료\n");
    }

    @Test
    @Order(5)
    @DisplayName("5단계: 메뉴 정보 수정")
    void step05_updateMenu() throws Exception {
        System.out.println("\n=== 5단계: 메뉴 정보 수정 ===");

        if (menuId == null) {
            menuId = UUID.fromString("550e8400-e29b-41d4-a716-446655440009");
        }

        MenuRequestDTO.MenuUpdateRequestDTO menuUpdateRequest = MenuRequestDTO.MenuUpdateRequestDTO.builder()
                .name("수정된 통합테스트 치킨123")
                .content("수정된 통합테스트용 맛있는 치킨")
                .menuPicture("updated-test-menu.jpg")
                .price(22000)
                .status(MenuStatus.판매중)
                .category("치킨")
                .build();

        HttpEntity<MenuRequestDTO.MenuUpdateRequestDTO> menuEntity =
                new HttpEntity<>(menuUpdateRequest, createAuthHeaders());
        ResponseEntity<String> menuResponse = testRestTemplate.exchange(
                baseUrl() + "/api/menus/" + menuId,
                HttpMethod.PATCH, menuEntity, String.class
        );

        assertApiSuccess(menuResponse);
        System.out.println("메뉴 정보 수정 완료: " + menuId);
        System.out.println("5단계 완료\n");
    }

    @Test
    @Order(6)
    @DisplayName("6단계: 메뉴 옵션 생성")
    void step06_createMenuOption() throws Exception {
        System.out.println("\n=== 6단계: 메뉴 옵션 생성 ===");

        if (menuId == null) {
            menuId = UUID.fromString("550e8400-e29b-41d4-a716-446655440009");
        }

        MenuRequestDTO.MenuOptionCreateRequestDTO menuOptionCreateRequest = MenuRequestDTO.MenuOptionCreateRequestDTO.builder()
                .optionName("통합테스트 옵션")
                .additionalPrice(2000)
                .build();

        HttpEntity<MenuRequestDTO.MenuOptionCreateRequestDTO> menuOptionEntity =
                new HttpEntity<>(menuOptionCreateRequest, createAuthHeaders());
        ResponseEntity<String> menuOptionResponse = testRestTemplate.postForEntity(
                baseUrl() + "/api/menus/" + menuId + "/options", menuOptionEntity, String.class
        );

        assertApiSuccess(menuOptionResponse);
        JsonNode menuOptionResult = getResponseResult(menuOptionResponse);
        menuOptionId = UUID.fromString(menuOptionResult.get("menuOptionId").asText());

        System.out.println("메뉴 옵션 생성 완료: " + menuOptionId);
        System.out.println("6단계 완료\n");
    }

    @Test
    @Order(7)
    @DisplayName("7단계: 메뉴 옵션 정보 수정")
    void step07_updateMenuOption() throws Exception {
        System.out.println("\n=== 7단계: 메뉴 옵션 정보 수정 ===");

        if (menuOptionId == null) {
            menuOptionId = UUID.fromString("550e8400-e29b-41d4-a716-446655440010");
        }

        MenuRequestDTO.MenuOptionStandaloneUpdateRequestDTO menuOptionUpdateRequest = MenuRequestDTO.MenuOptionStandaloneUpdateRequestDTO.builder()
                .optionName("수정된 통합테스트 옵션")
                .additionalPrice(2500)
                .build();

        HttpEntity<MenuRequestDTO.MenuOptionStandaloneUpdateRequestDTO> menuOptionEntity =
                new HttpEntity<>(menuOptionUpdateRequest, createAuthHeaders());
        ResponseEntity<String> menuOptionResponse = testRestTemplate.exchange(
                baseUrl() + "/api/menus/options/" + menuOptionId,
                HttpMethod.PATCH, menuOptionEntity, String.class
        );

        assertApiSuccess(menuOptionResponse);
        System.out.println("메뉴 옵션 정보 수정 완료: " + menuOptionId);
        System.out.println("7단계 완료\n");
    }

    @Test
    @Order(8)
    @DisplayName("8단계: 메뉴 옵션 삭제")
    void step09_deleteMenuOption() throws Exception {
        System.out.println("\n=== 8단계: 메뉴 옵션 삭제 ===");

        if (menuOptionId == null) {
            menuOptionId = UUID.fromString("550e8400-e29b-41d4-a716-446655440010");
        }

        HttpEntity<String> entity = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<String> menuOptionResponse = testRestTemplate.exchange(
                baseUrl() + "/api/menus/options/" + menuOptionId + "/deleted",
                HttpMethod.DELETE, entity, String.class
        );

        assertApiSuccess(menuOptionResponse);
        System.out.println("메뉴 옵션 삭제 완료: " + menuOptionId);
        System.out.println("8단계 완료\n");
    }

    @Test
    @Order(9)
    @DisplayName("9단계: 메뉴 삭제")
    void step10_deleteMenu() throws Exception {
        System.out.println("\n=== 9단계: 메뉴 삭제 ===");

        if (menuId == null) {
            menuId = UUID.fromString("550e8400-e29b-41d4-a716-446655440009");
        }

        HttpEntity<String> entity = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<String> menuResponse = testRestTemplate.exchange(
                baseUrl() + "/api/menus/" + menuId + "/deleted",
                HttpMethod.DELETE, entity, String.class
        );

        assertApiSuccess(menuResponse);
        System.out.println("메뉴 삭제 완료: " + menuId);
        System.out.println("9단계 완료\n");
    }

    @Test
    @Order(10)
    @DisplayName("10단계: 가게 삭제")
    void step11_deleteStore() throws Exception {
        System.out.println("\n=== 10단계: 가게 삭제 ===");

        if (storeId == null) {
            storeId = UUID.fromString("550e8400-e29b-41d4-a716-446655440007");
        }

        HttpEntity<String> entity = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<String> storeResponse = testRestTemplate.exchange(
                baseUrl() + "/api/stores/" + storeId + "/deleted",
                HttpMethod.PATCH, entity, String.class
        );

        assertApiSuccess(storeResponse);
        System.out.println("가게 삭제 완료: " + storeId);
        System.out.println("10단계 완료\n");
    }
}