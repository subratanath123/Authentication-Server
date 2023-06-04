package net.auzy.dao.user;

import net.auzy.entity.user.RedisUserMetaInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisBaseEntityDao extends CrudRepository<RedisUserMetaInfo, String> {

}
