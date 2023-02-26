package ting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ting.entity.Tag;

import java.util.List;

/**
 * The repository to manipulate the tag entity.
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByIdIn(List<Long> ids);
}
