package de.hotware.hibernate.query.intelligent.searcher;

import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.beanutils.WrapDynaClass;

public final class Util {

	private static final Pattern SPLIT_PATTERN = Pattern.compile("\\.");

	private Util() {
		throw new AssertionError("can't touch this");
	}

	public static Object getProperty(
			Map<Class<?>, WrapDynaClass> wrapDynaClassCache, Object bean,
			String property) {
		Object curObject = bean;
		if (property != null && !property.equals("")) {
			for (String split : SPLIT_PATTERN.split(property)) {
				WrapDynaClass clazz = wrapDynaClassCache.get(curObject
						.getClass());
				if (clazz == null) {
					clazz = WrapDynaClass.createDynaClass(curObject.getClass());
					wrapDynaClassCache.put(curObject.getClass(), clazz);
				}
				WrapDynaBean dynaBean = new WrapDynaBean(curObject, clazz);
				curObject = dynaBean.get(split);
			}
		}
		return curObject;
	}

}
