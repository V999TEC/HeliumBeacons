package uk.co.myzen.atoz.helium.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParameterMap {

	@JsonProperty("version")
	private Integer version;

	@JsonProperty("type")
	private String type;

	@JsonProperty("time")
	private Integer time;

	@JsonProperty("secret")
	private String secret;

	@JsonProperty("request_block_hash")
	private String requestBlockHash;

	@JsonProperty("start_epoch")
	private int startEpoch;

	@JsonProperty("rewards")
	private List<Object> rewards;

	@JsonProperty("secret_hash")
	private String secretHash;

	@JsonProperty("onion_key_hash")
	private String onionKeyHash;

	@JsonProperty("height")
	private Integer height;

	@JsonProperty("hash")
	private String hash;

	@JsonProperty("end_epoch")
	private int endEpoch;

	@JsonProperty("fee")
	private Integer fee;

	@JsonProperty("challenger_owner")
	private String challengerOwner;

	@JsonProperty("challenger_location")
	private String challengerLocation;

	@JsonProperty("challenger")
	private String challenger;

	@JsonProperty("block_hash")
	private String blockHash;

	@JsonProperty("path")
	private List<PathData> path;

	@JsonProperty("receipt")
	private Object receipt;

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public String getSecretHash() {
		return secretHash;
	}

	public void setSecretHash(String secretHash) {
		this.secretHash = secretHash;
	}

	public String getOnionKeyHash() {
		return onionKeyHash;
	}

	public void setOnionKeyHash(String onionKeyHash) {
		this.onionKeyHash = onionKeyHash;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Integer getFee() {
		return fee;
	}

	public void setFee(Integer fee) {
		this.fee = fee;
	}

	public String getChallengerOwner() {
		return challengerOwner;
	}

	public void setChallengerOwner(String challengerOwner) {
		this.challengerOwner = challengerOwner;
	}

	public String getChallengerLocation() {
		return challengerLocation;
	}

	public void setChallengerLocation(String challengerLocation) {
		this.challengerLocation = challengerLocation;
	}

	public String getChallenger() {
		return challenger;
	}

	public void setChallenger(String challenger) {
		this.challenger = challenger;
	}

	public String getBlockHash() {
		return blockHash;
	}

	public void setBlockHash(String blockHash) {
		this.blockHash = blockHash;
	}

	public int getStartEpoch() {
		return startEpoch;
	}

	public void setStartEpoch(int startEpoch) {
		this.startEpoch = startEpoch;
	}

	public List<Object> getRewards() {
		return rewards;
	}

	public void setRewards(List<Object> rewards) {
		this.rewards = rewards;
	}

	public int getEndEpoch() {
		return endEpoch;
	}

	public void setEndEpoch(int endEpoch) {
		this.endEpoch = endEpoch;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getRequestBlockHash() {
		return requestBlockHash;
	}

	public void setRequestBlockHash(String requestBlockHash) {
		this.requestBlockHash = requestBlockHash;
	}

	public Object getReceipt() {
		return receipt;
	}

	public void setReceipt(Object receipt) {
		this.receipt = receipt;
	}

	public List<PathData> getPath() {
		return path;
	}

	public void setPath(List<PathData> path) {
		this.path = path;
	}

}
