package dekiru.ting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import dekiru.ting.entity.TingTag;

import java.util.List;

/**
 * The repository to manipulate the ting tag entity.
 */
@Repository
public interface TingTagRepository extends JpaRepository<TingTag, Long> {
    List<TingTag> findByTingIdIn(List<Long> tingIds);
}
