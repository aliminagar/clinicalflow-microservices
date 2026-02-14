package com.clinicalflow.patient.config;

import com.clinicalflow.patient.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PatientService.PatientNotFoundException.class)
    public ProblemDetail handleNotFound(PatientService.PatientNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create("https://clinicalflow.com/errors/patient-not-found"));
        problem.setTitle("Patient Not Found");
        return problem;
    }

    @ExceptionHandler(PatientService.DuplicateMrnException.class)
    public ProblemDetail handleDuplicateMrn(PatientService.DuplicateMrnException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setType(URI.create("https://clinicalflow.com/errors/duplicate-mrn"));
        problem.setTitle("Duplicate MRN");
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errors);
        problem.setType(URI.create("https://clinicalflow.com/errors/validation-failed"));
        problem.setTitle("Validation Failed");
        return problem;
    }
}
