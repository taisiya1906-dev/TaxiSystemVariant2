package main;

import model.Taxi;
import service.ClientGenerator;
import service.Dispatcher;
import model.Order;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {
    public static void main(String[] args) {
        System.out.println("Запуск системы управления такси");

        BlockingQueue<Order> orderQueue = new ArrayBlockingQueue<>(20);
        Dispatcher dispatcher = new Dispatcher(orderQueue);

        ClientGenerator generator = new ClientGenerator(orderQueue);
        generator.start();

        for (int i = 1; i <= 2; i++) {
            Taxi taxi = new Taxi(i, dispatcher, i * 20, i * 20);
            dispatcher.addTaxi(taxi);
            taxi.start();
        }

        Thread dispatcherThread = new Thread(() -> {
            try {
                dispatcher.assignOrders();
            } catch (InterruptedException e) {
                System.out.println("Диспетчер остановлен.");
            }
        });
        dispatcherThread.start();

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n Остановка системы");
        generator.stopGenerator();
        dispatcherThread.interrupt();

        System.out.println("Система остановлена.");
    }
}