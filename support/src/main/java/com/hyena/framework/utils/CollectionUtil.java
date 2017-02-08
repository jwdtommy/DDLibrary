package com.hyena.framework.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 集合对象工具类
 * @author yangzc
 */
public class CollectionUtil {

	/**
	 * 检查集合元素是否为空
	 * 
	 * @param collection
	 *            集合
	 * @return
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();

	}

	/**
	 * 检查Map元素是否为空
	 * 
	 * @param map
	 *            Map
	 * @return
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();

	}

	/**
	 * 数组合并
	 * @param toList
	 * @param fromList
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<?> merge(List<?> toList, List<?> fromList) {

		if (toList != null && fromList == null) {
			return toList;
		}

		if (toList == null && fromList != null) {
			return fromList;
		}

		if (toList != null && fromList != null) {
			List mergedList = new ArrayList();
			mergedList.addAll(toList);
			mergedList.removeAll(fromList);
			mergedList.addAll(toList);
			return mergedList;
		}
		return null;
	}
}
