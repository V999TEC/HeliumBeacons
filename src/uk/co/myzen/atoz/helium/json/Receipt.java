package uk.co.myzen.atoz.helium.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Receipt {

	@JsonProperty("tx_power")
	private Integer txPower;

	@JsonProperty("timestamp")
	private Long timestamp;

	@JsonProperty("snr")
	private Float snr;

	@JsonProperty("signal")
	private Integer signal;

	@JsonProperty("origin")
	private String origin;

	@JsonProperty("gateway")
	private String gateway;

	@JsonProperty("frequency")
	private Float frequency;

	@JsonProperty("datarate")
	private String datarate;

	@JsonProperty("data")
	private String data;

	@JsonProperty("channel")
	private Integer channel;

	public Integer getTxPower() {
		return txPower;
	}

	public void setTxPower(Integer txPower) {
		this.txPower = txPower;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Float getSnr() {
		return snr;
	}

	public void setSnr(Float snr) {
		this.snr = snr;
	}

	public Integer getSignal() {
		return signal;
	}

	public void setSignal(Integer signal) {
		this.signal = signal;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public Float getFrequency() {
		return frequency;
	}

	public void setFrequency(Float frequency) {
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

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
