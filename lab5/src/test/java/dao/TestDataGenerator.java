package dao;

import dto.UserDTO;
import dto.FunctionDTO;
import dto.OperationDTO;
import dto.CacheDTO;
import java.util.UUID;

public class TestDataGenerator {

    public static UserDTO generateTestUser() {
        UserDTO user = new UserDTO();
        user.setUsername("testuser_" + System.currentTimeMillis());
        user.setEmail("test" + System.currentTimeMillis() + "@example.com");
        user.setPasswordHash("hashed_password_123");
        return user;
    }

    public static FunctionDTO generateBasicFunction(UUID userId) {
        FunctionDTO function = new FunctionDTO();
        function.setUserId(userId);
        function.setName("Quadratic Function");
        function.setType("BASIC");
        function.setExpression("x^2");
        function.setLeftBound(-10.0);
        function.setRightBound(10.0);
        function.setPointsCount(100);
        function.setPointsData("{\"points\": [[-10,100], [-5,25], [0,0], [5,25], [10,100]]}");
        return function;
    }

    public static FunctionDTO generateSineFunction(UUID userId) {
        FunctionDTO function = new FunctionDTO();
        function.setUserId(userId);
        function.setName("Sine Wave");
        function.setType("BASIC");
        function.setExpression("sin(x)");
        function.setLeftBound(0.0);
        function.setRightBound(2 * Math.PI);
        function.setPointsCount(50);
        function.setPointsData("{\"points\": [[0,0], [1.57,1], [3.14,0], [4.71,-1], [6.28,0]]}");
        return function;
    }

    public static OperationDTO generateAddOperation(UUID userId, UUID func1Id, UUID func2Id, UUID resultId) {
        OperationDTO operation = new OperationDTO();
        operation.setUserId(userId);
        operation.setFunction1Id(func1Id);
        operation.setFunction2Id(func2Id);
        operation.setResultFunctionId(resultId);
        operation.setOperationType("ADD");
        operation.setParameters("{\"param1\": \"value1\"}");
        return operation;
    }

    public static CacheDTO generateCacheEntry(UUID userId, UUID resultId) {
        CacheDTO cache = new CacheDTO();
        cache.setCacheKey("cache_key_" + System.currentTimeMillis());
        cache.setUserId(userId);
        cache.setFunctionExpression("x^2 + sin(x)");
        cache.setLeftBound(-5.0);
        cache.setRightBound(5.0);
        cache.setPointsCount(50);
        cache.setResultFunctionId(resultId);
        cache.setAccessCount(1);
        return cache;
    }
}