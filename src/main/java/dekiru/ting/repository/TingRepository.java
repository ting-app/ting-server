package dekiru.ting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import dekiru.ting.entity.Ting;

import java.util.List;

/**
 * The repository to manipulate the ting entity.
 */
@Repository
public interface TingRepository extends JpaRepository<Ting, Long> {
    List<Ting> findByProgramIdOrderByUpdatedAtDesc(long programId);

    void deleteByProgramId(long programId);

    List<Ting> findByIdIn(List<Long> ids);

    long countByProgramId(long programId);
}
