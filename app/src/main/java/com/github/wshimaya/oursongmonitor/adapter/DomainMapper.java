package com.github.wshimaya.oursongmonitor.adapter;

import io.micrometer.common.lang.NonNull;

@FunctionalInterface
public interface DomainMapper<I, O> {

  O map(@NonNull final I source);
}
