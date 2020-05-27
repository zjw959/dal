package com.jarvis.cache.to;

import java.util.Map;

public abstract interface HashRedisObject {
    Map<String, String> toHash();
}
