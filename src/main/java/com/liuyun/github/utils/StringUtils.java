package com.liuyun.github.utils;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class StringUtils {

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isBlank(CharSequence cs) {
        if(cs == null || cs.length() == 0) {
            return true;
        }
        for(int i = 0; i < cs.length(); ++i) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static <E> String joinToString(String separator, E... array) {
        return joinToString((e) -> String.valueOf(e), separator, array);
    }

    public static <E> String joinToString(String separator, Collection<E> collection) {
        return joinToString((e) -> String.valueOf(e), separator, collection);
    }

    public static <E> String joinToString(Function<E, String> function, String separator, E[] array) {
        return joinToString(function, separator, Lists.newArrayList(array));
    }

    public static <E> String joinToString(Function<E, String> function, String separator, Collection<E> collection) {
        if(collection == null) { return null; }
        StringBuilder sb = new StringBuilder();
        for (E e : collection) {
            if(e == null) { continue; }
            sb.append(function.apply(e)).append(separator);
        }
        if(sb.length() > separator.length()) {
            sb.delete(sb.length() - separator.length(), sb.length());
        }
        return sb.toString();
    }

    public static List<String> wrap(List<String> words, String tag) {
        return wrap(words, tag, tag);
    }

    public static List<String> wrap(List<String> words, String left, String right) {
        List<String> retList = Lists.newArrayList();
        for (String word : words) {
            if(isBlank(word)) {
                continue;
            }
            retList.add(wrap(word, left, right));
        }
        return retList;
    }

    public static String wrap(String word, String tag) {
        return wrap(word, tag, tag);
    }

    public static String wrap(String word, String left, String right) {
        return left + word + right;
    }

}
