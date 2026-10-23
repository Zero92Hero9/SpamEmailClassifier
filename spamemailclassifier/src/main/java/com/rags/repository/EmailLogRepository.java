package com.rags.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.rags.entity.EmailLog;

@Repository 
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {

}
