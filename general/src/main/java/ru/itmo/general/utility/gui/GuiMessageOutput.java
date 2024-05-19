package ru.itmo.general.utility.gui;

import ru.itmo.general.utility.MessageOutput;

import javax.swing.*;

/**
 * The {@code GuiMessageOutput} class provides an implementation of the {@code MessageOutput} interface
 * for outputting messages to a GUI component, such as a JTextArea.
 */
public class GuiMessageOutput implements MessageOutput {
    private final JTextArea textArea;

    public GuiMessageOutput(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void print(String message) {
        textArea.append(message);
    }

    @Override
    public void println(String message) {
        textArea.append(message + "\n");
    }

    @Override
    public void printError(String errorMessage) {
        textArea.append("ERROR: " + errorMessage + "\n");
    }

    public void printTable(String name, String description) {
        textArea.append("<table border=\"1\">\n");
        textArea.append("<tr>\n");
        textArea.append("<th>" + name + "</th>\n");
        textArea.append("<th>" + description + "</th>\n");
        textArea.append("</tr>\n");
        textArea.append("</table>\n");
    }
}
