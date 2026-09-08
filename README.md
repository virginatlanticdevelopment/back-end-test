# Virgin Atlantic ~ Flight Information Display

Here is an example of a flight times application for Virgin Atlantic. This project represents what we're looking for in
a candidate and our current technology choices.

Please spend up to 2 hours improving this application. What you improve is up to you: Perhaps it needs more tests,
additional functionality, or some bug fixing.

## Rules

1) The code must be your own work. If you have a strong case to use a small code snippet of someone else's work, e.g. a
boilerplate function, it must be clearly commented and attributed to the original author.
2) The flight data cannot be changed, and must be loaded from the CSV file, so it can easily be replaced with another file.
3) You must include any unit tests you think are appropriate.
4) You are not allowed to add any additional dependencies to the project - make use of what's been provided.
5) Identify intentional gaps in the code by looking for `//FIXME - applicant to complete` and provide either solutions or improvements.

## What it should do
The application should allow the user to select or input any date, of any year, resulting in the display of flights on
that day, displayed in chronological order -- a Flight Information Display.

## Requirements

* Java 16 (the `maven-compiler-plugin` is configured for `source`/`target` 16 - use a matching JDK).
* Maven 3.6+ (or use the Maven Wrapper if one is added to your fork).

## Building and running

Build and run the unit tests:

```
mvn clean test
```

Run the full build (compiles, tests, and packages the jar):

```
mvn clean install
```

Run the application locally with Spring Boot:

```
mvn spring-boot:run
```

The application starts on port `8080` by default, with the context path `/back-end-test` (see
[`application.properties`](./src/main/resources/application.properties)).

## Endpoint

```
GET /back-end-test/{date}/results
```

Where `{date}` is an ISO-8601 formatted date (`yyyy-MM-dd`), e.g. `2026-09-08`. The endpoint filters the flights
that operate on the day of week of the supplied date, and returns them sorted chronologically by departure time.

* `200 OK` - one or more flights operate on that day of the week; the flights are returned in the response body.
* `204 No Content` - no flights operate on that day of the week.
* `400 Bad Request` - the supplied `{date}` could not be parsed as an ISO-8601 date.

### Example request

`2026-09-08` is a Tuesday.

```
GET http://localhost:8080/back-end-test/2026-09-08/results
```

### Example response

```json
[
    {
        "departureTime": "09:00",
        "destination": "Antigua",
        "iata": "ANU",
        "flightNo": "VS033",
        "days": ["TUESDAY"]
    },
    {
        "departureTime": "10:15",
        "destination": "Havana",
        "iata": "HAV",
        "flightNo": "VS063",
        "days": ["TUESDAY"]
    },
    {
        "departureTime": "10:35",
        "destination": "Las Vegas",
        "iata": "LAS",
        "flightNo": "VS043",
        "days": ["MONDAY", "TUESDAY", "WEDNESDAY"]
    },
    {
        "departureTime": "11:05",
        "destination": "Barbados",
        "iata": "BGI",
        "flightNo": "VS029",
        "days": ["SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"]
    },
    {
        "departureTime": "11:45",
        "destination": "Orlando",
        "iata": "MCO",
        "flightNo": "VS027",
        "days": ["SUNDAY", "TUESDAY"]
    },
    {
        "departureTime": "12:20",
        "destination": "Cancun",
        "iata": "CUN",
        "flightNo": "VS093",
        "days": ["TUESDAY"]
    },
    {
        "departureTime": "13:00",
        "destination": "Orlando",
        "iata": "MCO",
        "flightNo": "VS015",
        "days": ["SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"]
    },
    {
        "departureTime": "15:35",
        "destination": "Las Vegas",
        "iata": "LAS",
        "flightNo": "VS044",
        "days": ["SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"]
    }
]
```

### Example - no flights / invalid date

```
GET http://localhost:8080/back-end-test/not-a-date/results
```

returns `400 Bad Request`.

## Supplying your code
Please **create and commit your code into a public Github repository** and supply the link to the recruiter for review.  Your code should compile and run in one step.

## Supporting Data
The [flight data](./src/main/resources/flights.csv) is a simple comma-separated file containing the following:

| Departure Time | Destination | Destination Airport IATA | Flight No | Sun | Mon | Tue | Wed | Thu | Fri | Sat
| :--- | :--- | :--- | :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| 09:00 | Antigua | ANU | VS033 |  |  | `x` |  |  |  | 
| 10:00 | Antigua | ANU | VS033 |  |  |  |  | `x` |  | `x`
| 11:05 | Barbados | BGI | VS029 | `x` | `x` | `x` | `x` | `x` | `x` | `x`
| 12:20 | Cancun | CUN | VS093 |  |  | `x` |  |  |  | 
| 09:00 | Grenada | GND | VS089 |  | `x` |  |  |  |  | 
| 10:10 | Grenada | GND | VS089 |  |  |  |  |  | `x` | 
| 10:15 | Havana | HAV | VS063 |  |  | `x` |  |  |  | 
| 10:15 | Havana | HAV | VS063 |  | `x` |  |  | `x` |  | 
| 10:15 | Las Vegas | LAS | VS043 | `x` |  |  |  |  | `x` | `x`
| 10:25 | Las Vegas | LAS | VS043 |  |  |  |  | `x` |  | 
| 10:35 | Las Vegas | LAS | VS043 |  | `x` | `x` | `x` |  |  | 
| 15:35 | Las Vegas | LAS | VS044 | `x` | `x` | `x` | `x` | `x` | `x` | `x`
| 12:25  | Montego Bay | MBJ | VS065 |  |  |  | `x` |  |  | 
| 12:40 | Montego Bay | MBJ | VS065 | `x` |  |  |  |  |  | 
| 10:10 | Orlando | MCO | VS049 | `x` |  |  |  |  |  | 
| 10:15 | Orlando | MCO | VS027 |  |  |  | `x` |  |  | 
| 11:00 | Orlando | MCO | VS027 |  | `x` |  |  |  |  | 
| 11:10 | Orlando | MCO | VS049 |  | `x` |  |  |  |  | 
| 11:20 | Orlando | MCO | VS027 |  |  |  |  |  | `x` | `x`
| 11:35 | Orlando | MCO | VS027 |  |  |  |  | `x` |  | 
| 11:45 | Orlando | MCO | VS027 | `x` |  | `x` |  |  |  | 
| 11:45 | Orlando | MCO | VS049 |  |  |  | `x` |  |  | 
| 13:00 | Orlando | MCO | VS015 | `x` | `x` | `x` | `x` | `x` | `x` | `x`
| 09:00 | St Lucia | UVF | VS089 |  | `x` |  |  |  |  | 
| 09:00 | St Lucia | UVF | VS097 | `x` |  |  |  |  |  | 
| 10:10 | St Lucia | UVF | VS089 |  |  |  |  |  | `x` | 
| 09:00 | Tobago | TAB | VS097 | `x` |  |  |  |  |  |

The ``x`` denotes days that the flight operates. 

