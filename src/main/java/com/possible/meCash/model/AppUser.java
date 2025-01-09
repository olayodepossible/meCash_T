package com.possible.mecash.model;

import com.possible.mecash.dto.enums.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
/*
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;*/


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Invalid firstName: must not be empty")
    @NotNull(message = "Invalid firstName:  must not be NULL")
    @Size(min = 3, max = 30, message = "Invalid firstName: Must be of 3 - 30 characters")
    private String firstName;

    @NotBlank(message = "Invalid lastName: must not be empty")
    @NotNull(message = "Invalid lastName: must not be NULL")
    @Size(min = 3, max = 30, message = "Invalid lastName: Must be of 3 - 30 characters")
    private String lastName;

    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Invalid Username: must not be empty")
    @NotNull(message = "Invalid Username:  must not be NULL")
    @Size(min = 3, max = 15, message = "Invalid UserName: Must be of 3 - 30 characters")
    private String username;

    @Min(value = 16, message = "Invalid Age: must be 16 years or more to register")
    @Max(value = 100, message = "Invalid Age: must not pass 100years")
    Integer age;

    @NotBlank(message = "Invalid Password: must not be empty")
    @NotNull(message = "Invalid Password:  must not be NULL")
    private String password;
    @NotNull(message = "User address must not be empty")

    @NotBlank(message = "Invalid Address: must not be empty")
    @NotNull(message = "Invalid Address:  must not be NULL")
    private String address;

    @NotBlank(message = "Invalid Phone number: must not be empty")
    @NotNull(message = "Invalid Phone number: must not be NULL")
    @Pattern(regexp = "^\\d{11}$", message = "Invalid phone number")
    private String phoneNumber;

    private boolean isIdentityProof;

    private String accountNumber;
    private boolean isEnable;

    private UserType userType;
    private BigDecimal accountBalance;

    @ManyToOne(cascade = CascadeType.ALL)
    private Role role;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany( mappedBy="user", cascade = CascadeType.ALL)
    private List<Account> accountList = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        return authorities;
    }

    public boolean setIsEnable(Boolean value){
        return value;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnable;
    }

}
