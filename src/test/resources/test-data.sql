-- 지역 데이터
INSERT INTO p_region (id, si, gu, dong) VALUES
  ('550e8400-e29b-41d4-a716-446655440001', '서울시', '강남구', ''),
  ('550e8400-e29b-41d4-a716-446655440002', '서울시', '서초구', ''),
  ('00000000-0000-0000-0000-000000000001', '서울시', '종로구', '');

-- 음식점 카테고리
INSERT INTO p_storecategory (id, category) VALUES
  ('550e8400-e29b-41d4-a716-446655440003', '치킨'),
  ('550e8400-e29b-41d4-a716-446655440004', '피자'),
  ('550e8400-e29b-41d4-a716-446655440005', '한식');

-- 음식점 사장 사용자
-- 음식점 사장 사용자 (password123의 BCrypt 해시값)
INSERT INTO p_user (id, email, nickname, password, number, role, email_verified, login_type, created_at, updated_at, is_deleted) VALUES
    ('550e8400-e29b-41d4-a716-446655440006', 'store.owner@test.com', '치킨집사장', '$2a$10$iZD2oXy6qKzKBCqQpl.Wke7h9DQPp3HDLK5BYqO/QGEHEzh8CBKW.', '010-0000-0000', 'OWNER', true, 'LOCAL', NOW(), NOW(), false);
-- 음식점 데이터
INSERT INTO p_store (id, name, address, phone, content, operation_hours, closed_days, min_price, delivery_tip, rating, like_count, review_count, store_picture, store_category_id, region_id, user_id, created_at, updated_at, is_deleted, created_by, updated_by) VALUES
  ('550e8400-e29b-41d4-a716-446655440007', '맛���는 치킨집', '서울시 강남구 치킨로 123', '02-1234-5678', '바삭한 치킨 전문점', '10:00-22:00', '일요일', 15000, 3000, 4.5, 100, 50, 'chicken-store.jpg', '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440006', NOW(), NOW(), false, 1, 1),
  ('550e8400-e29b-41d4-a716-446655440008', '황금 치킨', '서울시 강남구 황금로 456', '02-2345-6789', '황금빛 바삭 치킨', '11:00-23:00', '월요일', 18000, 2500, 4.2, 80, 30, 'golden-chicken.jpg', '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440006', NOW(), NOW(), false, 1, 1);

-- 메뉴 카테고리 데이터
INSERT INTO p_menucategory (id, category) VALUES
  ('550e8400-e29b-41d4-a716-446655440003', '치킨');

-- 메뉴 데이터
INSERT INTO p_menu (id, name, content, menu_picture, price, menu_category_id, store_id, created_at, updated_at, status) VALUES
  ('550e8400-e29b-41d4-a716-446655440009', '후라이드 치킨', '바삭바삭한 후라이드 치킨', 'fried-chicken.jpg', 18000, '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440007', NOW(), NOW(), '판매중'),
  ('550e8400-e29b-41d4-a716-446655440011', '양념 치킨', '달콤매콤한 양념 치킨', 'seasoned-chicken.jpg', 19000, '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440007', NOW(), NOW(), '판매중');

-- 메뉴 옵션 데이터
INSERT INTO p_menuoption (menu_option_id, option_name, additional_price, menu_id) VALUES
  ('550e8400-e29b-41d4-a716-446655440010', '일반', 0, '550e8400-e29b-41d4-a716-446655440009'),
  ('550e8400-e29b-41d4-a716-446655440012', '순한맛', 0, '550e8400-e29b-41d4-a716-446655440011');

-- 테스트용 사용자 주소 (미리 생성)
INSERT INTO p_useraddress (id, address, region_id, user_id) VALUES
  ('00000000-0000-0000-0000-000000000002', '서울시 강남구 테헤란로 123', '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440006');
