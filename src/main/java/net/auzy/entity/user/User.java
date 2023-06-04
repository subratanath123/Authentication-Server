package net.auzy.entity.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import net.auzy.entity.idp.IdpProvider;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.index.GeoIndexed;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document
@Data
public class User implements Serializable, UserDetails {

    @Id
    private String id;

    @NotEmpty
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "{invalid.email}")
    private String email;

    @NotEmpty
    private String password;

    @NotEmpty
    @Transient
    private String confirmPassword;

    private List<Role> roleList;

    private boolean verifiedUser;

    private String phone;

    private boolean isPrivateProfile;

    private List<IdpProvider> idpProviderList;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    private String displayName;

    private String gender;
    private String designation;
    private String organization;
    private String aboutUser;
    private String coverPhotoUrl;
    private String profilePictureUrl;

    private int age;

    private LocalDate birthDate;

    private boolean hasBusiness;

    private List<Address> addressList;

    @GeoIndexed
    private Point currentLocation;

    public User() {
        this.roleList = new ArrayList<>();
        this.idpProviderList = new ArrayList<>();
        this.addressList = new ArrayList<>();
    }

    @Override
    public Set<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        roleList.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.name())));

        return authorities;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
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
        return true;
    }

}