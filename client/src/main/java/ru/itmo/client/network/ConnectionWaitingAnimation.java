package ru.itmo.client.network;

import ru.itmo.general.utility.console.Console;

public class ConnectionWaitingAnimation extends Thread {
    private final Console console;
    private volatile boolean running;
    private int dotsCount;
    private int direction; // 1 - растет, -1 - убывает

    public ConnectionWaitingAnimation(Console console) {
        this.console = console;
    }

    @Override
    public void run() {
        running = true;
        dotsCount = 0;
        direction = 1; // 1 - растет, -1 - убывает
        try {
            while (running) {
                for (int i = 0; i < 3; i++) {
                    animate();
                    Thread.sleep(1000); // Подождать 1 секунду перед изменением
                }
                for (int i = 2; i >= 0; i--) {
                    animate();
                    Thread.sleep(1000); // Подождать 1 секунду перед изменением
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private void animate() {
        StringBuilder animation = new StringBuilder("Ожидание подключения");
        for (int j = 0; j < dotsCount; j++) {
            animation.append(".");
        }
        console.print("\r" + animation.toString());
        dotsCount += direction;
        if (dotsCount == 3 || dotsCount == 0) {
            direction *= -1; // Изменить направление, если достигнуто максимальное или минимальное количество точек
        }
    }

    public void stopAnimation() {
        running = false;
    }
}
