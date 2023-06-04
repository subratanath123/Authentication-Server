package net.auzy.service.user;

import net.auzy.dao.user.MongoUserProfileDao;
import net.auzy.dao.user.RedisBaseEntityDao;
import net.auzy.entity.idp.IdpProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import net.auzy.entity.idp.IdpUser;
import net.auzy.entity.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class RedisNewUserMessageProcessor implements StreamListener<String, ObjectRecord<String, IdpUser>> {

    private Logger log = LoggerFactory.getLogger(RedisNewUserMessageProcessor.class);

    @Autowired
    private RedisTemplate<String, RedisBaseEntity> redisBaseEntityRedisTemplate;

    @Autowired
    private RedisBaseEntityDao redisBaseEntityDao;

    @Autowired
    private GoogleUserMapperService googleUserMapperService;

    @Autowired
    private MongoUserProfileDao mongoUserProfileDao;

    @Override
    public void onMessage(ObjectRecord<String, IdpUser> record) {
        IdpUser idpUser = record.getValue();

        try {
            User user = idpUser.getIdpProvider() == IdpProvider.GOOGLE
                    ? googleUserMapperService.parseUserProfile(idpUser)
                    : null; //will add apple mapper once implemented

            if (user != null) {
                user = mongoUserProfileDao.save(user);

                RedisUserMetaInfo userMetaInfo = RedisUserMetaInfo.getRedisUserMetaInfoInstance(user);

                if (user.getAddressList() != null) {
                    Point location = user
                            .getAddressList()
                            .stream()
                            .filter(Objects::nonNull)
                            .map(Address::getLocation)
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse(null);

                    userMetaInfo.setLocation(location);

                    log.info("Saving user({}) address lat lng in geo spatial index", userMetaInfo.getEmail());
                }

                userMetaInfo.setMongoId(user.getId());

                // For storing geo indexed location
                redisBaseEntityDao.save(userMetaInfo);

                log.info("Message is consuming for user email {}", userMetaInfo.getEmail());
            }

        } catch (JsonProcessingException e) {
            log.warn("Exception occurred during consuming message", e);
            return;
        }

        redisBaseEntityRedisTemplate.opsForStream().acknowledge("auth-server-group-1", record);
    }
}