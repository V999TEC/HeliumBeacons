package uk.co.myzen.atoz.helium.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Location {

	@JsonProperty("data")
	private GeoCode data;

	public GeoCode getData() {
		return data;
	}

	public void setData(GeoCode data) {
		this.data = data;
	}

}
