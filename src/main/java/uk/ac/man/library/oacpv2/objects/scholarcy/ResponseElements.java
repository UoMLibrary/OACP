package uk.ac.man.library.oacpv2.objects.scholarcy;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseElements {
	
	
	@JsonProperty("items")
	private ArrayList<Elements> items;

	public ArrayList<Elements> getItems() {
		return items;
	}

	public void setItems(ArrayList<Elements> items) {
		this.items = items;
	}

}
