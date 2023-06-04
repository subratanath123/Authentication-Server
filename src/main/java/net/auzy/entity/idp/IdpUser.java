package net.auzy.entity.idp;

import lombok.Data;
import net.auzy.entity.token.TokenResponse;

import java.io.Serializable;

@Data
public class IdpUser implements Serializable {

    private String email;
    private IdpProvider idpProvider;
    private TokenResponse tokenInfo;
    private String profilePictureUrl;

}
