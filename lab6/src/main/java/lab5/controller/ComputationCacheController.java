package lab5.controller;

import lab5.entity.ComputationCacheEntity;
import lab5.service.SearchService;
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
    private SearchService searchService;

    @GetMapping("/{id}")
    public ResponseEntity<ComputationCacheEntity> getCacheById(@PathVariable Long id) {
        logger.info("Получен запрос на получение записи кэша с ID: {}", id);
        Optional<ComputationCacheEntity> cacheOpt = searchService.findComputationCacheById(id);
        if (cacheOpt.isPresent()) {
            logger.debug("Возвращена запись кэша с ID: {}", id);
            return new ResponseEntity<>(cacheOpt.get(), HttpStatus.OK);
        } else {
            logger.warn("Запись кэша с ID {} не найдена.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<ComputationCacheEntity>> getAllCaches(
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        logger.info("Получен запрос на получение всех записей кэша, сортировка: {}:{}", sortField, sortDirection);
        List<ComputationCacheEntity> caches = searchService.findAllComputationCachesSorted(sortField, sortDirection);
        logger.debug("Возвращено {} записей кэша.", caches.size());
        return new ResponseEntity<>(caches, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ComputationCacheEntity> createCache(@RequestBody ComputationCacheEntity cache) {
        logger.info("Получен запрос на создание записи кэша с ключом: {}", cache.getCacheKey());
        ComputationCacheEntity createdCache = searchService.createComputationCache(cache);
        logger.info("Запись кэша создана с ID: {}", createdCache.getId());
        return new ResponseEntity<>(createdCache, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComputationCacheEntity> updateCache(@PathVariable Long id, @RequestBody ComputationCacheEntity cacheDetails) {
        logger.info("Получен запрос на обновление записи кэша с ID: {}", id);
        ComputationCacheEntity updatedCache = searchService.updateComputationCache(id, cacheDetails);
        if (updatedCache != null) {
            logger.info("Запись кэша с ID {} обновлена.", id);
            return new ResponseEntity<>(updatedCache, HttpStatus.OK);
        } else {
            logger.warn("Запись кэша с ID {} не найдена для обновления.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCache(@PathVariable Long id) {
        logger.info("Получен запрос на удаление записи кэша с ID: {}", id);
        boolean deleted = searchService.deleteComputationCacheById(id);
        if (deleted) {
            logger.info("Запись кэша с ID {} удалена.", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            logger.warn("Запись кэша с ID {} не найдена для удаления.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/key/{cacheKey}")
    public ResponseEntity<ComputationCacheEntity> getCacheByCacheKey(@PathVariable String cacheKey) {
        logger.info("Получен запрос на поиск записи кэша по ключу: {}", cacheKey);
        Optional<ComputationCacheEntity> cacheOpt = searchService.findComputationCacheByCacheKey(cacheKey);
        if (cacheOpt.isPresent()) {
            logger.debug("Найдена запись кэша по ключу: {}", cacheKey);
            return new ResponseEntity<>(cacheOpt.get(), HttpStatus.OK);
        } else {
            logger.warn("Запись кэша с ключом {} не найдена.", cacheKey);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}