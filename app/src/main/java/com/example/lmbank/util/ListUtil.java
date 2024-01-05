package com.example.lmbank.util;

import java.util.List;

public class ListUtil {
    public static String[] toArray(List<String> strings) {
        String[] strings1 = new String[strings.size()];
        for (int i = 0; i < strings.size(); i++) {
            strings1[i] = strings.get(i);
        }
        return strings1;
    }
}
