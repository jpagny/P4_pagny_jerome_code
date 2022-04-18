package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParkingSpotDAOTest {

    private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

    public static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static Connection connection;
    private static ParkingSpotDAO parkingSpotDAO;

    @BeforeAll
    public static void setup(){
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
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
    public void should_get_next_available_slot_When_there_is_a_parking_available_slot(){
        int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertEquals(result,1);
    }

    @Test
    public void should_be_0_When_parking_type_is_not_available(){
        setParkingSpotNoAvailable();
        int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertEquals(result,0);
    }

    @Test
    public void should_be_updated_When_method_update_is_called(){
        ParkingSpot parkingSpot = new ParkingSpot(1, null, true);
        assertTrue(parkingSpotDAO.updateParking(parkingSpot));
    }


    private void setParkingSpotNoAvailable(){
        try {
            connection = dataBaseTestConfig.getConnection();
            connection.prepareStatement("update parking set available = false").execute();
        } catch (Exception ex) {
            logger.error("Error connecting to data base", ex);
        } finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }

}
