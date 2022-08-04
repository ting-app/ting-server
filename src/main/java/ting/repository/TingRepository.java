package ting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ting.entity.Ting;

import java.util.List;

public interface TingRepository extends JpaRepository<Ting, Long> {
    List<Ting> findByProgramId(long programId);
}
