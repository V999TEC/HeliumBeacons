package uk.co.myzen.atoz.helium;

public class Cache {

	private String address;

	private String cityId;

	// private String location;

	private String locationHex;

	private String threeWordName;

	private String longCity;

	private Double latitude;

	private Double longitude;

	public String toString() {

		StringBuffer sb = new StringBuffer();

		sb.append(address);
		sb.append('=');
		sb.append(cityId);
		sb.append(' ');
//		sb.append(location);
//		sb.append(' ');
		sb.append(locationHex);
		sb.append(' ');
		sb.append(latitude);
		sb.append(' ');
		sb.append(longitude);
		sb.append(' ');
		sb.append(threeWordName);
		sb.append(' ');
		sb.append(longCity);

		return sb.toString();
	}

	public Cache(String address, String cityId,
//			String location, 
			String locationHex, Double latitude, Double longitude, String threeWordName, String longCity) {

		setAddress(address);
		setCityId(cityId);
//		setLocation(location);
		setLocationHex(locationHex);
		setThreeWordName(threeWordName);
		setLongCity(longCity);
		setLatitude(latitude);
		setLongitude(longitude);
	}

	public static Cache instanceFromKeyValue(String key, String value) {

		int index = 6;

		String[] values = value.split(" ", index);

		String longCity = values[--index];

		String threeWordName = values[--index];

		double longitude = Double.parseDouble(values[--index]);
		double latitude = Double.parseDouble(values[--index]);

		String locationHex = values[--index];

		String cityId = values[--index];

//		String location = values[--index];

		return new Cache(key, cityId,
//				location, 
				locationHex, latitude, longitude, threeWordName, longCity);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLocationHex() {
		return locationHex;
	}

	public void setLocationHex(String locationHex) {
		this.locationHex = locationHex;
	}

	public String getThreeWordName() {
		return threeWordName;
	}

	public void setThreeWordName(String threeWordName) {
		this.threeWordName = threeWordName;
	}

	public String getLongCity() {
		return longCity;
	}

	public void setLongCity(String longCity) {
		this.longCity = longCity;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
//
//	public String getLocation() {
//		return location;
//	}
//
//	public void setLocation(String location) {
//		this.location = location;
//	}
}
