package com.clinicalflow.lab.repository;

import com.clinicalflow.lab.model.LabResult;
import com.clinicalflow.lab.model.ResultFlag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LabResultRepository extends JpaRepository<LabResult, UUID> {

    List<LabResult> findByPatientIdOrderByCollectedAtDesc(UUID patientId);

    List<LabResult> findByPatientIdAndTestNameOrderByCollectedAtDesc(UUID patientId, String testName);

    List<LabResult> findByFlag(ResultFlag flag);
}
