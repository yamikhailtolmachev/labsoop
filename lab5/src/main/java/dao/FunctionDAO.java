package dao;

import dto.FunctionDTO;
import java.util.List;
import java.util.UUID;

public interface FunctionDAO {
    UUID insertFunction(FunctionDTO function);
    FunctionDTO findFunctionById(UUID id);
    List<FunctionDTO> findFunctionsByUserId(UUID userId);
    List<FunctionDTO> findFunctionsByType(String type);
    List<FunctionDTO> findAllFunctions();
    void updateFunction(FunctionDTO function);
    void deleteFunction(UUID id);

    List<FunctionDTO> findFunctionsByMultipleCriteria(UUID userId, String namePattern, String type, Double minLeftBound, Double maxRightBound, String sortBy, String sortOrder);
    List<FunctionDTO> findFunctionDerivatives(UUID rootFunctionId);
    List<FunctionDTO> findFunctionsByLevel(UUID userId, String period);
    List<FunctionDTO> findFunctionsWithHighestPointCount(int limit);
}