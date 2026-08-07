package com.java.erp.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory token blacklist for JWT invalidation on logout.
 * Stores blacklisted tokens until they would have naturally expired.
 * A periodic cleanup removes expired entries every 15 minutes.
 *
 * Note: This is suitable for single-instance deployments.
 * For multi-instance deployments, consider a Redis-backed implementation.
 */
@Component
public class TokenBlacklistService {

    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);

    /**
     * Map of blacklisted token → expiry timestamp.
     * Tokens are removed after their natural expiry time has passed.
     */
    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    /**
     * Add a token to the blacklist.
     *
     * @param token      the JWT token to blacklist
     * @param expiresAt  when the token would naturally expire
     */
    public void blacklist(String token, Instant expiresAt) {
        blacklist.put(token, expiresAt);
        logger.debug("Token blacklisted, expires at {}", expiresAt);
    }

    /**
     * Check if a token is blacklisted.
     *
     * @param token the JWT token to check
     * @return true if the token is blacklisted
     */
    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }

    /**
     * Periodically clean up expired tokens from the blacklist.
     * Runs every 15 minutes.
     */
    @Scheduled(fixedRate = 900_000)
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        int before = blacklist.size();
        blacklist.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
        int removed = before - blacklist.size();
        if (removed > 0) {
            logger.info("Cleaned up {} expired tokens from blacklist", removed);
        }
    }

    /**
     * Get the current size of the blacklist (for monitoring).
     */
    public int size() {
        return blacklist.size();
    }
}
