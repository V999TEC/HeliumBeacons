package uk.co.myzen.atoz.helium.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Status {

	@JsonProperty("timestamp")
	private String timestamp;

	@JsonProperty("online")
	private String online;

	@JsonProperty("listen_addrs")
	private List<String> listenAddrs;

	@JsonProperty("height")
	private Integer height;

	public List<String> getListenAddrs() {
		return listenAddrs;
	}

	public void setListenAddrs(List<String> listenAddrs) {
		this.listenAddrs = listenAddrs;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getOnline() {
		return online;
	}

	public void setOnline(String online) {
		this.online = online;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
