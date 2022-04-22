package com.parkit.parkingsystem.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InputReaderUtilTest {

    @Mock
    Scanner scan;

    private static InputReaderUtil input;

    @BeforeEach
    private void setUp() {
        input = new InputReaderUtil(scan);
    }

    @Test
    public void should_be_returned_an_integer_when_i_entered_an_integer_in_input_with_readSelection() {
        when(scan.nextLine()).thenReturn("1");

        int result = input.readSelection();
        assertEquals(result,1);
    }

    @Test
    public void should_be_fail_when_i_entered_a_alpha_in_readSelection(){
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        when(scan.nextLine()).thenReturn("ABC");
        int result = input.readSelection();
        assertEquals(-1,result);
        assertTrue(outContent.toString().contains("Error reading input. Please enter valid number for proceeding further"));
    }

    @Test
    public void should_be_returned_ABC_when_i_entered_ABC_in_input_with_readVehicleRegistrationNumber() {
        when(scan.nextLine()).thenReturn("ABC");
        String result = input.readVehicleRegistrationNumber();
        assertEquals(result,"ABC");
    }

    @Test
    public void should_be_fail_when_input_is_empty_with_readVehicleRegistrationNumber() {
        when(scan.nextLine()).thenReturn("");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,()-> input.readVehicleRegistrationNumber());

        assertEquals(thrown.getMessage(),"Invalid input provided");
    }

    @Test
    public void should_be_fail_when_input_is_null_with_readVehicleRegistrationNumber() {
        when(scan.nextLine()).thenReturn(null);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,()-> input.readVehicleRegistrationNumber());

        assertEquals(thrown.getMessage(),"Invalid input provided");
    }


}
