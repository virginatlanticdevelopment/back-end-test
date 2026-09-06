package com.virginholidays.backend.test.resource;

import com.virginholidays.backend.test.api.Flight;
import com.virginholidays.backend.test.service.FlightInfoService;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * The FlightInfoResource unit tests
 *
 * @author Geoff Perks
 */
@ExtendWith(MockitoExtension.class)
class FlightInfoResourceTest {

    @Mock
    private FlightInfoService flightInfoService;

    @InjectMocks
    private FlightInfoResource flightInfoResource;

    @Test
    public void testGetResults_returnsOkWithFlightsForValidDate() throws ExecutionException, InterruptedException {

        Flight flight = new Flight(LocalTime.of(11, 5), "Barbados", "BGI", "VS029", List.of(DayOfWeek.TUESDAY));

        when(flightInfoService.findFlightByDate(any(LocalDate.class)))
                .thenReturn(completedFuture(Optional.of(List.of(flight))));

        ResponseEntity<?> response = flightInfoResource
                .getResults("2026-09-08")
                .toCompletableFuture()
                .get();

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody(), equalTo(List.of(flight)));

        // ensure the supplied date is used, not LocalDate.now()
        ArgumentCaptor<LocalDate> captor = ArgumentCaptor.forClass(LocalDate.class);
        verify(flightInfoService).findFlightByDate(captor.capture());
        assertThat(captor.getValue(), equalTo(LocalDate.of(2026, 9, 8)));
    }

    @Test
    public void testGetResults_returnsNoContentWhenNoFlights() throws ExecutionException, InterruptedException {

        when(flightInfoService.findFlightByDate(any(LocalDate.class)))
                .thenReturn(completedFuture(Optional.of(List.of())));

        ResponseEntity<?> response = flightInfoResource
                .getResults("2026-09-08")
                .toCompletableFuture()
                .get();

        assertThat(response.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
    }

    @Test
    public void testGetResults_returnsNoContentWhenServiceReturnsEmptyOptional() throws ExecutionException, InterruptedException {

        when(flightInfoService.findFlightByDate(any(LocalDate.class)))
                .thenReturn(completedFuture(Optional.empty()));

        ResponseEntity<?> response = flightInfoResource
                .getResults("2026-09-08")
                .toCompletableFuture()
                .get();

        assertThat(response.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
    }

    @Test
    public void testGetResults_returnsBadRequestForInvalidDate() throws ExecutionException, InterruptedException {

        ResponseEntity<?> response = flightInfoResource
                .getResults("not-a-date")
                .toCompletableFuture()
                .get();

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testGetResults_returnsBadRequestForWrongDateSeparator() throws ExecutionException, InterruptedException {

        ResponseEntity<?> response = flightInfoResource
                .getResults("2026/09/08")
                .toCompletableFuture()
                .get();

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testGetResults_returnsBadRequestForOutOfRangeMonth() throws ExecutionException, InterruptedException {

        ResponseEntity<?> response = flightInfoResource
                .getResults("2026-13-01")
                .toCompletableFuture()
                .get();

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testGetResults_returnsBadRequestForNonExistentLeapDay() throws ExecutionException, InterruptedException {

        // 2027 is not a leap year, so Feb 29th does not exist
        ResponseEntity<?> response = flightInfoResource
                .getResults("2027-02-29")
                .toCompletableFuture()
                .get();

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }
}
