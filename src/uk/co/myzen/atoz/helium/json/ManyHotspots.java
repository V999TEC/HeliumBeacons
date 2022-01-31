package uk.co.myzen.atoz.helium.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ManyHotspots {

	@JsonProperty("data")
	private List<HotSpot> data;

	public List<HotSpot> getData() {
		return data;
	}

	public void setData(List<HotSpot> data) {
		this.data = data;
	}

}
