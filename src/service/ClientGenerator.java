package service;

import model.Order;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientGenerator extends Thread {
    private final BlockingQueue<Order> orderQueue;
    private final AtomicInteger orderIdCounter = new AtomicInteger(1);
    private final Random random = new Random();
    private volatile boolean running = true;
    private final int maxCityX = 100;
    private final int maxCityY = 100;

    public ClientGenerator(BlockingQueue<Order> orderQueue) {
        this.orderQueue = orderQueue;
    }

    @Override
    public void run() {
        System.out.println("Генератор клиентов запущен.");
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                int fromX = random.nextInt(maxCityX);
                int fromY = random.nextInt(maxCityY);
                int toX = random.nextInt(maxCityX);
                int toY = random.nextInt(maxCityY);

                Order order = new Order(orderIdCounter.getAndIncrement(), fromX, fromY, toX, toY);
                System.out.println("Создан новый " + order);
                orderQueue.put(order);

                Thread.sleep(random.nextInt(3000) + 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Генератор клиентов остановлен.");
                break;
            }
        }
    }

    public void stopGenerator() {
        running = false;
        this.interrupt();
    }
}