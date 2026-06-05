package com.learning.admin.repository;

import com.learning.admin.document.AuditLogDO;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditLogRepository extends MongoRepository<AuditLogDO, String> {
}
