package service;

import model.Order;
import model.Taxi;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class Dispatcher {
    private final BlockingQueue<Order> orderQueue;
    private final List<Taxi> taxis;

    public Dispatcher(BlockingQueue<Order> orderQueue) {
        this.orderQueue = orderQueue;
        this.taxis = new CopyOnWriteArrayList<>();
    }

    public void addTaxi(Taxi taxi) {
        taxis.add(taxi);
    }

    public Order assignOrderToTaxi(Taxi taxi) throws InterruptedException {
        taxi.setFree(true);
        return null;
    }

    public void assignOrders() throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            Order order = orderQueue.take();
            System.out.println("Диспетчер получил новый " + order + ". Ищем свободное такси...");

            Taxi nearestTaxi = findNearestTaxi(order);
            if (nearestTaxi != null) {
                synchronized (nearestTaxi) {
                    if (nearestTaxi.isFree()) {
                        try {
                            nearestTaxi.assignOrder(order);
                            System.out.println("Диспетчер назначил " + order + " такси #" + nearestTaxi.getIdTaxi());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            } else {
                System.out.println("Нет свободных такси для " + order + ". Возвращаем в очередь.");
                orderQueue.put(order);
                Thread.sleep(1000);
            }
        }
    }

    private Taxi findNearestTaxi(Order order) {
        Taxi nearest = null;
        int minDistance = Integer.MAX_VALUE;

        for (Taxi taxi : taxis) {
            if (taxi.isFree()) {
                int[] pos = taxi.getCurrentPosition();
                int distance = Math.abs(pos[0] - order.getFromX()) + Math.abs(pos[1] - order.getFromY());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = taxi;
                }
            }
        }
        return nearest;
    }

    public void completeOrder(Taxi taxi, Order order) {
        System.out.println("Такси #" + taxi.getIdTaxi() + " сообщило о завершении " + order);
        taxi.setFree(true);
    }
}
