package dekiru.ting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import dekiru.ting.entity.Program;

import java.util.List;

/**
 * The repository to manipulate the program entity.
 */
@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
    List<Program> findByCreatedBy(Long createdBy);
}
