package ru.itmo.general.utility;

import lombok.Getter;

import java.util.Scanner;

/**
 * Manages user input mode.
 * Controls whether input is from the console or a file.
 *
 * @author zevtos
 */
public class Interrogator {

    /**
     * -- GETTER --
     * Retrieves the scanner for user input.
     *
     * @return The scanner for user input.
     */
    @Getter
    private static Scanner userScanner;

    private static boolean fileMode = false;

    /**
     * Sets the scanner for user input.
     *
     * @param userScanner The scanner for user input.
     */
    public static void setUserScanner(Scanner userScanner) {
        Interrogator.userScanner = userScanner;
    }

    /**
     * Checks if the program is in file input mode.
     *
     * @return true if the program is in file input mode, otherwise false.
     */
    public static boolean fileMode() {
        return fileMode;
    }

    /**
     * Sets user input mode.
     */
    public static void setUserMode() {
        Interrogator.fileMode = false;
    }

    /**
     * Sets file input mode.
     */
    public static void setFileMode() {
        Interrogator.fileMode = true;
    }
}
