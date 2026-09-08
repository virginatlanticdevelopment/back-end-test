package com.virginholidays.backend.test.service;

import com.virginholidays.backend.test.api.Flight;
import com.virginholidays.backend.test.repository.FlightInfoRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
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
    private FlightInfoServiceImpl flightInfoService;

    private Flight tuesdayLate;
    private Flight tuesdayEarly;
    private Flight wednesdayOnly;

    @BeforeEach
    public void beforeEach() {

        tuesdayLate = new Flight(LocalTime.of(12, 20), "Cancun", "CUN", "VS093", List.of(DayOfWeek.TUESDAY));
        tuesdayEarly = new Flight(LocalTime.of(9, 0), "Antigua", "ANU", "VS033", List.of(DayOfWeek.TUESDAY));
        wednesdayOnly = new Flight(LocalTime.of(12, 25), "Montego Bay", "MBJ", "VS065", List.of(DayOfWeek.WEDNESDAY));

        when(flightInfoRepository.findAll())
                .thenReturn(completedFuture(Optional.of(List.of(tuesdayLate, tuesdayEarly, wednesdayOnly))));
    }

    @Test
    public void testFindFlightByDate_filtersByDayOfWeekAndSortsChronologically() throws ExecutionException, InterruptedException {

        // 2026-09-08 is a Tuesday
        LocalDate tuesday = LocalDate.of(2026, 9, 8);

        Optional<List<Flight>> maybeFlights = flightInfoService
                .findFlightByDate(tuesday)
                .toCompletableFuture()
                .get();

        assertThat(maybeFlights.isPresent(), equalTo(true));
        assertThat(maybeFlights.get().size(), equalTo(2));

        // sorted chronologically - the 09:00 flight must come before the 12:20 flight
        assertThat(maybeFlights.get().get(0), equalTo(tuesdayEarly));
        assertThat(maybeFlights.get().get(1), equalTo(tuesdayLate));
    }

    @Test
    public void testFindFlightByDate_noFlightsForDay() throws ExecutionException, InterruptedException {

        // 2026-09-10 is a Thursday - no flights operate that day in the mocked data
        LocalDate thursday = LocalDate.of(2026, 9, 10);

        Optional<List<Flight>> maybeFlights = flightInfoService
                .findFlightByDate(thursday)
                .toCompletableFuture()
                .get();

        assertThat(maybeFlights.isPresent(), equalTo(true));
        assertThat(maybeFlights.get().isEmpty(), equalTo(true));
    }

    @Test
    public void testFindFlightByDate_flightOperatingOnMultipleDaysIsIncluded() throws ExecutionException, InterruptedException {

        Flight everyDayFlight = new Flight(LocalTime.of(15, 35), "Las Vegas", "LAS", "VS044",
                List.of(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY));

        when(flightInfoRepository.findAll())
                .thenReturn(completedFuture(Optional.of(List.of(tuesdayLate, tuesdayEarly, wednesdayOnly, everyDayFlight))));

        // 2026-09-08 is a Tuesday
        LocalDate tuesday = LocalDate.of(2026, 9, 8);

        Optional<List<Flight>> maybeFlights = flightInfoService
                .findFlightByDate(tuesday)
                .toCompletableFuture()
                .get();

        assertThat(maybeFlights.isPresent(), equalTo(true));
        assertThat(maybeFlights.get().size(), equalTo(3));

        // chronological order: 09:00, 12:20, 15:35
        assertThat(maybeFlights.get().get(0), equalTo(tuesdayEarly));
        assertThat(maybeFlights.get().get(1), equalTo(tuesdayLate));
        assertThat(maybeFlights.get().get(2), equalTo(everyDayFlight));
    }

    @Test
    public void testFindFlightByDate_multipleFlightsAreSortedChronologically() throws ExecutionException, InterruptedException {

        Flight first = new Flight(LocalTime.of(8, 0), "A", "AAA", "VS001", List.of(DayOfWeek.WEDNESDAY));
        Flight second = new Flight(LocalTime.of(10, 30), "B", "BBB", "VS002", List.of(DayOfWeek.WEDNESDAY));
        Flight third = new Flight(LocalTime.of(23, 59), "C", "CCC", "VS003", List.of(DayOfWeek.WEDNESDAY));

        // deliberately supplied out of order
        when(flightInfoRepository.findAll())
                .thenReturn(completedFuture(Optional.of(List.of(third, first, second))));

        LocalDate wednesday = LocalDate.of(2026, 9, 9);

        Optional<List<Flight>> maybeFlights = flightInfoService
                .findFlightByDate(wednesday)
                .toCompletableFuture()
                .get();

        assertThat(maybeFlights.isPresent(), equalTo(true));
        assertThat(maybeFlights.get(), equalTo(List.of(first, second, third)));
    }

    @Test
    public void testFindFlightByDate_repositoryReturnsEmpty() throws ExecutionException, InterruptedException {

        when(flightInfoRepository.findAll()).thenReturn(completedFuture(Optional.empty()));

        LocalDate tuesday = LocalDate.of(2026, 9, 8);

        Optional<List<Flight>> maybeFlights = flightInfoService
                .findFlightByDate(tuesday)
                .toCompletableFuture()
                .get();

        assertThat(maybeFlights.isPresent(), equalTo(false));
    }
}
