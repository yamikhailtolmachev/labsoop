package lab5.service;

import lab5.dto.ComputationCacheDTO;
import lab5.entity.ComputationCacheEntity;
import lab5.entity.FunctionEntity;
import lab5.entity.UserEntity;
import lab5.repository.ComputationCacheRepository;
import lab5.repository.FunctionRepository;
import lab5.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ComputationCacheService {

    private static final Logger logger = LoggerFactory.getLogger(ComputationCacheService.class);

    @Autowired
    private ComputationCacheRepository computationCacheRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Transactional
    public ComputationCacheEntity createComputationCache(ComputationCacheDTO cacheDTO) {
        try {
            logger.info("Создание кэша с ключом: {}", cacheDTO.getCacheKey());
            UserEntity user = userRepository.findById(cacheDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("Пользователь с ID " + cacheDTO.getUserId() + " не найден"));

            FunctionEntity resultFunction = functionRepository.findById(cacheDTO.getResultFunctionId())
                    .orElseThrow(() -> new RuntimeException("Функция с ID " + cacheDTO.getResultFunctionId() + " не найдена"));

            if (computationCacheRepository.existsByCacheKey(cacheDTO.getCacheKey())) {
                throw new RuntimeException("Ключ кэша '" + cacheDTO.getCacheKey() + "' уже существует");
            }

            ComputationCacheEntity cache = new ComputationCacheEntity();
            cache.setCacheKey(cacheDTO.getCacheKey());
            cache.setUser(user);
            cache.setFunctionExpression(cacheDTO.getFunctionExpression());
            cache.setLeftBound(cacheDTO.getLeftBound());
            cache.setRightBound(cacheDTO.getRightBound());
            cache.setPointsCount(cacheDTO.getPointsCount());
            cache.setResultFunction(resultFunction);
            cache.setAccessCount(1);

            ComputationCacheEntity savedCache = computationCacheRepository.save(cache);
            logger.debug("Кэш сохранен с ID: {}", savedCache.getId());
            return savedCache;

        } catch (Exception e) {
            logger.error("Ошибка при создании кэша: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при создании кэша: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<ComputationCacheEntity> getComputationCacheById(Long id) {
        logger.debug("Поиск записи кэша по ID: {}", id);
        Optional<ComputationCacheEntity> cacheOpt = computationCacheRepository.findById(id);
        return cacheOpt;
    }

    @Transactional(readOnly = true)
    public List<ComputationCacheEntity> getAllComputationCaches(String sortField, String sortDirection) {
        logger.debug("Получение всех записей кэша с сортировкой: {}:{}", sortField, sortDirection);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return computationCacheRepository.findAll(sort);
    }

    @Transactional(readOnly = true)
    public Optional<ComputationCacheEntity> getComputationCacheByKey(String cacheKey) {
        logger.debug("Поиск записи кэша по ключу: {}", cacheKey);
        Optional<ComputationCacheEntity> cacheOpt = computationCacheRepository.findByCacheKey(cacheKey);
        return cacheOpt;
    }

    @Transactional
    public void incrementAccessCount(Long id) {
        logger.debug("Обновление счётчика доступа для кэша ID: {}", id);
        Optional<ComputationCacheEntity> cacheOpt = computationCacheRepository.findById(id);
        if (cacheOpt.isPresent()) {
            ComputationCacheEntity cache = cacheOpt.get();
            cache.setAccessCount(cache.getAccessCount() + 1);
            cache.setUpdatedAt(LocalDateTime.now());
            computationCacheRepository.save(cache);
            logger.debug("Счётчик доступа для кэша ID {} обновлён.", id);
        } else {
            logger.warn("Не удалось обновить счётчик: кэш с ID {} не найден.", id);
        }
    }

    @Transactional
    public boolean deleteComputationCacheById(Long id) {
        logger.info("Удаление кэша с ID: {}", id);
        if (computationCacheRepository.existsById(id)) {
            computationCacheRepository.deleteById(id);
            logger.debug("Кэш с ID {} удалён.", id);
            return true;
        } else {
            logger.warn("Кэш с ID {} не найден для удаления.", id);
            return false;
        }
    }
}