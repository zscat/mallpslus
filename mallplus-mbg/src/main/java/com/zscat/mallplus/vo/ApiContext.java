package com.zscat.mallplus.vo;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ApiContext {


    private static final String KEY_CURRENT_PROVIDER_ID = "KEY_CURRENT_PROVIDER_ID";
    private static final Map<String, Object> mContext = Maps.newConcurrentMap();

    public Long getCurrentProviderId() {
        return (Long) mContext.get(KEY_CURRENT_PROVIDER_ID);
    }

    public void setCurrentProviderId(Long providerId) {
        mContext.put(KEY_CURRENT_PROVIDER_ID, providerId);
    }
}