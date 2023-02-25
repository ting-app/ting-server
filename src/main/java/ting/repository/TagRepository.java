package ting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ting.entity.Tag;

/**
 * The repository to manipulate the tag entity.
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
}
