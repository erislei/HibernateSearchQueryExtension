package de.hotware.hiberante.query.stringbridge;

import org.hibernate.search.bridge.StringBridge;


public class ToStringStringBridge implements StringBridge {

	@Override
	public String objectToString(Object object) {
		return String.valueOf(object);
	}
	
}
