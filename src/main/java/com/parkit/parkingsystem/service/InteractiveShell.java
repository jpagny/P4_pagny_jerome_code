package com.parkit.parkingsystem.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

public class InteractiveShell {

    private static final Logger logger = LogManager.getLogger("InteractiveShell");

    private ParkingService parkingService;

    public InteractiveShell(ParkingService parkingService){
        this.parkingService = parkingService;
;    }

    public void loadInterface() throws CloneNotSupportedException {
        logger.info("App initialized!!!");
        System.out.println("Welcome to Parking System!");

        boolean continueApp = true;

        while (continueApp) {
            loadMenu();
            int option = parkingService.getInputReaderUtil().readSelection();
            switch (option) {
                case 1: {
                    parkingService.processIncomingVehicle(LocalDateTime.now());
                    break;
                }
                case 2: {
                    parkingService.processExitingVehicle(LocalDateTime.now());
                    break;
                }
                case 3: {
                    System.out.println("Exiting from the system!");
                    continueApp = false;
                    break;
                }
                default:
                    System.out.println("Unsupported option. Please enter a number corresponding to the provided menu");
            }
        }
    }

    private void loadMenu() {
        System.out.println("Please select an option. Simply enter the number to choose an action");
        System.out.println("1 New Vehicle Entering - Allocate Parking Space");
        System.out.println("2 Vehicle Exiting - Generate Ticket Price");
        System.out.println("3 Shutdown System");
    }

}
