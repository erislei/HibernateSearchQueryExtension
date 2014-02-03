/*  
 * This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.*
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *   (C) Martin Braun 2014
 */
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
