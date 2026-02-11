package com.maria.finance.user.infrastructure.persistence.mapper;

import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.infrastructure.persistence.entity.UserEntity;

public class UserMapper {

    public static User toDomain(UserEntity entity) {
        if (entity == null) return null;

        User user = new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getType()
        );

        user.setActive(entity.getActive()); // 🔥 importante pro soft delete
        return user;
    }

    public static UserEntity toEntity(User user) {
        if (user == null) return null;

        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setType(user.getType());
        entity.setActive(user.getActive()); // 🔥 importante

        return entity;
    }
}
