package com.liuyun.github.function;

import com.google.common.base.Function;

/**
 * @Author: liuyun18
 * @Date: 2018/10/24 下午2:23
 */
public abstract class SafeFunction<InType, OutType> implements Function<InType, OutType> {

    @Override
    public final OutType apply(InType input) {
        return input != null ? this.safeApply(input) : null;
    }

    protected abstract OutType safeApply(InType var1);

}
