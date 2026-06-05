package com.learning.admin.repository;

import com.learning.admin.document.OperationLogDO;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OperationLogRepository extends MongoRepository<OperationLogDO, String> {
}
