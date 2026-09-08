package com.virginholidays.backend.test.resource;

import com.virginholidays.backend.test.api.Flight;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Full-stack integration tests for the flight results endpoint, exercised against the real
 * {@code flights.csv} supplied with the application (loaded via the real Spring context).
 *
 * @author Geoff Perks
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FlightInfoResourceIntegrationTest {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    private String url(String path) {
        return "http://localhost:" + port + "/back-end-test" + path;
    }

    @Test
    public void testGetResults_returnsFlightsForTuesdaySortedChronologically() {

        // 2026-09-08 is a Tuesday
        ResponseEntity<List<Flight>> response = restTemplate.exchange(
                url("/2026-09-08/results"),
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() { });

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        List<Flight> flights = response.getBody();

        assertThat(flights, equalTo(List.of(
                new Flight(LocalTime.of(9, 0), "Antigua", "ANU", "VS033", List.of(DayOfWeek.TUESDAY)),
                new Flight(LocalTime.of(10, 15), "Havana", "HAV", "VS063", List.of(DayOfWeek.TUESDAY)),
                new Flight(LocalTime.of(10, 35), "Las Vegas", "LAS", "VS043",
                        List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY)),
                new Flight(LocalTime.of(11, 5), "Barbados", "BGI", "VS029",
                        List.of(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY)),
                new Flight(LocalTime.of(11, 45), "Orlando", "MCO", "VS027",
                        List.of(DayOfWeek.SUNDAY, DayOfWeek.TUESDAY)),
                new Flight(LocalTime.of(12, 20), "Cancun", "CUN", "VS093", List.of(DayOfWeek.TUESDAY)),
                new Flight(LocalTime.of(13, 0), "Orlando", "MCO", "VS015",
                        List.of(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY)),
                new Flight(LocalTime.of(15, 35), "Las Vegas", "LAS", "VS044",
                        List.of(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY))
        )));
    }

    @Test
    public void testGetResults_returnsBadRequestForInvalidDate() {

        ResponseEntity<String> response = restTemplate.getForEntity(url("/not-a-date/results"), String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }
}
