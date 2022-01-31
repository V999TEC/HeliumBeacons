package uk.co.myzen.atoz.helium.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeoCode {

	@JsonProperty("short_street")
	private String shortStreet;

	@JsonProperty("short_state")
	private String shortState;

	@JsonProperty("short_country")
	private String shortCountry;

	@JsonProperty("short_city")
	private String shortCity;

	@JsonProperty("long_street")
	private String longStreet;

	@JsonProperty("long_state")
	private String longState;

	@JsonProperty("long_country")
	private String longCountry;

	@JsonProperty("long_city")
	private String longCity;

	@JsonProperty("location")
	private String location;

	@JsonProperty("city_id")
	private String cityId;

	public String getShortStreet() {
		return shortStreet;
	}

	public void setShortStreet(String shortStreet) {
		this.shortStreet = shortStreet;
	}

	public String getShortState() {
		return shortState;
	}

	public void setShortState(String shortState) {
		this.shortState = shortState;
	}

	public String getShortCountry() {
		return shortCountry;
	}

	public void setShortCountry(String shortCountry) {
		this.shortCountry = shortCountry;
	}

	public String getShortCity() {
		return shortCity;
	}

	public void setShortCity(String shortCity) {
		this.shortCity = shortCity;
	}

	public String getLongStreet() {
		return longStreet;
	}

	public void setLongStreet(String longStreet) {
		this.longStreet = longStreet;
	}

	public String getLongState() {
		return longState;
	}

	public void setLongState(String longState) {
		this.longState = longState;
	}

	public String getLongCountry() {
		return longCountry;
	}

	public void setLongCountry(String longCountry) {
		this.longCountry = longCountry;
	}

	public String getLongCity() {
		return longCity;
	}

	public void setLongCity(String longCity) {
		this.longCity = longCity;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
