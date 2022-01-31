package uk.co.myzen.atoz.helium.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Witness {

	private Long timestamp;

	private Double snr;

	private Integer signal;

	@JsonProperty("packet_hash")
	private String packetHash;

	private String owner;

	@JsonProperty("location_hex")
	private String locationHex;

	private String location;

	@JsonProperty("is_valid")
	private Boolean isValid;

	@JsonProperty("invalid_reason")
	private String invalidReason;

	private String gateway;

	private Double frequency;

	private String datarate;

	private Integer channel;

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Double getSnr() {
		return snr;
	}

	public void setSnr(Double snr) {
		this.snr = snr;
	}

	public Integer getSignal() {
		return signal;
	}

	public void setSignal(Integer signal) {
		this.signal = signal;
	}

	public String getPacketHash() {
		return packetHash;
	}

	public void setPacketHash(String packetHash) {
		this.packetHash = packetHash;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
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

	public Boolean getIsValid() {
		return isValid;
	}

	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public Double getFrequency() {
		return frequency;
	}

	public void setFrequency(Double frequency) {
		this.frequency = frequency;
	}

	public String getDatarate() {
		return datarate;
	}

	public void setDatarate(String datarate) {
		this.datarate = datarate;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public String getInvalidReason() {
		return invalidReason;
	}

	public void setInvalidReason(String invalidReason) {
		this.invalidReason = invalidReason;
	}

}
