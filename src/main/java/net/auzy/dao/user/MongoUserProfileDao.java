package net.auzy.dao.user;

import net.auzy.entity.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoUserProfileDao extends MongoRepository<User, String> {

}
