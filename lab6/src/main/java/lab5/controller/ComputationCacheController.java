package lab5.controller;

import lab5.dto.ComputationCacheDTO;
import lab5.entity.ComputationCacheEntity;
import lab5.service.ComputationCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ComputationCacheEntity> getCacheById(@PathVariable Long id) {
        logger.info("Получен запрос на получение записи кэша с ID: {}", id);
        Optional<ComputationCacheEntity> cacheOpt = computationCacheService.getCacheById(id);
        if (cacheOpt.isPresent()) {
            logger.debug("Возвращена запись кэша с ID: {}", id);
            return ResponseEntity.ok(cacheOpt.get());
        } else {
            logger.warn("Запись кэша с ID {} не найдена.", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ComputationCacheEntity>> getAllCaches(
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        logger.info("Получен запрос на получение всех записей кэша, сортировка: {}:{}", sortField, sortDirection);
        List<ComputationCacheEntity> caches = computationCacheService.getAllCaches(sortField, sortDirection);
        logger.debug("Возвращено {} записей кэша.", caches.size());
        return ResponseEntity.ok(caches);
    }

    @PostMapping
    public ResponseEntity<ComputationCacheEntity> createCache(@RequestBody ComputationCacheDTO cacheDTO) {
        logger.info("Получен запрос на создание записи кэша с ключом: {}", cacheDTO.getCacheKey());
        try {
            ComputationCacheEntity createdCache = computationCacheService.createComputationCache(cacheDTO);
            logger.info("Запись кэша создана с ID: {}", createdCache.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCache);
        } catch (Exception e) {
            logger.error("Ошибка при создании записи кэша: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComputationCacheEntity> updateCache(@PathVariable Long id, @RequestBody ComputationCacheEntity cacheDetails) {
        logger.info("Получен запрос на обновление записи кэша с ID: {}", id);
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCache(@PathVariable Long id) {
        logger.info("Получен запрос на удаление записи кэша с ID: {}", id);
        boolean deleted = computationCacheService.deleteCache(id);
        if (deleted) {
            logger.info("Запись кэша с ID {} удалена.", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("Запись кэша с ID {} не найдена для удаления.", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/key/{cacheKey}")
    public ResponseEntity<ComputationCacheEntity> getCacheByCacheKey(@PathVariable String cacheKey) {
        logger.info("Получен запрос на поиск записи кэша по ключу: {}", cacheKey);
        Optional<ComputationCacheEntity> cacheOpt = computationCacheService.getCacheByKey(cacheKey);
        if (cacheOpt.isPresent()) {
            logger.debug("Найдена запись кэша по ключу: {}", cacheKey);
            return ResponseEntity.ok(cacheOpt.get());
        } else {
            logger.warn("Запись кэша с ключом {} не найдена.", cacheKey);
            return ResponseEntity.notFound().build();
        }
    }
}