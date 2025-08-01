package com.example.cloudfour.peopleofdelivery.domain.user.service;

import com.example.cloudfour.peopleofdelivery.domain.region.entity.Region;
import com.example.cloudfour.peopleofdelivery.domain.region.repository.RegionRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.dto.UserRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.dto.UserResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.UserAddress;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserAddressRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAddressService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final RegionRepository regionRepository;

    public void addAddress(UUID userId, UserRequestDTO.AddressRequestDTO req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Region region = regionRepository.findById(req.regionId())
                .orElseThrow(() -> new IllegalArgumentException("지역 정보를 찾을 수 없습니다."));

        UserAddress ua = UserAddress.builder()
                .address(req.address())
                .build();
        ua.setUser(user);
        ua.setRegion(region);

        userAddressRepository.save(ua);
    }


    public List<UserResponseDTO.AddressResponseDTO> getAddresses(UUID userId) {
        return userAddressRepository.findAllByUser_Id(userId).stream()
                .map(a -> UserResponseDTO.AddressResponseDTO.builder()
                        .addressId(a.getId())
                        .address(a.getAddress())
                        .regionId(a.getRegion().getId())
                        .build())
                .toList();
    }

    public void updateAddress(UUID userId, UUID addressId, UserRequestDTO.AddressRequestDTO req) {
        UserAddress ua = userAddressRepository.findByIdAndUser_Id(addressId, userId)
                .orElseThrow(() -> new IllegalArgumentException("주소를 찾을 수 없습니다."));

        ua.changeAddress(req.address());

        // 지역 변경 - 현재 region entity 수정 전 이후 추가 작업 예정
        if (!ua.getRegion().getId().equals(req.regionId())) {
            Region region = regionRepository.findById(req.regionId())
                    .orElseThrow(() -> new IllegalArgumentException("지역 정보를 찾을 수 없습니다."));
            ua.setRegion(region);
        }
        // JPA dirty checking으로 업데이트 반영
    }

    // soft delete
    public void deleteAddress(UUID userId, UUID addressId) {
        UserAddress ua = userAddressRepository.findByIdAndUser_Id(addressId, userId)
                .orElseThrow(() -> new IllegalArgumentException("주소를 찾을 수 없습니다."));
        userAddressRepository.delete(ua);
    }
}