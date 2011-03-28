package com.heychinaski.findbus.service;

import java.lang.ref.*;
import java.util.*;

/**
 * @author pestrella
 */
public class ObjectCache {
  private static Map<Integer, WeakReference<Object>> cache = new HashMap<Integer, WeakReference<Object>>();

  public static void put(int id, Object obj) {
    cache.put(id, new WeakReference<Object>(obj));
  }

  public static Object get(int id) {
    WeakReference<Object> weakReference = cache.get(id);
    /* I was thinking about memory here... should I remove the object? e.g. cache.remove(id); */
    return weakReference.get();
  }
}