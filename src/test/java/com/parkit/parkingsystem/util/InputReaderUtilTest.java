package com.parkit.parkingsystem.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InputReaderUtilTest {

    @Test
    public void should_be_returned_an_integer_when_i_entered_an_integer_in_input_with_readSelection() {
        InputReaderUtil inputOutput = new InputReaderUtil();

        String input = "1";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        assertEquals(1, inputOutput.readSelection());
    }

    @Test
    public void should_be_fail_when_i_entered_a_alpha_in_readSelection() {
        InputReaderUtil inputOutput = new InputReaderUtil();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String input = "A";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        assertEquals(-1, inputOutput.readSelection());
    }

    @Test
    public void should_be_returned_ABC_when_i_entered_ABC_in_input_with_readVehicleRegistrationNumber() {
        InputReaderUtil inputOutput = new InputReaderUtil();

        String input = "ABCD";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        assertEquals("ABCD", inputOutput.readVehicleRegistrationNumber());
    }

    @Test
    public void should_be_fail_when_input_is_empty_with_readVehicleRegistrationNumber() {
        InputReaderUtil inputOutput = new InputReaderUtil();

        String input = " ";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> inputOutput.readVehicleRegistrationNumber());

        assertEquals(thrown.getMessage(), "Invalid input provided");
    }

    @Test
    public void should_be_cloned_when_i_used_clone() throws CloneNotSupportedException {
        InputReaderUtil inputOutput = new InputReaderUtil();
        assertTrue( inputOutput.clone() instanceof InputReaderUtil);
    }


}
