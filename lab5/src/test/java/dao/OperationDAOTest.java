package dao;

import dto.UserDTO;
import dto.FunctionDTO;
import dto.OperationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class OperationDAOTest {
    private UserDAO userDAO = new UserDAOImpl();
    private FunctionDAO functionDAO = new FunctionDAOImpl();
    private OperationDAO operationDAO = new OperationDAOImpl();
    private UUID testUserId;
    private UUID testFunctionId;

    @BeforeEach
    void setUp() {
        UserDTO user = TestDataGenerator.generateTestUser();
        testUserId = userDAO.insertUser(user);

        FunctionDTO function = TestDataGenerator.generateBasicFunction(testUserId);
        testFunctionId = functionDAO.insertFunction(function);
    }

    @Test
    void testInsertAndFindOperation() {
        OperationDTO operation = TestDataGenerator.generateAddOperation(testUserId, testFunctionId, testFunctionId, testFunctionId);
        UUID operationId = operationDAO.insertOperation(operation);

        OperationDTO found = operationDAO.findOperationById(operationId);
        assertNotNull(found);
        assertEquals("ADD", found.getOperationType());
    }

    @Test
    void testFindOperationsByUser() {
        OperationDTO operation = TestDataGenerator.generateAddOperation(testUserId, testFunctionId, testFunctionId, testFunctionId);
        operationDAO.insertOperation(operation);

        List<OperationDTO> operations = operationDAO.findOperationsByUserId(testUserId);
        assertFalse(operations.isEmpty());
    }

    @Test
    void testFindOperationsByType() {
        OperationDTO operation = TestDataGenerator.generateAddOperation(testUserId, testFunctionId, testFunctionId, testFunctionId);
        operationDAO.insertOperation(operation);

        List<OperationDTO> operations = operationDAO.findOperationsByType("ADD");
        assertFalse(operations.isEmpty());
    }

    @Test
    void testDeleteOperation() {
        OperationDTO operation = TestDataGenerator.generateAddOperation(testUserId, testFunctionId, testFunctionId, testFunctionId);
        UUID operationId = operationDAO.insertOperation(operation);

        operationDAO.deleteOperation(operationId);

        OperationDTO deleted = operationDAO.findOperationById(operationId);
        assertNull(deleted);
    }
}