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
}