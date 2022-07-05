package ting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ting.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByNameExists(String name);
}
