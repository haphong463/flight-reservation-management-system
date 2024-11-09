package com.windev.flight_service.service.cache;

import com.windev.flight_service.dto.CrewWithFlightsDTO;
import com.windev.flight_service.dto.FlightDetailDTO;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CrewCacheService {
    private static final String HASH_KEY = "CREW";

    @Value("${redis.time-to-leave}")
    private Long REDIS_TIME_TO_LEAVE;

    @Autowired
    private final RedisTemplate<Object, Object> redisTemplate;

    private final HashOperations<Object, String, CrewWithFlightsDTO> hashOperations;

    @Autowired
    public CrewCacheService(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void save(CrewWithFlightsDTO crew) {
        hashOperations.put(HASH_KEY, String.valueOf(crew.getId()), crew);
        redisTemplate.expire(HASH_KEY, REDIS_TIME_TO_LEAVE, TimeUnit.MILLISECONDS);
        log.info("save() --> Successfully saved to Redis.");
    }

    public Map<String, CrewWithFlightsDTO> findAll() {
        return hashOperations.entries(HASH_KEY);
    }

    public CrewWithFlightsDTO findById(String id) {
        log.info("findById() --> Successfully hit cache from Redis.");
        return hashOperations.get(HASH_KEY, id);
    }

    public void update(CrewWithFlightsDTO flight){
        save(flight);
        log.info("update() --> Successfully updated to Redis.");
    }

    public void delete(String id){
        hashOperations.delete(HASH_KEY, id);
        log.info("delete() --> Successfully deleted from Redis.");
    }
}
