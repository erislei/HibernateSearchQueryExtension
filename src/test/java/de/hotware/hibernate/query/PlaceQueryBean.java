package de.hotware.hibernate.query;

import de.hotware.hiberante.query.stringbridge.ToStringStringBridge;

public class PlaceQueryBean extends BaseQueryBean<Place> {

	private String name;

	@SearchField(fieldNames = { "name", "sorcerers.name" }, betweenFields = Junction.SHOULD, 
			queryType = StockQueryTypes.Term.class, stringBridge = ToStringStringBridge.class)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
