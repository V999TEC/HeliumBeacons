package uk.co.myzen.atoz.helium.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataHotSpot {

	@JsonProperty("data")
	private HotSpot data;

	public HotSpot getData() {
		return data;
	}

	public void setData(HotSpot data) {
		this.data = data;
	}

}
