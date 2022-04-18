package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.*;

public class TicketDAOTest {

    private static final Logger logger = LogManager.getLogger("TicketDAOTest");

    public static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static Connection connection;
    private static TicketDAO ticketDAO;

    @BeforeAll
    public static void setup(){
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
    }

    @BeforeEach
    public void setUpPerTest() {
        try {
            connection = dataBaseTestConfig.getConnection();
            connection.prepareStatement("update parking set available = true").execute();
            connection.prepareStatement("truncate table ticket").execute();
        } catch (Exception ex) {
            logger.error("Error connecting to data base", ex);
        } finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }

    @Test
    public void should_be_saved_When_method_save_is_called() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
        String inTime = "2022-04-18 10:00:00.0";
        String outTime = "2022-04-18 11:00:00.0";
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setInTime(sdf.parse(inTime));
        ticket.setOutTime(sdf.parse(outTime));
        ticket.setParkingSpot(parkingSpot);

        assertTrue(ticketDAO.saveTicket(ticket));
    }

    @Test
    public void should_be_throw_When_ticket_had_bad_datetime(){
        try {
            Ticket ticket = new Ticket();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
            String time = "2022-04-18 10:00:00.0";
            ticket.setInTime(sdf.parse(time));
            ticket.setOutTime(sdf.parse(time));

            ticketDAO.saveTicket(ticket);
        } catch (Exception exception){
            assertTrue(exception instanceof IllegalArgumentException);
            assertTrue(exception.getMessage().contains("Error fetching next available slot"));
        }
    }

    @Test
    public void should_be_saved_When_outTime_is_empty() throws ParseException {
        Ticket ticket = new Ticket();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
        String inTime = "2022-04-18 10:00:00.0";
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setInTime(sdf.parse(inTime));
        ticket.setOutTime(null);
        ticket.setParkingSpot(parkingSpot);

        assertTrue(ticketDAO.saveTicket(ticket));
    }

    @Test public void should_have_Ticket_When_get_ticket_with_vehicle_reg_number() throws ParseException {
        Ticket ticket = new Ticket();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
        String inTime = "2022-04-18 10:00:00.0";
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setInTime(sdf.parse(inTime));
        ticket.setOutTime(null);
        ticket.setParkingSpot(parkingSpot);

        ticketDAO.saveTicket(ticket);

        ticket = ticketDAO.getTicket("ABCDEF");

        assertNotNull(ticket);
        assertEquals(ticket.getInTime().toString(),"2022-04-18 10:00:00.0");
        assertEquals(ticket.getVehicleRegNumber(),"ABCDEF");
    }

    @Test
    public void should_be_updated_When_method_updateTicket_is_called() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
        String inTime = "2022-04-18 10:00:00.0";
        String outTime = "2022-04-18 11:00:00.0";
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setInTime(sdf.parse(inTime));
        ticket.setParkingSpot(parkingSpot);

        ticketDAO.saveTicket(ticket);

        ticket = ticketDAO.getTicket("ABCDEF");

        assertEquals(ticket.getPrice(),0);

        ticket.setOutTime(sdf.parse(outTime));
        ticket.setPrice(1.5);

        assertTrue(ticketDAO.updateTicket(ticket));
    }

    @Test
    public void should_be_0_when_there_is_not_recurring_vehicle() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
        String inTime = "2022-04-18 10:00:00.0";
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setInTime(sdf.parse(inTime));
        ticket.setParkingSpot(parkingSpot);

        ticketDAO.saveTicket(ticket);

        int result = ticketDAO.countRecurringVehicle("ABCDEF");

        assertEquals(result,0);
    }

    @Test
    public void should_be_1_when_there_is_1_recurring_vehicle() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
        String inTime = "2022-04-18 10:00:00.0";
        String outTIme = "2022-04-18 11:00:00.0";
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setInTime(sdf.parse(inTime));
        ticket.setOutTime(sdf.parse(outTIme));
        ticket.setParkingSpot(parkingSpot);

        ticketDAO.saveTicket(ticket);

        inTime = "2022-04-18 12:00:00.0";
        outTIme = "2022-04-18 13:00:00.0";

        ticket.setInTime(sdf.parse(inTime));
        ticket.setOutTime(sdf.parse(outTIme));

        ticketDAO.saveTicket(ticket);

        int result = ticketDAO.countRecurringVehicle("ABCDEF");

        assertEquals(result,2);
    }


}
