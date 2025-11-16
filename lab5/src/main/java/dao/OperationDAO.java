package dao;

import dto.OperationDTO;
import java.util.List;
import java.util.UUID;

public interface OperationDAO {
    UUID insertOperation(OperationDTO operation);
    OperationDTO findOperationById(UUID id);
    List<OperationDTO> findOperationsByUserId(UUID userId);
    List<OperationDTO> findOperationsByType(String operationType);
    List<OperationDTO> findAllOperations();
    void deleteOperation(UUID id);
}