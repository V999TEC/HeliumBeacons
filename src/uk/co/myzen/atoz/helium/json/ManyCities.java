package uk.co.myzen.atoz.helium.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ManyCities {

	@JsonProperty("data")
	private List<City> data;

	public List<City> getData() {
		return data;
	}

	public void setData(List<City> data) {
		this.data = data;
	}
}
