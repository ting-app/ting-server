package ting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ting.entity.Ting;

public interface TingRepository extends JpaRepository<Ting, Long> {
}
