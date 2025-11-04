package com.unicauca.fiet.evaluation.repo;

import com.unicauca.fiet.evaluation.domain.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EvaluationRepository extends JpaRepository<Evaluation, UUID> {
}
