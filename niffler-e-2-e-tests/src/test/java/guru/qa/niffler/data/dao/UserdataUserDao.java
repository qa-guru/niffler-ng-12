package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.UserEntity;


import java.util.Optional;
import java.util.UUID;

public interface UserdataUserDao {
    UserEntity createUser(UserEntity user);

    void delete(UUID id);


    Optional<UserEntity> findById(UUID id);

    Optional<UserEntity> findByUsername(String username);
}
