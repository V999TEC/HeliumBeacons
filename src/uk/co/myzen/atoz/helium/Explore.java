package uk.co.myzen.atoz.helium;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import uk.co.myzen.atoz.helium.json.ActivityCursor;
import uk.co.myzen.atoz.helium.json.City;
import uk.co.myzen.atoz.helium.json.DataHotSpot;
import uk.co.myzen.atoz.helium.json.GeoCode;
import uk.co.myzen.atoz.helium.json.HotSpot;
import uk.co.myzen.atoz.helium.json.Location;
import uk.co.myzen.atoz.helium.json.ManyCities;
import uk.co.myzen.atoz.helium.json.ManyHotspots;
import uk.co.myzen.atoz.helium.json.ParameterMap;
import uk.co.myzen.atoz.helium.json.PathData;
import uk.co.myzen.atoz.helium.json.Status;
import uk.co.myzen.atoz.helium.json.Witness;
import uk.co.myzen.atoz.utility.Haversine;

public class Explore {

	public final static String[] API = { "https://helium-api.stakejoy.com/v1/", "https://api.helium.io/v1/" };
	// https://api.helium.io/v1/
	// https://helium-api.stakejoy.com/v1/

	public static int API_INDEX = 1;

	public final static String resourcesCache = "cache.properties";

	private final static File cache = new File("RecentAddresses.properties");

	private final static String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36";
	private final static String contentType = "application/json";

	private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss");

	private final static DateTimeFormatter formatterZulu = DateTimeFormatter
			.ofPattern("yyyy-MM-dd'T'HH:mm:ss.nnnnnnnnn'Z'");

	private static Properties constraints = new Properties();

	private static Set<String> exclusions = new HashSet<String>();

	private static Properties lookups = loadProperties(constraints, exclusions); // will combine properties from
	// resources/cache.properties &
	// RecentAddresses.properties

	private static Map<String, TreeSet<Long>> beaconEvents = new HashMap<String, TreeSet<Long>>();

	public static Explore instance;

	private final ObjectMapper mapper;

	private final Haversine h;

	private long millis = 5000;

	private Map<String, String> cities = null;

	public static synchronized Explore getInstance() {

		if (null == instance) {

			instance = new Explore();
		}

		return instance;
	}

	private Explore() {

		h = new Haversine();

		mapper = new ObjectMapper();

		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	private void displayHelp() {

		System.out.println("Option: no parameters:");
		System.out.println("\tApp will start analysing recently published global beacon details.");
		System.out.println(
				"\tThe contents of resources/cache.properties should be set first, otherwise ALL beacons will be listed.");
		System.out.println(
				"\tWhen resources/cache.properties has a list of city/area ids the scope of the ALL beacons is filtered.");
		System.out.println("\nOption: city/area name or cityId");
		System.out.println("\tThe app will enumerate all the hotspots for the specified area or id and analyse.");
		System.out.println("\nOption: three-word-address");
		System.out.println("\tOne or more three-word-addresses can be specified");
		System.out.println("\nOption: a single Hex id");
		System.out
				.println("\tA single address for a Hex will enumerate the hotspots in that hexagonal area and analyse");
		System.out.println("\nOption: a single hostspot address");
		System.out.println("\tThe hotspot will be analysed");
		System.out.println("\nOption: A circular area with a radius in metres from a decimal degrees coordinate:");
		System.out.println("\tExample: lat=51.55722 lon=-1.77671 distance=6500");
		System.out.println("\nOption: A rectangular area specifying the south-west and north-east coordinates:");
		System.out.println("\tExample: swlat=50.56734 swlon=-1.58319 nelat=50.76640 nelon=-1.04253");
		System.out.println("\nN.B.\tAll parameters are position dependent and space delimited");
		System.out.println("N.B.\tDates should be used to limit the output");

		System.out.println("\nAn AFTER date (in Zulu format) may be specified after all the other parameters");
		System.out.println("\nZulu date must be in the strict format YYYY-MM-DDThh:mm:ss.123456789Z");
		System.out.println("\nOnly if an AFTER date is used, a BEFORE date may follow as the last parameter");
		System.out.println("\n\tExample: Wroughton 2022-01-10T12:00:00.123456789Z  2022-01-11T23:59:59.999999999Z");
		System.out.println(
				"\tThis would enumerate all the hotspots in the area known as Wroughton between the specified dates");
		System.out.println("\nIf only one date is specified, the BEFORE date is implicitly 'time now'");

		System.out.println("\n\nAnd finally...");
		System.out.println(
				"\tFor future efficiency one may replace the contents of resources/cache.properties (in jar) by local addresses");
	}

	public static void main(String[] args) throws IOException {

		Explore explore = getInstance();

		explore.analyseBeacons(args);
	}

	private void analyseBeacons(String[] args) throws IOException {

		Float swLat = null;
		Float swLon = null;
		Float neLat = null;
		Float neLon = null;

		Float latitude = null;
		Float longitude = null;

		Integer distance = null;

		Map<Instant, String> summarySent = new HashMap<Instant, String>();

		ManyHotspots hotspotsForCity = null;

		List<HotSpot> selectedHotspots = new ArrayList<HotSpot>();

		int skip = 0; // advanced arg by arg until it points to first zulu time

		if (args.length > 0) {

			if (args[0].startsWith("/h") || args[0].startsWith(":/h") || args[0].toUpperCase().contains("HELP")) {

				displayHelp();

				return;
			}

			if (args.length > 2) {

				if (args[0].startsWith("lat")) {

					int eq = args[0].indexOf('=');

					latitude = Float.parseFloat(args[0].substring(1 + eq));

					if (args[1].startsWith("lon")) {

						eq = args[1].indexOf('=');

						longitude = Float.parseFloat(args[1].substring(1 + eq));

						if (args[2].startsWith("dist")) {

							eq = args[2].indexOf('=');

							distance = Integer.parseInt(args[2].substring(1 + eq));

							skip = 3;

							ManyHotspots mhs = obtainHotspotsForCircleRadius(latitude, longitude, distance);

							List<HotSpot> hsList = mhs.getData();

							for (HotSpot hs : hsList) {

								selectedHotspots.add(hs);
							}
						}
					}
				}
			}

			if (0 == selectedHotspots.size()) {

				// test if a box has been specified using swlat=a, swlon=b, nelat=c, nelon=d

				if (args.length > 3) {

					if (args[0].startsWith("swlat=")) {

						swLat = Float.parseFloat(args[0].substring(6));

						if (args[1].startsWith("swlon=")) {

							swLon = Float.parseFloat(args[1].substring(6));

							if (args[2].startsWith("nelat=")) {

								neLat = Float.parseFloat(args[2].substring(6));

								if (args[3].startsWith("nelon=")) {

									neLon = Float.parseFloat(args[3].substring(6));

									skip = 4;

									ManyHotspots mhs = obtainHotspotsForBox(swLat, swLon, neLat, neLon);

									List<HotSpot> hsList = mhs.getData();

									for (HotSpot hs : hsList) {

										selectedHotspots.add(hs);
									}
								}
							}
						}
					}
				}
			}

			if (0 == selectedHotspots.size()) {

				// scan args for 3-word-names, build a list

				for (String arg : args) {

					// we assume names do not have characters found in a zulu time

					if (arg.indexOf(':') > -1) {

						break;
					}

					if (arg.indexOf('.') > -1) {

						break;
					}

					int a = arg.indexOf('-');

					if (-1 == a) {

						continue;
					}

					int b = arg.indexOf('-', 1 + a);

					if (-1 == b) {

						continue;
					}

					int c = arg.indexOf('-', 1 + b);

					if (-1 != c) {

						continue;
					}

					skip++;

					// assume arg is pattern xxx-yyy-zzz

					// find the address from the address cache

					String address = null;

					for (Object k : lookups.keySet()) {

						address = (String) k;

						String value = lookups.getProperty(address);

						if (value.contains(arg)) {

							Cache cache = Cache.instanceFromKeyValue(address, value);

							HotSpot hs = new HotSpot();

							hs.setAddress(address);
							hs.setName(cache.getThreeWordName());
							hs.setLocationHex(cache.getLocationHex());
//							hs.setLocation(cache.getLocation());
							hs.setLat(cache.getLatitude());
							hs.setLng(cache.getLongitude());

							String longCity = cache.getLongCity();

							City city = null;

							if (lookups.containsValue(longCity)) {

								for (Object o : lookups.keySet()) {

									String key = (String) o;

									String val = lookups.getProperty(key);

									if (longCity.equals(val)) {

										city = new City();

										city.setCityId(key);
										city.setLongCity(longCity);

										break;
									}
								}
							} else {

								city = findCityIgnoreCase(longCity);

								lookups.put(city.getCityId(), longCity);
							}

							GeoCode geocode = new GeoCode();

							geocode.setCityId(city.getCityId());
							geocode.setLongCity(city.getLongCity());

							hs.setGeocode(geocode);

							hs.setStatus(null);

							selectedHotspots.add(hs);

							break;
						}

						address = null;
					}

					if (null == address) { // lookup this three-word-name and create a hotspot (which later will get
											// added to cache)

						String[] qualifiers = arg.split("/");

						ManyHotspots mhs = obtainHotspotsForName(qualifiers[0]); // typically only one, but not
																					// guaranteed

						List<HotSpot> hsList = mhs.getData();

						boolean many = hsList.size() > 1;

						if (many && 1 == qualifiers.length) {

							System.out.println("Ambiguous address " + qualifiers[0]);
						}

						String longCity = "city";

						String locationHex = "";

						for (HotSpot hs : hsList) {

							GeoCode g = hs.getGeocode();

							longCity = g.getLongCity();

							locationHex = hs.getLocationHex();

							String value = locationHex + " " + hs.getName() + " " + hs.getLocation() + " " + longCity;

							if (!many || (2 == qualifiers.length && value.contains(qualifiers[1]))) {

								selectedHotspots.add(hs);

								lookups.put(hs.getAddress(), value);

								break;
							}

							if (many && 1 == qualifiers.length) {

								System.out.println("\t" + value + "\t" + g.getLongCountry());
							}
						}

						if (many && 1 == qualifiers.length) {

							System.out.println("If used as an arg, qualify such as " + arg + "/" + longCity + " or "
									+ arg + "/" + locationHex + " etc.");
							return;
						}
					}
				}
			}

			if (null == selectedHotspots || 0 == selectedHotspots.size()) {

				if (args.length > 0 && !args[0].contains(":")) { // assume first param is NOT a zulu date

					skip++;

					int p1Len = args[0].length();

					if (p1Len > 100) {

						// assume args[0] is a cursor for a global list of challenges

					} else if (p1Len < 16 && args[0].endsWith("ffff")) { // this is a probably Hex address

						hotspotsForCity = obtainHotspotsForHex(args[0]);

						selectedHotspots = hotspotsForCity.getData();

						System.out.println("Hex " + args[0] + " has " + selectedHotspots.size() + " hotspot(s)");

					} else if (args[0].startsWith("11")) {

						// assume individual hotspot

						DataHotSpot hotspot = obtainHotspotForAddress(args[0]);

						selectedHotspots.add(hotspot.getData());

						System.out.println("Address " + args[0] + " has the following hotspot:");

					} else { // assume this is a city: implying many hex & hot spots

						// is it a city name or a city id?

						String cityId = null;

						if (args[0].length() > 35) {

							cityId = args[0];

						} else {

							City city = findCityIgnoreCase(args[0]);

							cityId = city.getCityId();
						}

						if (null == cityId) {

							System.err.println("Unable to determine cityId from name " + args[0]);

							return;
						}

						hotspotsForCity = obtainHotspotsForCity(cityId);

						selectedHotspots = hotspotsForCity.getData();

						System.out.println("Area " + args[0] + " has " + selectedHotspots.size() + " hotspot(s)");
					}
				}
			}
		}

		// add hotspots & "cities" to cache

		Set<String> cityIds = new HashSet<String>();

		cities = new HashMap<String, String>();

//		exclusions = new HashSet<String>();

		for (Object k : lookups.keySet()) {

			String key = (String) k;

			String value = lookups.getProperty(key);

			if (!key.startsWith("11")) {

				// assume a city_id

				if (!cityIds.contains(key)) {

					cityIds.add(key);

					cities.put(key, value);
				}
			}
		}

		int hotspotsSize = lookups.size() - cities.size();

		System.out.println("\nThere are " + hotspotsSize + " hotspots & " + cities.size()
				+ " city/areas currently cached (including " + exclusions.size() + " exclusions & " + constraints.size()
				+ " constraints)\n");

		for (String exclusion : exclusions) {

			String cityArea = exclusion.replace('_', ' ');

			System.out.println("Excluded:" + cityArea);
		}

		for (String cityId : cityIds) {

			String cityArea = cities.get(cityId);

			if (!exclusions.contains(cityArea)) {

				System.out.println("\t " + cityArea);
			}
		}

		// a time specification must follow the 3-word-names or the single address spec

		Instant earliestTimestamp = null;

		if (args.length > skip) {

			earliestTimestamp = Instant.parse(args[skip]);
		}

		Instant latestTimestamp = Instant.now();

		if (args.length > (1 + skip)) {

			latestTimestamp = Instant.parse(args[1 + skip]);
		}

		String timeRange;

		String timeTo = timeAs(formatter, latestTimestamp.getEpochSecond());

		String timeFrom = null;

		if (null == earliestTimestamp) {

			timeRange = "up to " + timeTo;

		} else {
			timeFrom = timeAs(formatter, earliestTimestamp.getEpochSecond());

			timeRange = "between " + timeFrom + " and " + timeTo;
		}

		if (0 == skip || args[0].length() > 100) {

			// assume no HotSpots selected yet
			System.out.println("\nChecking latest challenges based on city/area(s) named in cache.properties");
			System.out.println(
					"Character '*' before address will indicate city/area(s) not currently in cache.properties\n");

			String cursor = 0 == skip ? null : args[0];

			int tally = 0;

			do {

				try {
					Thread.sleep(1000);

				} catch (InterruptedException e) {

					e.printStackTrace();
				}

				ActivityCursor ac = obtainWorldChallengeData(cursor);

				cursor = ac.getCursor();

				List<ParameterMap> data = ac.getData();

				// create GlobalHotspot

				for (ParameterMap datum : data) {

					Integer time = datum.getTime();

					String hhmmss = timeAs(formatter, time);

					if (0 == tally % 25000) {

						System.out.println(hhmmss + "\t" + tally);
						System.out.println(cursor);
					}

					tally++;

					List<PathData> pathDataList = datum.getPath();

					String cityId = pathDataList.get(0).getGeocode().getCityId();

					// only return city/areas listed in cache.properties
					// unless cache.properties is empty, in which case return ALL

					if (cityIds.size() > 0) {

						if (!cityIds.contains(cityId)) {

							continue;
						}
					}

					String challengee = pathDataList.get(0).getChallengee();

					String value = "";

					List<Witness> witnesses = pathDataList.get(0).getWitnesses();

					Integer channel = null;
					Double frequency = null;

					int witnessCount = witnesses.size();

					if (witnessCount > 0) {

						List<Long> timestamps = new ArrayList<Long>(witnessCount);

						for (Witness witness : witnesses) {

							Long timestamp = witness.getTimestamp();

							timestamps.add(timestamp);

							if (null == channel) {

								channel = witness.getChannel();

								frequency = witness.getFrequency();
							}
						}

						double sd = Double
								.parseDouble(constraints.getProperty("timestampsMaxStandardDeviation", "5.0E18"));

						float minUse = Float.parseFloat(constraints.getProperty("timestampsUseAtLeast", "0.5"));

						Long meanTime = fairMeanTime(timestamps, sd, minUse);

						String meanZulu = timeAsZulu(meanTime.longValue());

						value = meanZulu + " (" + channel + ") " + String.format("%5.1f", frequency);
					}

					String flag;

					Cache cache = null;

					if (lookups.containsKey(challengee)) {

						String geo = lookups.getProperty(challengee); // assume this contains: hex three-word-name
						// location

						cache = Cache.instanceFromKeyValue(challengee, geo);

						flag = " ";

					} else {

						cache = buildGeoForAddress(challengee);

						String[] keyValues = cache.toString().split("=");

						lookups.put(challengee, keyValues[1]);

						cityId = cache.getCityId();

						String witnessLongCity = cache.getLongCity();

						if (!cityIds.contains(cityId)) {

//							if (!g.getLongCountry().equals(data.getGeocode().getLongCountry())) {
//
//								System.err.println("Ignore:\tfalse witness?\t" + cityId + "\t" + witnessLongCity + "\t"
//										+ g.getLongCountry());
//								continue;
//							}

							System.err.println("New:\t" + cityId + "\t" + witnessLongCity);

							cityIds.add(cityId);

							cities.put(cityId, witnessLongCity);
						}

						flag = "*";
					}

					System.out
							.println(hhmmss + " " + flag + " " + challengee + "\t(" + String.format("%2d", witnessCount)
									+ ") " + value + "\t" + cache.getThreeWordName() + "/" + cache.getLongCity());
				}

			} while (null != cursor);

		}

		int count = 0;

		if (0 == selectedHotspots.size()) {

		} else {

			System.out
					.println("\nSelecting the following 3-word-name address(es) for analysis of beacons " + timeRange);

			for (HotSpot hotSpot : selectedHotspots) {

				count++;

				GeoCode g = hotSpot.getGeocode();

				Float metres = hotSpot.getDistance();

				System.out.println(count + "\t" + hotSpot.getName() + "/" + g.getLongCity()
						+ (null != metres && 0 != metres ? "\t" + metres.intValue() + " m" : ""));
			}

			count = 0;
		}

		for (HotSpot hotSpot : selectedHotspots) {

			count++;

			Status status = hotSpot.getStatus();

			if (null == status) {

				DataHotSpot dhs = obtainHotspotForAddress(hotSpot.getAddress());

				hotSpot = dhs.getData();

				status = hotSpot.getStatus();
			}

			String online = status.getOnline();

			List<String> listenAddrs = status.getListenAddrs();

			Double lat = hotSpot.getLat();
			Double lng = hotSpot.getLng();

			String longCity = hotSpot.getGeocode().getLongCity();

			String listen = null != listenAddrs && listenAddrs.size() > 0 ? listenAddrs.get(0) : "";

			boolean relaying = listen.contains("p2p");

			System.out.println("\n" + hotSpot.getName() + "\t<================ " + count + " / "
					+ selectedHotspots.size() + " ==========================\t" + hotSpot.getLocationHex() + "\t"
					+ hotSpot.getGeocode().getLongStreet() + "\t" + longCity + "\t(" + lat + "," + lng + ")\t" + online
					+ "\t" + (relaying ? "relaying" : listen));

			if (relaying) {

				System.out.println("\n\t\t\tThis hotspot is operating a p2p relay via:");

				for (String element : listenAddrs) {

					int beginIndex = 5;
					int endIndex = element.indexOf('/', beginIndex);

					String remoteRelayAddress = element.substring(beginIndex, endIndex);

					DataHotSpot remote = null;

					try {

						remote = obtainHotspotForAddress(remoteRelayAddress);

						GeoCode gRemote = remote.getData().getGeocode();

						System.out.println(
								"\t\t\t" + element + "\t" + gRemote.getLongCity() + "\t" + gRemote.getLongCountry());

					} catch (java.io.FileNotFoundException e) {

						System.out.println("\t\t\t" + remoteRelayAddress + "\tExeception: " + e.getLocalizedMessage());
					}
				}

			}

			String address = hotSpot.getAddress();

			List<ParameterMap> hotspotData = obtainHotspotChallengeData(address, earliestTimestamp, latestTimestamp,
					null);

			boolean timeTooOld = false;

			int pCount = 0;
			for (ParameterMap parameterMap : hotspotData) {

				if (timeTooOld) {

					String timeCutOff = timeAs(formatter, earliestTimestamp.getEpochSecond());

					System.out.println("\t\t\tNo further beacon records later than " + timeCutOff + " (Ignoring "
							+ (hotspotData.size() - pCount)
							+ " available poc receipts for this hotspot with older timestamps)\n\n");

					break;
				}

				pCount++;

				String type = parameterMap.getType();

				if (!"poc_receipts_v1".equals(type)) {

					continue;
				}

				String challenger = parameterMap.getChallenger();

				String hotSpotName = hotSpot.getName();

				Integer time = parameterMap.getTime();

				String hhmmss = timeAs(formatter, time);

				List<PathData> pathData = parameterMap.getPath();

				boolean challengedBeaconer = challenger.equals(address);

				PathData data = pathData.get(0);

				if (challengedBeaconer) {

					// type implied is a Challenged Beaconer
					// which we are not interested

					GeoCode g = pathData.get(0).getGeocode();

					System.out.println("\n" + hhmmss + "\t(" + pCount + ")\t" + hotSpotName
							+ "\tChallenged Beaconer located in hex " + data.getChallengeeLocationHex() + "\t"
							+ g.getLongCity() + "\t" + g.getLongCountry() + "\n");

					continue;
				}

				String challengee = data.getChallengee();
				String infoChallengee = "";

				String nameBeaconer = "";

				Double latitudeBeaconer = null;
				Double longitudeBeaconer = null;

				if (lookups.contains(challengee)) {

					infoChallengee = lookups.getProperty(challengee);

					Cache cache = Cache.instanceFromKeyValue(challengee, infoChallengee);

					longitudeBeaconer = cache.getLongitude();

					latitudeBeaconer = cache.getLatitude();

				} else {

					DataHotSpot hotspotBeaconer = obtainHotspotForAddress(challengee);

					nameBeaconer = hotspotBeaconer.getData().getName();

					GeoCode g = hotspotBeaconer.getData().getGeocode();

					latitudeBeaconer = hotspotBeaconer.getData().getLat();
					longitudeBeaconer = hotspotBeaconer.getData().getLng();

					String longCityBeaconer = g.getLongCity();

					infoChallengee = nameBeaconer + "/" + longCityBeaconer;
				}

				List<Witness> witnessList = data.getWitnesses();

				int included = 0;

				List<Long> timestamps = new ArrayList<Long>(witnessList.size());

				Integer channel = null;
				Double frequency = null;

				System.out.println("\n" + hhmmss + "\t(" + pCount + ")\t" + hotSpotName + "\tBeacon from: " + challengee
						+ "\t" + infoChallengee + "\n");

				// check if challengee is in out list of selected hotspots

				boolean beaconerIsOneOfTheSelectedHotspots = false;

				for (HotSpot selectedHotSpot : selectedHotspots) {

					if (selectedHotSpot.getAddress().equals(challengee)) {

						beaconerIsOneOfTheSelectedHotspots = true;
						break;
					}
				}

				for (Witness witness : witnessList) {

					Long timestamp = witness.getTimestamp();

					Instant witnessTimestamp = nanosToInstant(timestamp);

					if (null != earliestTimestamp) {

						if (witnessTimestamp.isBefore(earliestTimestamp)) {

							timeTooOld = true;
							break;
						}
					}

					if (witnessTimestamp.isAfter(latestTimestamp)) {

						System.out.println("\t\t\tOutside required time range\n");

						break;
					}

					String gateway = witness.getGateway();

					if (!beaconerIsOneOfTheSelectedHotspots) {

						boolean witnessIsOneOfTheSelectedHotspots = false;

						// The beacon was sent by challengee not on our selectedHotspots
						// However if this witness *is* one of selectedHotspots display the detail

						for (HotSpot selectedHotSpot : selectedHotspots) {

							if (gateway.equals(selectedHotSpot.getAddress())) {

								witnessIsOneOfTheSelectedHotspots = true;
								break;
							}
						}

						if (!witnessIsOneOfTheSelectedHotspots) {

							continue; // ignore this witness as neither it or the beaconer is a selected hotspot
						}
					}

					String zulu = timeAsZulu(timestamp);

					Double snr = witness.getSnr();
					Integer signal = witness.getSignal();

					channel = witness.getChannel();

					frequency = witness.getFrequency();

					boolean isValid = witness.getIsValid();

					String invalidReason = witness.getInvalidReason();

					String geo = "";

					boolean added = false;

					Double latitudeWitness = null;

					Double longitudeWitness = null;

					Cache cache = null;

					long distanceWitness;

					Integer maxRange = Integer.parseInt(constraints.getProperty("maxRange", "200000"));

					if (lookups.containsKey(gateway)) {

						geo = lookups.getProperty(gateway); // assume this contains: hex three-word-name
															// location

						cache = Cache.instanceFromKeyValue(gateway, geo);

						String witnessLongCity = cache.getLongCity();

						longitudeWitness = cache.getLongitude();
						latitudeWitness = cache.getLatitude();

						distanceWitness = Math
								.round(Haversine.METRES_IN_IMPERIAL_MILE * h.calculateDistance(latitudeBeaconer,
										longitudeBeaconer, latitudeWitness, longitudeWitness));

						if (distanceWitness > maxRange) {

							System.out.println(String.format("%7d", distanceWitness) + "m > " + maxRange + "\t\t"
									+ "\t\t" + gateway + "\t\t\t\t" + cache.getThreeWordName() + "/"
									+ cache.getLongCity() + "\t<---- excluded (maxRange)");

							continue;
						}

						if (exclusions.contains(witnessLongCity)) {

							System.out.println(String.format("%7d", distanceWitness) + "m\t\t\t\t\t" + gateway
									+ "\t\t\t\t" + cache.getThreeWordName() + "/" + cache.getLongCity()
									+ "\t<---- excluded");

							continue;
						}

					} else {

						// potentially add geo data to cache

						cache = buildGeoForAddress(gateway);

						String witnessLongCity = cache.getLongCity();

						latitudeWitness = cache.getLatitude();
						longitudeWitness = cache.getLongitude();

						distanceWitness = Math
								.round(Haversine.METRES_IN_IMPERIAL_MILE * h.calculateDistance(latitudeBeaconer,
										longitudeBeaconer, latitudeWitness, longitudeWitness));

						if (distanceWitness > maxRange) {

							System.out.println(String.format("%7d", distanceWitness) + "m > " + maxRange + "\t\t"
									+ "\t\t" + gateway + "\t\t\t\t" + cache.getThreeWordName() + "/"
									+ cache.getLongCity() + "\t<---- excluded (maxRange)");

							continue;
						}

						if (exclusions.contains(witnessLongCity)) {

							System.out.println(String.format("%7d", distanceWitness) + "m\t\t\t\t\t" + gateway
									+ "\t\t\t\t" + cache.getThreeWordName() + "/" + cache.getLongCity()
									+ "\t<---- excluded");

							continue;
						}

						String cityId = cache.getCityId();

						if (!cityIds.contains(cityId)) {

							System.err.println("New:\t" + cityId + "\t" + witnessLongCity);

							cityIds.add(cityId);

							cities.put(cityId, witnessLongCity);
						}

						String[] keyValues = cache.toString().split("=");

						geo = keyValues[1];

						lookups.put(gateway, geo);

						added = true;
					}

					System.out.println(String.format("%7d", distanceWitness) + "m\t" + zulu + "\t" + gateway + "\t"
							+ String.format("%4d", signal) + "  " + String.format("%+5.1f", snr) + "  " + channel + "  "
							+ String.format("%5.1f", frequency) + "  " + cache.getThreeWordName() + "/"
							+ cache.getLongCity() + (added ? "\t#" + lookups.size() : "")
							+ (isValid ? "" : "\t<---- invalid: " + invalidReason));

					if (isValid) {

						included++;

						timestamps.add(timestamp);
					}

				}

				if (included > 0) {

					// determine mean beacon time

					double sd = Double.parseDouble(constraints.getProperty("timestampsMaxStandardDeviation", "5.0E18"));

					float minUse = Float.parseFloat(constraints.getProperty("timestampsUseAtLeast", "0.5"));

					Long meanTime = fairMeanTime(timestamps, sd, minUse);

					// capture some timing data related to the current beaconer

					TreeSet<Long> beaconTimings = null;

					if (beaconEvents.containsKey(infoChallengee)) {

						beaconTimings = beaconEvents.get(infoChallengee);
					} else {

						beaconTimings = new TreeSet<Long>();

						beaconEvents.put(infoChallengee, beaconTimings);
					}

					if (!beaconTimings.contains(meanTime)) {

						beaconTimings.add(meanTime);
					}

					String meanZulu = timeAsZulu(meanTime.longValue());

					String text = "is the mean beacon time sent from " + infoChallengee + " witnessed by " + included
							+ " on channel " + channel + " (" + String.format("%3.1f", frequency) + ")";

					Instant mean = Instant.parse(meanZulu);

					summarySent.put(mean, text);

					System.out.println("\n\t\t\t" + meanZulu + "\t" + text);

				}

				if (timeTooOld) {

					break;
				}

			}

			// save our cached three Word name Addresses

			flushToCache();
		}

		displaySummary("sent Beacons", summarySent, timeRange);

		// display

		System.out.println("\n\nFor future efficiency one may replace the contents of resources/" + resourcesCache
				+ " (in jar) by the following text (also saved at " + cache.getAbsolutePath() + ")");

		loadPreviousCache(null); // this will do System.err.println() for each entry

	}

	private long estimateAverageRepeatTime(TreeSet<Long> beaconTimings) {

		long result = 0;

		int size = beaconTimings.size();

		if (size > 1) {

			// calculate delays between first & last timestamps

			long first = beaconTimings.first();

			long last = beaconTimings.last();

			double est = ((last - first) / (size - 1)) / 1000000000;

			result = Double.valueOf(est).longValue();

		}

		return result;
	}

	private static void loadPreviousCache(Properties properties) throws IOException {

		FileInputStream inputStream = new FileInputStream(cache);

		if (null == properties) {

			InputStreamReader isr = new InputStreamReader(inputStream);

			BufferedReader br = new BufferedReader(isr);

			String ln;

			while (null != (ln = br.readLine())) {

				System.err.println(ln);
			}

			br.close();
			isr.close();

		} else {

			properties.load(inputStream);
		}

		inputStream.close();
	}

	private Cache buildGeoForAddress(String address) {

		Cache result = null;

		try {
			Thread.sleep(millis);

			DataHotSpot dhs = obtainHotspotForAddress(address);

			HotSpot hotSpot = dhs.getData();

			GeoCode g = hotSpot.getGeocode();

			String cityId = g.getCityId();

			String locationHex = hotSpot.getLocationHex();

			String longCity = g.getLongCity();

			String threeWordName = hotSpot.getName();

			Double latitudeWitness = hotSpot.getLat();

			Double longitudeWitness = hotSpot.getLng();

			result = new Cache(address, cityId, locationHex, latitudeWitness, longitudeWitness, threeWordName,
					longCity);

		} catch (InterruptedException e) {

			e.printStackTrace();

		} catch (IOException e2) {

		}

		return result;
	}

	private long fairMeanTime(List<Long> timestamps, double maxStandardDeviation, float useAtLeast) {

		// find the mean
		// calculate the standard deviation
		// if SD is within accepted range, return the mean

		// if SD is too large, remove outliers and recalculate mean
		// until variance from mean is acceptable.

		Double meanTime = -1.0;

		Double standardDeviation = null;

		int numTimestamps = timestamps.size();

		List<Double> squares = new ArrayList<Double>(numTimestamps);

		do {

			Double accumulatedTimestamps = 0.0;

			Double accumulatedSquares = 0.0;

			int acceptableTimestampCount = 0;

			for (int index = 0; index < numTimestamps; index++) {

				Long timestamp = timestamps.get(index);

				if (null == timestamp) {

					continue;
				}

				acceptableTimestampCount++;

				Double square;

				if (squares.size() < numTimestamps) {

					square = timestamp.doubleValue() * timestamp.doubleValue();

					squares.add(square);

				} else {

					square = squares.get(index);
				}

				accumulatedSquares += square;

				accumulatedTimestamps += timestamp.doubleValue();
			}

			meanTime = accumulatedTimestamps / acceptableTimestampCount;

			standardDeviation = Math.sqrt(accumulatedSquares);

//			System.out.println("\ts.d. " + standardDeviation + " with " + acceptableTimestampCount
//					+ " timestamps. mean = " + meanTime.longValue());

			if (standardDeviation > maxStandardDeviation || acceptableTimestampCount < (numTimestamps * useAtLeast)) {

				// remove an outlier (choose the one with the greatest variance from mean)

				long largestVariance = 0;

				int indexOfLargestVariance = -1;

				for (int index = 0; index < numTimestamps; index++) {

					Long timestamp = timestamps.get(index);

					if (null == timestamp) {

						continue;
					}

					long variance = Math.abs(meanTime.longValue() - timestamp);

					if (variance > largestVariance) {

						largestVariance = variance;
						indexOfLargestVariance = index;
					}
				}

				if (indexOfLargestVariance < 0) {

					break;
				}

				timestamps.set(indexOfLargestVariance, null);
			}

		} while (standardDeviation > maxStandardDeviation);

		return meanTime.longValue();
	}

	private void displaySummary(String text, Map<Instant, String> summary, String timeRange) {

		System.out.println("\nChronological summary of " + text + " " + timeRange + "\n");

		List<Instant> keyList = new ArrayList<Instant>(summary.size());

		for (Instant key : summary.keySet()) {

			keyList.add(key);
		}

		Collections.sort(keyList);

		for (Instant key : keyList) {

			long epochSeconds = key.getEpochSecond();

			int nano = key.getNano();

			long nanoSeconds = 1000000000 * epochSeconds + nano;

			String textSummary = summary.get(key);

			int beginIndex = 5 + textSummary.indexOf("from ");

			int endIndex = textSummary.indexOf(" witnessed");

			String beaconer = textSummary.substring(beginIndex, endIndex);

			TreeSet<Long> beaconSet = beaconEvents.get(beaconer);

			long averagePeriod = estimateAverageRepeatTime(beaconSet);

			int index = 0;

			for (Long value : beaconSet) {

				index++;

				if (value == nanoSeconds) {

					break;
				}
			}

			String time = timeAsZulu(nanoSeconds);

			System.out.println(time + " " + textSummary + "\tPeriod average: " + averagePeriod + " seconds (" + index
					+ "/" + beaconSet.size() + ")");
		}

	}

	private void flushToCache() throws IOException {

		FileOutputStream outputStream = new FileOutputStream(cache);

		int sizeAddresses = lookups.size();
		int sizeCities = cities.size();
		int sizeConstraints = constraints.size();

		lookups.store(outputStream,
				"Cache of " + (sizeAddresses - sizeCities) + " local addresses & " + sizeCities
						+ " City/Area Ids (including " + exclusions.size() + " exclusions & " + sizeConstraints
						+ " constraints)");

		for (String cityId : cities.keySet()) {

			if (!lookups.containsKey(cityId)) {

				String cityName = cities.get(cityId);

				outputStream.write(cityId.getBytes());
				outputStream.write("=".getBytes());
				outputStream.write(cityName.getBytes());
				outputStream.write("\n".getBytes());
			}
		}

		outputStream.write("#exclusions".getBytes());
		outputStream.write("\n".getBytes());

		for (String exclusion : exclusions) {

			String keyNoValue = exclusion.replace(' ', '_');

			outputStream.write(keyNoValue.getBytes());
			outputStream.write("=".getBytes());
			outputStream.write("\n".getBytes());
		}

		outputStream.write("#constraints".getBytes());
		outputStream.write("\n".getBytes());

		for (Object key : constraints.keySet()) {

			String specialKey = (String) key;
			String specialValue = constraints.getProperty(specialKey);

			outputStream.write(specialKey.getBytes());
			outputStream.write("=".getBytes());
			outputStream.write(specialValue.getBytes());
			outputStream.write("\n".getBytes());
		}

		outputStream.close();
	}

	private ActivityCursor obtainWorldChallengeData(String cursor) throws IOException {

		StringBuilder sb = new StringBuilder();

		sb.append(API[API_INDEX] + "challenges");

		if (null != cursor && 0 != cursor.trim().length()) {

			sb.append("?cursor=");
			sb.append(cursor);
		}

		URL url = new URL(sb.toString());

		String json = getRequest(url);

		ActivityCursor result = "".equals(json) ? null : mapper.readValue(json, ActivityCursor.class);

		return result;
	}

	private ActivityCursor queryHotspotChallenges(String address, String cursor, Instant minTime, Instant maxTime,
			Integer limit) throws IOException {

		StringBuilder sb = new StringBuilder();

		sb.append(API[API_INDEX] + "hotspots/");
		sb.append(address);
		sb.append("/challenges");

		boolean doneFirstQueryParam = false;

		if (null != cursor) {

			if (doneFirstQueryParam) {

				sb.append('&');

			} else {
				sb.append('?');
				doneFirstQueryParam = true;
			}

			sb.append("cursor=");
			sb.append(cursor);
		}

		if (null != minTime) {

			if (doneFirstQueryParam) {

				sb.append('&');

			} else {
				sb.append('?');
				doneFirstQueryParam = true;
			}

			sb.append("min_time=");

			String tMin = timeAsZulu(minTime);

			sb.append(tMin);
		}

		if (null != maxTime) {

			if (doneFirstQueryParam) {

				sb.append('&');

			} else {
				sb.append('?');
				doneFirstQueryParam = true;
			}

			sb.append("max_time=");

			String tMax = timeAsZulu(maxTime);

			sb.append(tMax);
		}

		if (null != limit) {

			if (doneFirstQueryParam) {

				sb.append('&');

			} else {
				sb.append('?');
				doneFirstQueryParam = true;
			}

			sb.append("limit=");
			sb.append(limit);
		}

		URL url = new URL(sb.toString());

		String json = getRequest(url);

		ActivityCursor result = "".equals(json) ? null : mapper.readValue(json, ActivityCursor.class);

		return result;
	}

	private List<ParameterMap> obtainHotspotChallengeData(String address, Instant minTime, Instant maxTime,
			Integer limit) throws IOException {

		List<ParameterMap> result = new ArrayList<ParameterMap>();

		ActivityCursor ac = queryHotspotChallenges(address, null, minTime, maxTime, null);

		if (null == ac) {

			return result;
		}

		String cursor = ac.getCursor();

		if (null == cursor) {

			result = ac.getData();

		} else
			while (null != cursor) {
				ac = queryHotspotChallenges(address, cursor, null, null, null);

				List<ParameterMap> page = ac.getData();

				if (null != page && page.size() > 0) {

					result.addAll(page);
				}

				// is there more ?

				String nextCursor = ac.getCursor();

				if (cursor.equals(nextCursor)) {

					cursor = null;

				} else {

					cursor = nextCursor;
				}

			}
		;

		return result;
	}

	private Location obtainLocation(String location) throws IOException {

		StringBuilder sb = new StringBuilder();

		sb.append(API[API_INDEX] + "locations/");
		sb.append(location);

		URL url = new URL(sb.toString());

		String json = getRequest(url);

		Location result = "".equals(json) ? null : mapper.readValue(json, Location.class);

		return result;
	}

	private City findCityIgnoreCase(String name) throws IOException {

		String nameNoSpaces = name.replace(" ", "%20");

		ManyCities manyCities = searchForCityName(nameNoSpaces);

		for (City city : manyCities.getData()) {

			if (city.getShortCity().equalsIgnoreCase(name) || city.getLongCity().equalsIgnoreCase(name)) {

//				cityId = city.getCityId();

				return city;
			}
		}

		return null;
	}

	private ManyCities searchForCityName(String name) throws IOException {

		// https://api.helium.io/v1/cities?search=swindon

		StringBuilder sb = new StringBuilder();

		sb.append(API[API_INDEX] + "cities?search=");
		sb.append(name);

		URL url = new URL(sb.toString());

		String json = getRequest(url);

		ManyCities result = "".equals(json) ? null : mapper.readValue(json, ManyCities.class);

		return result;
	}

	private ManyHotspots obtainHotspotsForName(String name) throws IOException {

		// Fetch the hotspots which map to the given 3-word animal name.
		// The name must be all lower-case with dashes between the words,
		// e.g. tall-plum-griffin.
		// Because of collisions in the Angry Purple Tiger algorithm,
		// the given name might map to more than one hotspot.

		StringBuilder sb = new StringBuilder();

		sb.append(API[API_INDEX] + "hotspots/name/");
		sb.append(name);

		URL url = new URL(sb.toString());

		String json = getRequest(url);

		ManyHotspots result = "".equals(json) ? null : mapper.readValue(json, ManyHotspots.class);

		return result;

	}

	private DataHotSpot obtainHotspotForAddress(String address) throws IOException {

		StringBuilder sb = new StringBuilder();

		sb.append(API[API_INDEX] + "hotspots/");
		sb.append(address);

		URL url = new URL(sb.toString());

		String json = getRequest(url);

		DataHotSpot result = "".contentEquals(json) ? null : mapper.readValue(json, DataHotSpot.class);

		return result;

	}

	private ManyHotspots obtainHotspotsForHex(String hexH3Index) throws IOException {

		StringBuilder sb = new StringBuilder();

		sb.append(API[API_INDEX] + "hotspots/hex/");
		sb.append(hexH3Index);

		URL url = new URL(sb.toString());

		String json = getRequest(url);

		ManyHotspots result = "".equals(json) ? null : mapper.readValue(json, ManyHotspots.class);

		return result;

	}

	private ManyHotspots obtainHotspotsForCity(String cityId) throws IOException {

		StringBuilder sb = new StringBuilder();

		sb.append(API[API_INDEX] + "cities/");
		sb.append(cityId);
		sb.append("/hotspots");

		URL url = new URL(sb.toString());

		String json = getRequest(url);

		ManyHotspots result = "".equals(json) ? null : mapper.readValue(json, ManyHotspots.class);

		return result;

	}

	private ManyHotspots obtainHotspotsForBox(float latSouthWestCorner, float lonSouthWestCorner,
			float latNorthEastCorner, float lonNorthEastCorner) throws IOException {

		StringBuilder sb = new StringBuilder();

		sb.append(API[API_INDEX] + "hotspots/location/box?swlat=");
		sb.append(latSouthWestCorner);
		sb.append("&swlon=");
		sb.append(lonSouthWestCorner);
		sb.append("&nelat=");
		sb.append(latNorthEastCorner);
		sb.append("&nelon=");
		sb.append(lonNorthEastCorner);

		URL url = new URL(sb.toString());

		String json = getRequest(url);

		ManyHotspots manyHotspots = "".equals(json) ? null : mapper.readValue(json, ManyHotspots.class);

		return manyHotspots;
	}

	private ManyHotspots obtainHotspotsForCircleRadius(float latCentre, float lonCentre, int metresRadius)
			throws IOException {

		StringBuilder sb = new StringBuilder();

		sb.append(API[API_INDEX] + "hotspots/location/distance?lat=");
		sb.append(latCentre);
		sb.append("&lon=");
		sb.append(lonCentre);
		sb.append("&distance=");
		sb.append(metresRadius);

		URL url = new URL(sb.toString());

		String json = getRequest(url);

		ManyHotspots manyHotspots = "".equals(json) ? null : mapper.readValue(json, ManyHotspots.class);

		return manyHotspots;
	}

	private String obtainHotspotCursor(String hotspot) throws IOException {

		StringBuilder sb = new StringBuilder();

		sb.append(API[API_INDEX] + "hotspots/");
		sb.append(hotspot);
		sb.append("/activity");

		URL url = new URL(sb.toString());

		String json = getRequest(url);

		ActivityCursor activityCuror = "".equals(json) ? null : mapper.readValue(json, ActivityCursor.class);

		return activityCuror.getCursor();
	}

	private List<ParameterMap> obtainHotspotData(String hotspot, String cursor) throws IOException {

		StringBuilder sb = new StringBuilder();

		sb.append(API[API_INDEX] + "hotspots/");
		sb.append(hotspot);
		sb.append("/activity");
		sb.append("?cursor=");
		sb.append(cursor);

		URL url = new URL(sb.toString());

		String json = getRequest(url);

		ActivityCursor activityCursor = "".equals(json) ? null : mapper.readValue(json, ActivityCursor.class);

		List<ParameterMap> result = activityCursor.getData();

		return result;
	}

	private String getRequest(URL url) throws IOException {

		int status = -1;
		HttpURLConnection con = null;

		do {
			try {
				Thread.sleep(millis);

			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			con.setRequestProperty("Content-Type", contentType);
			con.setRequestProperty("user-agent", userAgent);

			con.connect();

			status = con.getResponseCode();

			if (429 == status) {

				con.disconnect();

				millis += 1000;
			}

		} while (429 == status);

		if (millis > 200) {

			millis -= 100;
		}

		String json = "";

		BufferedReader in = null;

		try {

			in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			String inputLine;
			StringBuffer content = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {

				content.append(inputLine);
			}

			json = content.toString();

		} catch (IOException e) {

			e.printStackTrace();
		}

		if (null != in) {

			in.close();
		}

		if (null != con) {

			con.disconnect();
		}

		return json;
	}

	static String timeAs(DateTimeFormatter formatter, long epochSecond) {

		LocalDateTime ldt = LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC);

		return String.format("%8s", ldt.format(formatter));
	}

	static String timeAs(long epochMilliSecond) {

		Instant instant = Instant.ofEpochMilli(epochMilliSecond);

		LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

		return String.format("%8s", ldt.format(formatter));
	}

	static Instant nanosToInstant(long epochNanoSecond) {

		long msTimestamp = epochNanoSecond / 1000000;

		long epochSecond = msTimestamp / 1000;

		long nanoAdjustment = epochNanoSecond - (epochSecond * 1000000000);

		Instant instant = Instant.ofEpochSecond(epochSecond, nanoAdjustment);

		return instant;
	}

	static String timeAsZulu(long epochNanoSecond) {

		Instant instant = nanosToInstant(epochNanoSecond);

		return timeAsZulu(instant);
	}

	static String timeAsZulu(Instant instant) {

		LocalDateTime ldtZulu = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

		String result = String.format("%8s", ldtZulu.format(formatterZulu));

		return result;
	}

	private static Properties loadProperties(Properties constraints, Set<String> exclusions) {

		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream is = cl.getResourceAsStream(resourcesCache);

		Properties properties = new Properties();

		try {

			properties.load(is);

			is.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

		try {

			if (cache.exists()) {

				loadPreviousCache(properties);
			}

		} catch (IOException e) {

			e.printStackTrace();
		}

		// move the constraints out of the properties

		String[] specials = { "maxRange", "timestampsMaxStandardDeviation", "timestampsUseAtLeast" };

		for (String specialKey : specials) {

			if (properties.containsKey(specialKey)) {

				String specialValue = properties.getProperty(specialKey, "");

				constraints.setProperty(specialKey, specialValue);

				properties.remove(specialKey);
			}
		}

		// move the exclusions out of the properties

		for (Object k : properties.keySet()) {

			Object v = properties.get(k);

			if ("".equals(v)) {

				String key = ((String) k).replace('_', ' ');

				exclusions.add((String) key);
			}
		}

		for (String key : exclusions) {

			properties.remove(key.replace(' ', '_'));
		}

		return properties;
	}

}
