package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InteractiveShellTest {

    public static InteractiveShell shell;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @Spy
    private static ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

    @BeforeEach
    private void setUp() {
        shell = new InteractiveShell(inputReaderUtil, parkingService);
    }

    @Test
    public void should_call_processIncomingVehicle_when_menu_1_is_choose() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        doThrow(new IllegalArgumentException()).when(parkingService).processIncomingVehicle(any(LocalDateTime.class));

        assertThrows(IllegalArgumentException.class,()->shell.loadInterface());
        verify(parkingService,times(1)).processIncomingVehicle(any(LocalDateTime.class));
    }

    @Test
    public void should_call_processExitingVehicle_when_menu_2_is_choose() {
        when(inputReaderUtil.readSelection()).thenReturn(2);
        doThrow(new IllegalArgumentException()).when(parkingService).processExitingVehicle(any(LocalDateTime.class));

        assertThrows(IllegalArgumentException.class,()->shell.loadInterface());
        verify(parkingService,times(1)).processExitingVehicle(any(LocalDateTime.class));
    }

    @Test
    public void should_be_stopped_when_menu_3_is_choose() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        when(inputReaderUtil.readSelection()).thenReturn(3);

        shell.loadInterface();

        assertTrue(outContent.toString().contains("Exiting from the system!"));
    }

}
