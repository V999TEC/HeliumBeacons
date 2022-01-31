# Helium
Analyse beacons by geographical area

## Download
https://github.com/V999TEC/HeliumBeacons/blob/main/download/Helium-0.0.1-SNAPSHOT-jar-with-dependencies.jar

## Parameters

Time should be constrained by specifying one or two zulu time(s) in the format YYYY-MM-DDThh:mm:ss.123456789Z

Specify a single time to imply data "after"

Specify a range of times by a pair of zulu times i.e., "after" and "before"

Times must always be the last parameters. Preceed times by...

specifing a city or area (known to Helium)

or define a circular area of known radius

lat=latitude long=longitude distance=metres (time (time))

or define a rectangle using south-west and north-east coordinates

swlat=50.56734 swlon=-1.58319 nelat=50.76640 nelon=-1.04253

or a Helium 'hex' identifier (typcally a hexagonal zone about 1km across )

a single hex identifier

or one or more three-word-addresses

three-word-address (further three-word-addresses)

## Main class:
uk.co.myzen.atoz.helium.Explore

## Project Dependency
https://github.com/V999TEC/Utility


## jar containing all dependencies 
Helium-0.0.1-SNAPSHOT-jar-with-dependencies.jar

## command line execution
java -jar Helium-0.0.1-SNAPSHOT-jar-with-dependencies.jar [parameters]

java -jar Helium-0.0.1-SNAPSHOT-jar-with-dependencies.jar 881958a095fffff

### More example parameters

lat=51.52304 long=-0.17050 distance=3750  2022-01-25T11:00:00.000000000Z 2022-01-26T13:00:00.000000000Z

Swindon

long-felt-grasshopper

881958a717fffff 2022-01-31T08:00:00.000000000Z

### Example extract of output:

Chronological summary of sent Beacons between 2022-01-31 08:00:00 and 2022-01-31 15:04:21

```
2022-01-31T09:03:35.122836736Z is the mean beacon time sent from stale-coral-beaver/Swindon witnessed by 1 on channel 3 (867.7)
2022-01-31T10:52:39.167652608Z is the mean beacon time sent from icy-pebble-capybara/Wroughton witnessed by 14 on channel 6 (868.3)
2022-01-31T10:53:54.043644672Z is the mean beacon time sent from refined-garnet-lion/Westlea witnessed by 1 on channel 4 (867.9)
2022-01-31T11:32:56.492972032Z is the mean beacon time sent from rapid-licorice-tadpole/Swindon witnessed by 1 on channel 5 (868.1)
2022-01-31T12:03:10.326817024Z is the mean beacon time sent from rural-zinc-crocodile/Swindon witnessed by 1 on channel 7 (868.5)
2022-01-31T12:43:06.673033472Z is the mean beacon time sent from radiant-tin-goblin/Freshbrook witnessed by 1 on channel 7 (868.5)
2022-01-31T12:43:53.230298368Z is the mean beacon time sent from helpful-iris-moth/Swindon witnessed by 1 on channel 3 (867.7)
2022-01-31T13:24:15.640197120Z is the mean beacon time sent from dancing-grape-toad/Swindon witnessed by 1 on channel 1 (867.3)
```


## RecentAddresses.properties
If the jar runs to completion it will display RecentAddresses.properties created in the current directory

This property file acts as a cache to speed up execution on subsequent invocations

