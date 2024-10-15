package com.example.bookrent2.rentBook.model.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;
    private String password;
    private String email;

    private boolean isOAuth2;

    @ManyToMany(fetch = FetchType.EAGER, cascade =
            {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))  //
    private Collection<Role> roles = new HashSet<>();


    // 이름 변경
    public User update(String nickname) {
        this.nickname = nickname;
        return this;
    }

    @Builder
    public User(String email, String password, Collection<Role> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.isOAuth2 = false;  // 기본적으로 이메일/비밀번호 사용자는 OAuth2가 아님
    }

    // 새로운 생성자: 구글 OAuth2 로그인용 (비밀번호 대신 닉네임을 설정)
    public User(String email, String nickname, Collection<Role> roles, boolean isOAuth2) {
        this.email = email;
        this.nickname = nickname;
        this.roles = roles;
        this.isOAuth2 = isOAuth2;  // OAuth2 여부를 구분
        if (isOAuth2) {
            this.password = null;  // OAuth2 사용자는 비밀번호가 없음
        }
    }

    /** Security ******************************************************************************* */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
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
        return true;
    }

}
