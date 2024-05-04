package ru.itmo.client.network;

import ru.itmo.general.utility.console.Console;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ConnectionMonitor extends Thread {
    private TCPClient tcpClient;
    private Console console;
    private boolean connect_flag = false;

    public ConnectionMonitor(TCPClient tcpClient, Console console) {
        this.tcpClient = tcpClient;
        this.console = console;
    }

    @Override
    public void run() {
        if (!tcpClient.isConnected()) {
            boolean connected = false;
            try {
                connected = tcpClient.connect();
            } catch (TimeoutException e) {
                console.printError(getClass(),"Тайм-аут при подключении к серверу");
            }
            if (connected) {
                connect_flag = true;
                console.println("Подключение установлено.");
            } else {
                connect_flag = false;
                console.printError(getClass(), "Соединение с сервером не установлено");
                repairConnection();
            }
        }
        while (true) {
            try {
                if (!tcpClient.isConnected()) {
                    connect_flag = false;
                    console.printError(getClass(), "Потеряно соединение с сервером. Попытка восстановления...");
                    repairConnection();
                }
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                console.printError(getClass(), "Ошибка в приостановлении потока");
            }
        }
    }

    public void repairConnection() {
        while (true) {
            try {
                tcpClient.connect();
                if (tcpClient.isConnected()) {
                    connect_flag = true;
                    console.println("Соединение с сервером восстановлено.");
                    break;
                }
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                connect_flag = false;
                console.printError(getClass(), "Ошибка при восстановлении соединения: " + e.getMessage());
            } catch (TimeoutException e) {
                if(connect_flag){
                    console.printError(getClass(), "Тайм-аут при подключении к серверу");
                }
            }
        }
    }
}