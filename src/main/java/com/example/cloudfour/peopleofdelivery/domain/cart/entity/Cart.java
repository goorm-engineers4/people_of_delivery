package com.example.cloudfour.peopleofdelivery.domain.cart.entity;

import com.example.cloudfour.peopleofdelivery.domain.cartitem.entity.CartItem;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name="p_cart")
public class Cart {
    @Id
    @GeneratedValue
    private UUID id;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId" ,nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId" ,nullable = false)
    private Store store;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cart",cascade = CascadeType.ALL)
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    public static class CartBuilder{
        private CartBuilder id(UUID id){
            throw new UnsupportedOperationException("id 수정 불가");
        }
    }

    public void setUser(User user){
        this.user = user;
        user.setCart(this);
    }

    public void setStore(Store store){
        this.store = store;
        store.getCarts().add(this);
    }
}
