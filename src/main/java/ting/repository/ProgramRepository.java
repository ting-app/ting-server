package ting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ting.entity.Program;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
}
