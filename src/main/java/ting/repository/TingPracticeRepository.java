package ting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ting.entity.TingPractice;

@Repository
public interface TingPracticeRepository extends JpaRepository<TingPractice, Long> {
}
