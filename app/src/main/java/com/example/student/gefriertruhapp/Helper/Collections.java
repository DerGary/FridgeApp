package com.example.student.gefriertruhapp.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 22-10-17.
 */

public class Collections {
    public static <E> List<E> makeList(Iterable<E> iter) {
        if (iter == null){
            return null;
        }
        List<E> list = new ArrayList<E>();
        for (E item : iter) {
            list.add(item);
        }
        return list;
    }
    public static <E> boolean isEmpty(Iterable<E> iter) {
        if (iter == null){
            return true;
        }
        return !iter.iterator().hasNext();
    }
}
