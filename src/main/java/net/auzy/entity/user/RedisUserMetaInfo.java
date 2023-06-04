package net.auzy.entity.user;

import net.auzy.entity.token.TokenResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@RedisHash("user")
public class RedisUserMetaInfo extends RedisBaseEntity implements Serializable {

    private String email;
    private List<TokenResponse> tokenResponseList;

    public RedisUserMetaInfo() {
    }

    public RedisUserMetaInfo(String email, String mongoId, String name) {
        super(mongoId, email, null, name, "user", "user");
        this.email = email;
    }

    public RedisUserMetaInfo(String email, String mongoId, Point location, String name, String category, String type) {
        super(mongoId, email, location, name, category, type);
        this.email = email;
    }

    public static RedisUserMetaInfo getRedisUserMetaInfoInstance(User user) {
        return new RedisUserMetaInfo(user.getEmail(), user.getId(), user.getCurrentLocation(), user.getDisplayName(), "user", "user");
    }

}
