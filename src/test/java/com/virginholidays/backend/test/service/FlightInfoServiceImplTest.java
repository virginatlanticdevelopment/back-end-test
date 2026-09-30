package com.virginholidays.backend.test.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.virginholidays.backend.test.api.Flight;
import com.virginholidays.backend.test.repository.FlightInfoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
/**
 * The FlightInfoServiceImpl unit tests
 *
 * @author Geoff Perks
 */
@ExtendWith(MockitoExtension.class)
class FlightInfoServiceImplTest {

    @Mock
    private FlightInfoRepository flightInfoRepository;

    @InjectMocks
    private FlightInfoServiceImpl service;

    @Test
    void shouldReturnFlightsForRequestedDaySortedByDepartureTime() {
        Flight lateFlight = new Flight(
                LocalTime.of(15, 35),
                "Las Vegas",
                "LAS",
                "VS044",
                List.of(DayOfWeek.WEDNESDAY)
        );

        Flight earlyFlight = new Flight(
                LocalTime.of(10, 35),
                "Las Vegas",
                "LAS",
                "VS043",
                List.of(DayOfWeek.WEDNESDAY)
        );

        Flight differentDayFlight = new Flight(
                LocalTime.of(9, 0),
                "Antigua",
                "ANU",
                "VS033",
                List.of(DayOfWeek.TUESDAY)
        );

        when(flightInfoRepository.findAll())
                .thenReturn(CompletableFuture.completedFuture(
                        Optional.of(List.of(
                                lateFlight,
                                differentDayFlight,
                                earlyFlight
                        ))
                ));

        Optional<List<Flight>> result =
                service.findFlightByDate(LocalDate.of(2026, 9, 30))
                        .toCompletableFuture()
                        .join();

        assertTrue(result.isPresent());

        List<Flight> flights = result.get();

        assertEquals(2, flights.size());

        assertEquals("VS043", flights.get(0).flightNo());
        assertEquals("VS044", flights.get(1).flightNo());

        assertEquals(LocalTime.of(10, 35),
                flights.get(0).departureTime());

        assertEquals(LocalTime.of(15, 35),
                flights.get(1).departureTime());
    }
}
