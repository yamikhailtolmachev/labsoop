package dao;

import dto.UserDTO;
import dto.FunctionDTO;
import dto.OperationDTO;
import dto.CacheDTO;
import java.util.UUID;

public class TestDataGenerator {

    public static UserDTO generateTestUser() {
        UserDTO user = new UserDTO();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashed_password");
        return user;
    }

    public static FunctionDTO generateBasicFunction(UUID userId) {
        FunctionDTO function = new FunctionDTO();
        function.setUserId(userId);
        function.setName("Quadratic Function");
        function.setType("BASIC");
        function.setExpression("x^2");
        return function;
    }

    public static OperationDTO generateAddOperation(UUID userId, UUID func1Id, UUID func2Id, UUID resultId) {
        OperationDTO operation = new OperationDTO();
        operation.setUserId(userId);
        operation.setFunction1Id(func1Id);
        operation.setFunction2Id(func2Id);
        operation.setResultFunctionId(resultId);
        operation.setOperationType("ADD");
        return operation;
    }

    public static CacheDTO generateCacheEntry(UUID userId, UUID resultId) {
        CacheDTO cache = new CacheDTO();
        cache.setCacheKey("cache_key");
        cache.setUserId(userId);
        cache.setFunctionExpression("x^2");
        cache.setLeftBound(-5.0);
        cache.setRightBound(5.0);
        cache.setPointsCount(50);
        cache.setResultFunctionId(resultId);
        return cache;
    }
}