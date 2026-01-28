package com.maria.finance.user.infrastructure.persistence.mapper;


import com.maria.finance.user.domain.model.User;
import com.maria.finance.user.infrastructure.persistence.entity.UserEntity;

public class UserMapper {

    public static User toDomain(UserEntity e) {
        return new User(
                e.getId(),
                e.getName(),
                e.getEmail(),
                e.getPassword(),
                e.getType()
        );
    }

    public static UserEntity toEntity(User u) {
        UserEntity entity = new UserEntity();
        entity.setId(u.getId()); // null para novo, preenchido para update
        entity.setName(u.getName());
        entity.setEmail(u.getEmail());
        entity.setPassword(u.getPassword());
        entity.setType(u.getType());
        return entity;
    }


}


