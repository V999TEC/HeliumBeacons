package uk.co.myzen.atoz.helium.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActivityCursor {

	@JsonProperty("data")
	private List<ParameterMap> data;

	@JsonProperty("cursor")
	private String cursor;

	public String getCursor() {
		return cursor;
	}

	public void setCursor(String cursor) {
		this.cursor = cursor;
	}

	public List<ParameterMap> getData() {
		return data;
	}

	public void setData(List<ParameterMap> data) {
		this.data = data;
	}

}
