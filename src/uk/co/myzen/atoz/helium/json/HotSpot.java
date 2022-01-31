package uk.co.myzen.atoz.helium.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HotSpot {

	@JsonProperty("lat")
	private Double lat;

	@JsonProperty("lng")
	private Double lng;

	@JsonProperty("distance")
	private Float distance;

	@JsonProperty("gain")
	private Integer gain;

	@JsonProperty("elevation")
	private Integer elevation;

	@JsonProperty("timestamp_added")
	private String timestampAdded;

	@JsonProperty("status")
	private Status status;

	@JsonProperty("name")
	private String name;

	@JsonProperty("mode")
	private String mode;

	@JsonProperty("address")
	private String address;

	@JsonProperty("location")
	private String location;

	@JsonProperty("location_hex")
	private String locationHex;

	@JsonProperty("geocode")
	private GeoCode geocode;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public GeoCode getGeocode() {
		return geocode;
	}

	public void setGeocode(GeoCode geocode) {
		this.geocode = geocode;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public String getTimestampAdded() {
		return timestampAdded;
	}

	public void setTimestampAdded(String timestampAdded) {
		this.timestampAdded = timestampAdded;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Integer getGain() {
		return gain;
	}

	public void setGain(Integer gain) {
		this.gain = gain;
	}

	public Integer getElevation() {
		return elevation;
	}

	public void setElevation(Integer elevation) {
		this.elevation = elevation;
	}

	public String getLocationHex() {
		return locationHex;
	}

	public void setLocationHex(String locationHex) {
		this.locationHex = locationHex;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Float getDistance() {
		return distance;
	}

	public void setDistance(Float distance) {
		this.distance = distance;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}
