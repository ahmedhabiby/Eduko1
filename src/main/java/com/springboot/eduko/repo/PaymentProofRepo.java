package com.springboot.eduko.repo;

import com.springboot.eduko.model.PaymentProof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentProofRepo extends JpaRepository<PaymentProof, Long> {

    /** All proofs ordered newest first. */
    List<PaymentProof> findAllByOrderByCreatedAtDesc();

    /** Proofs awaiting admin review. */
    List<PaymentProof> findByStatus(String status);

    /** Check for a duplicate pending proof. */
    boolean existsByStudentIdAndCourseIdAndStatus(Long studentId, Long courseId, String status);
}
