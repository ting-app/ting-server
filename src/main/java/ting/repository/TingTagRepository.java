package ting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ting.entity.TingTag;

/**
 * The repository to manipulate the ting tag entity.
 */
@Repository
public interface TingTagRepository extends JpaRepository<TingTag, Long> {
}
