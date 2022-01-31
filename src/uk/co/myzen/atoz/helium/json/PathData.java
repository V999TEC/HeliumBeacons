package uk.co.myzen.atoz.helium.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PathData {

	@JsonProperty("witnesses")
	private List<Witness> witnesses;

	@JsonProperty("receipt")
	private Receipt receipt;

	@JsonProperty("geocode")
	private GeoCode geocode;

	@JsonProperty("challengee_owner")
	private String challengeeOwner;

	@JsonProperty("challengee_lon")
	private Double challengeeLon;

	@JsonProperty("challengee_location_hex")
	private String challengeeLocationHex;

	@JsonProperty("challengee_location")
	private String challengeeLocation;

	@JsonProperty("challengee_lat")
	private Double challengeeLat;

	@JsonProperty("challengee")
	private String challengee;

	public List<Witness> getWitnesses() {
		return witnesses;
	}

	public void setWitnesses(List<Witness> witnesses) {
		this.witnesses = witnesses;
	}

	public GeoCode getGeocode() {
		return geocode;
	}

	public void setGeocode(GeoCode geocode) {
		this.geocode = geocode;
	}

	public String getChallengeeOwner() {
		return challengeeOwner;
	}

	public void setChallengeeOwner(String challengeeOwner) {
		this.challengeeOwner = challengeeOwner;
	}

	public Double getChallengeeLon() {
		return challengeeLon;
	}

	public void setChallengeeLon(Double challengeeLon) {
		this.challengeeLon = challengeeLon;
	}

	public String getChallengeeLocationHex() {
		return challengeeLocationHex;
	}

	public void setChallengeeLocationHex(String challengeeLocationHex) {
		this.challengeeLocationHex = challengeeLocationHex;
	}

	public String getChallengeeLocation() {
		return challengeeLocation;
	}

	public void setChallengeeLocation(String challengeeLocation) {
		this.challengeeLocation = challengeeLocation;
	}

	public String getChallengee() {
		return challengee;
	}

	public void setChallengee(String challengee) {
		this.challengee = challengee;
	}

	public Receipt getReceipt() {
		return receipt;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	public Double getChallengeeLat() {
		return challengeeLat;
	}

	public void setChallengeeLat(Double challengeeLat) {
		this.challengeeLat = challengeeLat;
	}

}
