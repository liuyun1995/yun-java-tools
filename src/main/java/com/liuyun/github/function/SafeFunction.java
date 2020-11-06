package com.liuyun.github.function;

import java.util.function.Function;

public abstract class SafeFunction<InType, OutType> implements Function<InType, OutType> {

    @Override
    public final OutType apply(InType input) {
        return input != null ? this.safeApply(input) : null;
    }

    protected abstract OutType safeApply(InType var1);

}
