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

    List<OperationDTO> findOperationsByMultipleCriteria(UUID userId, String operationType, UUID functionId, String sortBy, String sortOrder);
    List<OperationDTO> findOperationChainDepthFirst(UUID functionId);
    List<OperationDTO> findRecentOperations(UUID userId, int days);
    List<OperationDTO> findOperationsByFunctionHierarchy(UUID rootFunctionId);
}