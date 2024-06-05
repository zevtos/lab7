package ru.itmo.client.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import ru.itmo.client.MainApp;
import ru.itmo.client.utility.runtime.Runner;
import ru.itmo.general.models.Ticket;

import java.util.*;

public class DataVisualizationController {
    private MainApp mainApp;
    private Runner runner;
    private MainController mainController;

    @FXML
    private Canvas canvas;

    private List<Ticket> routes;
    private List<CircleAnimation> circles;
    private Map<Integer, Color> userColors;
    private Random random;
    private double maxX;
    private float maxY;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        routes = new ArrayList<>();
        circles = new ArrayList<>();
        userColors = new HashMap<>();
        random = new Random();
        maxX = 0;
        maxY = 0;

        // Запуск анимации
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> draw()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        canvas.setOnMouseClicked(this::handleCanvasClick);
    }

    public void initializeRoutes(List<Ticket> routes) {
        this.routes.clear();
        this.circles.clear();
        updateMaxCoordinates(routes);

        for (Ticket ticket : routes) {
            addTicket(ticket);
        }
        refreshCircles();
    }

    public void addTicket(Ticket ticket) {
        routes.add(ticket);
        if (!userColors.containsKey(ticket.getUserId())) {
            userColors.put(ticket.getUserId(), generateColorForUserId(ticket.getUserId()));
        }
        updateMaxCoordinates(Collections.singletonList(ticket));
        refreshCircles();
    }

    public void removeTicket(Ticket ticket) {
        routes.remove(ticket);
        updateMaxCoordinates(routes);
        refreshCircles();
    }

    public void updateTicket(Ticket ticket) {
        removeTicket(ticket);
        addTicket(ticket);
    }

    public void clearAllTickets() {
        routes.clear();
        circles.clear();
        maxX = 0;
        maxY = 0;
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (CircleAnimation circle : circles) {
            circle.draw(gc);
        }
    }

    private void handleCanvasClick(MouseEvent event) {
        for (CircleAnimation circle : circles) {
            if (circle.contains(event.getX(), event.getY())) {
                mainController.selectTicket(circle.getRoute());
                break;
            }
        }
    }

    private void updateMaxCoordinates(List<Ticket> tickets) {
        if (tickets.isEmpty()) {
            return;
        }

        for (Ticket ticket : tickets) {
            double x = ticket.getCoordinates().x();
            float y = ticket.getCoordinates().y();

            if (Math.abs(x) > maxX) maxX = Math.abs(x);
            if (Math.abs(y) > maxY) maxY = Math.abs(y);
        }
    }

    private void refreshCircles() {
        circles.clear();
        for (Ticket ticket : routes) {
            circles.add(new CircleAnimation(ticket, userColors.get(ticket.getUserId()), maxX, maxY));
        }
    }

    private class CircleAnimation {
        private Ticket ticket;
        private double x, y;
        private double radius;
        private Color color;

        public CircleAnimation(Ticket ticket, Color color, double maxX, float maxY) {
            this.ticket = ticket;
            this.color = color;
            double canvasWidth = canvas.getWidth();
            double canvasHeight = canvas.getHeight();
            // Центр canvas как центр координатной системы
            double centerX = canvasWidth / 2;
            double centerY = canvasHeight / 2;
            this.x = centerX + normalizeCoordinate(ticket.getCoordinates().x(), maxX, canvasWidth);
            this.y = centerY - normalizeCoordinate(ticket.getCoordinates().y(), maxY, canvasHeight); // Инвертируем y для правильного отображения
            this.radius = calculateRadius(ticket);
        }

        public Ticket getRoute() {
            return ticket;
        }

        private double calculateRadius(Ticket ticket) {
            return (1 / (0.5 + Math.pow(Math.E, (-ticket.getPrice()) / 50))) * 10;
        }

        private double normalizeCoordinate(double value, double max, double canvasSize) {
            return (value / max) * (canvasSize / 2 - 20); // Нормализуем и масштабируем координаты
        }

        private double normalizeCoordinate(float value, float max, double canvasSize) {
            return (value / max) * (canvasSize / 2 - 20); // Нормализуем и масштабируем координаты
        }

        public void draw(GraphicsContext gc) {
            gc.setFill(color);
            gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        }

        public boolean contains(double mx, double my) {
            double dx = mx - x;
            double dy = my - y;
            return dx * dx + dy * dy <= radius * radius;
        }
    }

    private Color generateColorForUserId(int userId) {
        int hash = Integer.hashCode(userId);
        Random random = new Random(hash);
        return Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
    }

}
