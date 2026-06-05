package com.learning.admin.repository;

import com.learning.admin.document.ErrorLogDO;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ErrorLogRepository extends MongoRepository<ErrorLogDO, String> {
}
