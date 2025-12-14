package lab5.service;

import lab5.dto.ComputationCacheDTO;
import lab5.entity.ComputationCacheEntity;
import lab5.entity.FunctionEntity;
import lab5.entity.UserEntity;
import lab5.repository.ComputationCacheRepository;
import lab5.repository.FunctionRepository;
import lab5.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ComputationCacheService {

    @Autowired
    private ComputationCacheRepository computationCacheRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Transactional
    public ComputationCacheEntity createComputationCache(ComputationCacheDTO cacheDTO) {
        try {
            UserEntity user = userRepository.findById(cacheDTO.getUserId()).orElseThrow(() -> new RuntimeException("Пользователь с ID " + cacheDTO.getUserId() + " не найден"));

            FunctionEntity resultFunction = functionRepository.findById(cacheDTO.getResultFunctionId()).orElseThrow(() -> new RuntimeException("Функция с ID " + cacheDTO.getResultFunctionId() + " не найдена"));

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

            return computationCacheRepository.save(cache);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании кэша: " + e.getMessage(), e);
        }
    }

    public Optional<ComputationCacheEntity> getCacheById(Long id) {
        Optional<ComputationCacheEntity> cache = computationCacheRepository.findById(id);
        cache.ifPresent(c -> {
            c.setAccessCount(c.getAccessCount() + 1);
            c.setUpdatedAt(LocalDateTime.now());
            computationCacheRepository.save(c);
        });
        return cache;
    }

    public List<ComputationCacheEntity> getAllCaches(String sortField, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return computationCacheRepository.findAll(sort);
    }

    public Optional<ComputationCacheEntity> getCacheByKey(String cacheKey) {
        Optional<ComputationCacheEntity> cache = computationCacheRepository.findByCacheKey(cacheKey);
        cache.ifPresent(c -> {
            c.setAccessCount(c.getAccessCount() + 1);
            c.setUpdatedAt(LocalDateTime.now());
            computationCacheRepository.save(c);
        });
        return cache;
    }

    @Transactional
    public boolean deleteCache(Long id) {
        if (computationCacheRepository.existsById(id)) {
            computationCacheRepository.deleteById(id);
            return true;
        }
        return false;
    }
}