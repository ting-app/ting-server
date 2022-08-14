package ting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ting.entity.Program;

/**
 * The repository to manipulate the program entity.
 */
@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
}
