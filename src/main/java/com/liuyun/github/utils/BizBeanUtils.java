package com.liuyun.github.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class BizBeanUtils {

    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("入参不能为空");
        }
        BeanUtils.copyProperties(source, target);
    }

    public static <S, T> T copyProperties(S source, Class<T> clazz) {
        if (source == null || clazz == null) {
            throw new IllegalArgumentException("入参不能为空");
        }
        try {
            T target = clazz.newInstance();
            copyProperties(source, target);
            return target;
        } catch (Exception e) {
            log.error("Copy Bean Error!", e);
            return null;
        }
    }

    public static <S, T> List<T> copyProperties(List<S> source, Class<T> clazz) {
        if (CollectionUtils.isEmpty(source)) {
            return new ArrayList(0);
        }
        if (clazz == null) {
            throw new IllegalArgumentException("入参不能为空");
        }
        return source.stream().map(e -> copyProperties(e, clazz)).collect(Collectors.toList());
    }

    public static <S, T> List<T> copyList(List<S> sList, Class<T> clazz) {
        if (CollectionUtils.isEmpty(sList)) {
            return new ArrayList(0);
        }
        if (clazz == null) {
            throw new IllegalArgumentException("入参不能为空");
        }
        List<T> targetList = Lists.newArrayList();
        sList.forEach(source -> {
            try {
                T target = clazz.newInstance();
                copyProperties(source, target);
                targetList.add(target);
            } catch (Exception e) {
                log.error("copyList Error!", e);
            }
        });
        return targetList;
    }

    public static <S, T> T map(S source, Class<? extends Function<S, T>> clazz) {
        if (source == null) {
            return null;
        }
        if (clazz == null) {
            throw new IllegalArgumentException("入参不能为空");
        }
        try {
            return clazz.newInstance().apply(source);
        } catch (Exception e) {
            log.error("Mapping Bean Error!", e);
            return null;
        }
    }

    public static <S, T> List<T> map(List<S> sList, Class<? extends Function<S, T>> clazz) {
        if (CollectionUtils.isEmpty(sList)) {
            return new ArrayList(0);
        }
        if (clazz == null) {
            throw new IllegalArgumentException("入参不能为空");
        }
        try {
            Function<S, T> function = clazz.newInstance();
            return sList.stream().map(e -> function.apply(e)).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Mapping Bean Error!", e);
            return null;
        }
    }

    public static <S, T> List<T> map(List<S> sList, Function<S, T> function) {
        if (CollectionUtils.isEmpty(sList)) {
            return new ArrayList(0);
        }
        if (function == null) {
            throw new IllegalArgumentException("入参不能为空");
        }
        try {
            return sList.stream().map(e -> function.apply(e)).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Mapping Bean Error!", e);
            return null;
        }
    }

    public static <K, V> Map<K, V> listToMap(Function<V, K> func1, List<V> list) {
        if (func1 == null || list == null) {
            throw new IllegalArgumentException("入参不能为空");
        }
        Function func2 = Function.identity();
        return listToMap(func1, func2, list, HashMap.class);
    }

    public static <K, V> Map<K, V> listToMap(Function<V, K> func1, List<V> list, Class<? extends Map> clazz) {
        if (func1 == null || list == null) {
            throw new IllegalArgumentException("入参不能为空");
        }
        Function func2 = Function.identity();
        return listToMap(func1, func2, list, clazz);
    }

    public static <K, R, V> Map<K, V> listToMap(Function<R, K> func1, Function<R, V> func2, List<R> list, Class<? extends Map> clazz) {
        if (func1 == null || func2 == null || list == null) {
            throw new IllegalArgumentException("入参不能为空");
        }
        try {
            Map<K, V> map = clazz.newInstance();
            list.forEach(R -> map.put(func1.apply(R), func2.apply(R)));
            return map;
        } catch (Exception e) {
            log.error("listToMap Error!!!", e);
            return Collections.EMPTY_MAP;
        }
    }

    public static <K, V> Map<K, List<V>> listToListMap(Function<V, K> func1, List<V> list) {
        if (func1 == null || list == null) {
            throw new IllegalArgumentException("入参不能为空");
        }
        Function func2 = Function.identity();
        return listToListMap(func1, func2, list, HashMap.class);
    }

    public static <K, V> Map<K, List<V>> listToListMap(Function<V, K> func1, List<V> list, Class<? extends Map> clazz) {
        if (func1 == null || list == null) {
            throw new IllegalArgumentException("入参不能为空");
        }
        Function func2 = Function.identity();
        return listToListMap(func1, func2, list, clazz);
    }

    public static <K, R, V> Map<K, List<V>> listToListMap(Function<R, K> func1, Function<R, V> func2, List<R> list, Class<? extends Map> clazz) {
        if (func1 == null || list == null) {
            throw new IllegalArgumentException("入参不能为空");
        }
        try {
            Map<K, List<V>> map = clazz.newInstance();
            for (R r : list) {
                List<V> vList = map.get(func1.apply(r));
                if(vList == null) {
                    vList = Lists.newArrayList();
                }
                vList.add(func2.apply(r));
                map.put(func1.apply(r), vList);
            }
            return map;
        } catch (Exception e) {
            log.error("listToListMap Error!!!", e);
            return Collections.EMPTY_MAP;
        }
    }

    public static <K, V> Map<K, Set<V>> reverseMap(Map<V, Set<K>> sourceMap) {
        Map<K, Set<V>> targetMap = new TreeMap();
        for (Map.Entry<V, Set<K>> entry : sourceMap.entrySet()) {
            for (K k : entry.getValue()) {
                targetMap.computeIfAbsent(k, (i)-> Sets.newLinkedHashSet()).add(entry.getKey());
            }
        }
        return targetMap;
    }

    public static <T, R> List<R> distinctFieldsList(Function<T, R> function, List<T> list) {
        if (function == null || list == null) {
            throw new IllegalArgumentException("入参不能为空");
        }
        return list.stream().map(function).distinct().collect(Collectors.toList());
    }

    public static <T> List<T> distinctList(List<T> list, Function<? super T, Object>... funcList) {
        return list.stream().filter(distinctByKeys(funcList)).collect(Collectors.toList());
    }

    private static <T> Predicate<T> distinctByKeys(Function<? super T, Object>... funcList) {
        Map<Object, Boolean> map = new ConcurrentHashMap();
        return object -> {
            StringBuilder builder = new StringBuilder();
            for (Function function : funcList) {
                builder.append(function.apply(object));
            }
            return map.putIfAbsent(builder.toString(), Boolean.TRUE) == null;
        };
    }

    public static <E> List<List<E>> partition(List<E> list, List<Integer> numList) {
        List<List<E>> result = Lists.newArrayList();
        Iterator<E> iterator = list.iterator();
        for (Integer num : numList) {
            int index = 0;
            List<E> tempList = Lists.newArrayList();
            while (iterator.hasNext() && index < num) {
                tempList.add(iterator.next());
                iterator.remove();
                index++;
            }
            result.add(tempList);
        }
        return result;
    }

    public static <E, T extends Comparable> List<E> sorted(List<E> list, Function<E, T> function, boolean isAscend) {
        if(list == null || list.size() == 0) {
            return Lists.newArrayList();
        }
        Comparator<E> comparator = isAscend ? Comparator.comparing(function) : Comparator.comparing(function).reversed();
        return list.stream().sorted(comparator).collect(Collectors.toList());
    }

}
