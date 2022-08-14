package ting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ting.entity.User;

/**
 * The repository to manipulate the user entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByName(String name);

    User findByEmail(String email);

    User findByNameOrEmail(String name, String email);
}
