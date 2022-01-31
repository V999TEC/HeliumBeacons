package uk.co.myzen.atoz.helium.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class City {

	@JsonProperty("short_city")
	private String shortCity;

	@JsonProperty("short_state")
	private String shortState;

	@JsonProperty("short_country")
	private String shortCountry;

	@JsonProperty("online_count")
	private Integer onlineCount;

	@JsonProperty("offline_count")
	private Integer offlineCount;

	@JsonProperty("long_city")
	private String longCity;

	@JsonProperty("long_state")
	private String longState;

	@JsonProperty("long_country")
	private String longCountry;

	@JsonProperty("hotspot_count")
	private Integer hotspotCount;

	@JsonProperty("city_id")
	private String cityId;

	public String getShortCity() {
		return shortCity;
	}

	public void setShortCity(String shortCity) {
		this.shortCity = shortCity;
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

	public Integer getOnlineCount() {
		return onlineCount;
	}

	public void setOnlineCount(Integer onlineCount) {
		this.onlineCount = onlineCount;
	}

	public Integer getOfflineCount() {
		return offlineCount;
	}

	public void setOfflineCount(Integer offlineCount) {
		this.offlineCount = offlineCount;
	}

	public String getLongCity() {
		return longCity;
	}

	public void setLongCity(String longCity) {
		this.longCity = longCity;
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

	public Integer getHotspotCount() {
		return hotspotCount;
	}

	public void setHotspotCount(Integer hotspotCount) {
		this.hotspotCount = hotspotCount;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

}
