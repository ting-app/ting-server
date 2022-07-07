package ting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ting.entity.Program;

public interface ProgramRepository extends JpaRepository<Program, Long> {
}
