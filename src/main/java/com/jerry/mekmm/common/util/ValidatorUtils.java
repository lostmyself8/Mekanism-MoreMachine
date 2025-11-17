package com.jerry.mekmm.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class ValidatorUtils {

    // 正则表达式规则：命名空间和物品名由小写字母、数字、下划线组成，数字部分为整数
    public static final String FORMAT_REGEX = "^([a-z0-9_]+:[a-z0-9_]+)#(\\d+)$";
    public static final Pattern PATTERN = Pattern.compile(FORMAT_REGEX);

    public static boolean validateList(String list) {
        if (list == null) return false; // 处理空列表或null列表
        return PATTERN.matcher(list).matches(); // 任意元素不匹配则返回false
    }

    public static HashMap<String, Integer> getRecipeFromConfig(List<?> config) {
        HashMap<String, Integer> map = new HashMap<>();
        List<String> recipes = new ArrayList<>();
        for (Object item : config) {
            if (item instanceof String list) {
                recipes.add(list);
            }
        }
        if (recipes.isEmpty()) return null;
        for (String element : recipes) {
            String[] parts = element.split("#", 2); // 分割成最多两部分
            if (parts.length != 2) continue;

            String key = parts[0];
            int value = Integer.parseInt(parts[1]);
            map.put(key, value);
        }
        return map;
    }
}
