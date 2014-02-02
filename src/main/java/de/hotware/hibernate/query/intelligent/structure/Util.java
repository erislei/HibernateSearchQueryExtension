package de.hotware.hibernate.query.intelligent.structure;

import java.util.regex.Pattern;

import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.beanutils.WrapDynaClass;

public final class Util {

	private static final Pattern SPLIT_PATTERN = Pattern.compile("\\.");

	private Util() {
		throw new AssertionError("can't touch this");
	}

	public static Object getProperty(
			CachedInfo cachedInfo, Object bean,
			String property) {
		Object curObject = bean;
		if (property != null && !property.equals("")) {
			for (String split : SPLIT_PATTERN.split(property)) {
				WrapDynaClass clazz = cachedInfo.getCachedClass(curObject
						.getClass());
				WrapDynaBean dynaBean = new WrapDynaBean(curObject, clazz);
				curObject = dynaBean.get(split);
			}
		}
		return curObject;
	}

}
