package model;

import service.Dispatcher;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class Taxi extends Thread {
    private final int id;
    private final Dispatcher dispatcher;
    private final BlockingQueue<Order> personalOrderQueue = new LinkedBlockingQueue<>();
    private volatile boolean isFree = true;
    private final ReentrantLock lock = new ReentrantLock();
    private int currentX, currentY;

    public Taxi(int id, Dispatcher dispatcher, int startX, int startY) {
        this.id = id;
        this.dispatcher = dispatcher;
        this.currentX = startX;
        this.currentY = startY;
    }

    @Override
    public void run() {
        System.out.println("Такси #" + id + " выехало на линию. Координаты: (" + currentX + "," + currentY + ")");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Order order = personalOrderQueue.take();
                System.out.println("Такси #" + id + " получило заказ: " + order);

                processOrder(order);

                dispatcher.completeOrder(this, order);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Такси #" + id + " завершает работу.");
                break;
            }
        }
    }

    private void processOrder(Order order) throws InterruptedException {

        int distanceToClient = Math.abs(order.getFromX() - currentX) + Math.abs(order.getFromY() - currentY);
        System.out.println("Такси #" + id + " едет к клиенту, расстояние: " + distanceToClient);
        Thread.sleep(distanceToClient * 100L);

        int tripDistance = order.calculateDistance();
        System.out.println("Такси #" + id + " везет клиента, расстояние поездки: " + tripDistance);
        Thread.sleep(tripDistance * 150L);

        currentX = order.getToX();
        currentY = order.getToY();
        System.out.println("Такси #" + id + " завершило поездку. Новые координаты: (" + currentX + "," + currentY + ")");
    }

    public void assignOrder(Order order) throws InterruptedException {
        personalOrderQueue.put(order);
        setFree(false);
    }

    public boolean isFree() {
        lock.lock();
        try {
            return isFree;
        } finally {
            lock.unlock();
        }
    }

    public void setFree(boolean free) {
        lock.lock();
        try {
            isFree = free;
        } finally {
            lock.unlock();
        }
    }

    public int[] getCurrentPosition() {
        lock.lock();
        try {
            return new int[]{currentX, currentY};
        } finally {
            lock.unlock();
        }
    }

    public int getIdTaxi() { return id; }
}