package ting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ting.entity.Ting;

import java.util.List;

@Repository
public interface TingRepository extends JpaRepository<Ting, Long> {
    List<Ting> findByProgramId(long programId);

    void deleteByProgramId(long programId);
}
