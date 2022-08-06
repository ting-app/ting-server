package ting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ting.entity.Program;

import java.util.List;

public interface ProgramRepository extends JpaRepository<Program, Long> {
    List<Program> findByLanguage(int language);
}
