package dekiru.ting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import dekiru.ting.entity.TingPractice;

/**
 * The repository to manipulate the ting practice entity.
 */
@Repository
public interface TingPracticeRepository extends JpaRepository<TingPractice, Long> {
}
