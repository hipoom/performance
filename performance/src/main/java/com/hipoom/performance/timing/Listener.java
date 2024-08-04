package com.hipoom.performance.timing;

import androidx.annotation.NonNull;

/**
 * @author ZhengHaiPeng
 * @since 2024/8/4 15:38
 */
public interface Listener {
    void onFramePop(int depth, @NonNull Frame frame);
}
