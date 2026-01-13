package lab5.controller;

import lab5.dto.CacheResponseDTO;
import lab5.entity.ComputationCacheEntity;
import lab5.service.ComputationCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cache")
public class ComputationCacheController {

    private static final Logger logger = LoggerFactory.getLogger(ComputationCacheController.class);

    @Autowired
    private ComputationCacheService computationCacheService;

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<CacheResponseDTO> getCacheById(@PathVariable Long id) {
        logger.info("Получен запрос на получение записи кэша с ID: {}", id);
        try {
            Optional<ComputationCacheEntity> cacheOpt = computationCacheService.getComputationCacheById(id);
            if (cacheOpt.isPresent()) {
                computationCacheService.incrementAccessCount(id);
                CacheResponseDTO responseDto = CacheResponseDTO.fromEntity(cacheOpt.get());
                logger.debug("Найдена запись кэша с ID: {}", id);
                return ResponseEntity.ok(responseDto);
            } else {
                logger.warn("Запись кэша с ID {} не найдена.", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Ошибка при получении кэша по ID: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/key/{cacheKey}")
    @Transactional(readOnly = true)
    public ResponseEntity<CacheResponseDTO> getCacheByKey(@PathVariable String cacheKey) {
        logger.info("Получен запрос на получение записи кэша по ключу: {}", cacheKey);
        try {
            Optional<ComputationCacheEntity> cacheOpt = computationCacheService.getComputationCacheByKey(cacheKey);
            if (cacheOpt.isPresent()) {
                computationCacheService.incrementAccessCount(cacheOpt.get().getId());
                CacheResponseDTO responseDto = CacheResponseDTO.fromEntity(cacheOpt.get());
                logger.debug("Найдена запись кэша по ключу: {}", cacheKey);
                return ResponseEntity.ok(responseDto);
            } else {
                logger.warn("Запись кэша с ключом '{}' не найдена.", cacheKey);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Ошибка при получении кэша по ключу: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<CacheResponseDTO>> getAllCaches(
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        logger.info("Получен запрос на получение всех записей кэша, сортировка: {}:{}", sortField, sortDirection);
        try {
            List<ComputationCacheEntity> caches = computationCacheService.getAllComputationCaches(sortField, sortDirection);
            List<CacheResponseDTO> responseDtos = caches.stream()
                    .map(CacheResponseDTO::fromEntity)
                    .collect(java.util.stream.Collectors.toList());
            logger.debug("Возвращено {} записей кэша.", responseDtos.size());
            return ResponseEntity.ok(responseDtos);
        } catch (Exception e) {
            logger.error("Ошибка при получении всех кэшей: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping
    public ResponseEntity<CacheResponseDTO> createCache(@RequestBody lab5.dto.ComputationCacheDTO cacheDTO) {
        logger.info("Получен запрос на создание записи кэша DTO");
        try {
            ComputationCacheEntity createdCache = computationCacheService.createComputationCache(cacheDTO);
            if (createdCache != null) {
                CacheResponseDTO responseDto = CacheResponseDTO.fromEntity(createdCache);
                logger.debug("Запись кэша Entity создана с ID: {}", createdCache.getId());
                return ResponseEntity.status(201).body(responseDto);
            } else {
                logger.error("Ошибка при создании кэша DTO: возвращено null");
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            logger.error("Ошибка при создании кэша DTO: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCache(@PathVariable Long id) {
        logger.info("Получен запрос на удаление записи кэша с ID: {}", id);
        boolean deleted = computationCacheService.deleteComputationCacheById(id);
        if (deleted) {
            logger.info("Запись кэша с ID {} удалена.", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("Запись кэша с ID {} не найдена для удаления.", id);
            return ResponseEntity.notFound().build();
        }
    }
}