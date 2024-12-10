import java.util.concurrent.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultiThreadingStorage {
    public static void main(String[] args) {

        Storage storage1 = new Storage("Storage1");

        storage1.addProduct(new Product("1", 40));
        storage1.addProduct(new Product("2", 70));
        storage1.addProduct(new Product("3", 30));
        storage1.addProduct(new Product("4", 20));
        storage1.addProduct(new Product("5", 10));
        storage1.addProduct(new Product("6", 80));
        storage1.addProduct(new Product("7", 70));
        storage1.addProduct(new Product("8", 39));
        storage1.addProduct(new Product("9", 60));
        storage1.addProduct(new Product("10", 70));

        storage1.print();

        Storage storage2 = new Storage("Storage2");

        storage2.print();

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(3);

        Loader loader1 = new Loader("loader1", storage1, 150, storage2, start, end);
        Loader loader2 = new Loader("loader2", storage1, 150, storage2, start, end);
        Loader loader3 = new Loader("loader3", storage1, 150, storage2, start, end);

        loader1.start();
        loader2.start();
        loader3.start();

        try {
            start.countDown();

            end.await();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        storage1.print();
        storage2.print();
    }

    static class Product {
        private String name;
        private int weight;

        public Product(String name, int weight) {
            this.name = name;
            this.weight = weight;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public synchronized int getWeight() {
            return weight;
        }

        public synchronized String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name + " " + weight + "кг";
        }
    }

    static class Storage {
        private final Queue<Product> products = new ConcurrentLinkedQueue<>();
        private String name;

        public Storage(String name) {
            this.name = name;
        }

        public void addProduct(Product product) {
            products.add(product);
        }

        public Product getProduct() {
            return products.poll();
        }

        public boolean isEmpty() {
            return products.isEmpty();
        }

        public void print() {
            System.out.println(name);
            for (var i : products) {
                System.out.println(i);
            }
            if (isEmpty()) {
                System.out.println("Пусто");
            }
            System.out.println();
        }
    }

    static class Loader extends Thread {
        private final Storage storage;
        private final Storage target;
        private final int maxWeight;
        private final ConcurrentLinkedQueue<Product> inventory = new ConcurrentLinkedQueue<>();
        private final CountDownLatch start;
        private final CountDownLatch end;

        public Loader(String name, Storage storage, int maxWeight, Storage target, CountDownLatch start, CountDownLatch end) {
            super(name);
            this.storage = storage;
            this.maxWeight = maxWeight;
            this.target = target;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            try {
                start.await();

                int currentWeight = 0;
                while (true) {
                    Product product = storage.getProduct();

                    if (product == null) {
                        if (currentWeight > 0) {
                            System.out.println("\n" + getName() + " отправляется с грузом " + currentWeight + " кг." + "\n");
                            for (var i : inventory) {
                                target.addProduct(i);
                            }
                            inventory.clear();
                        }
                        break;
                    }

                    if (currentWeight + product.getWeight() > maxWeight) {
                        System.out.println("\n" + getName() + " отправляется с грузом " + currentWeight + " кг." + "\n");
                        for (var i : inventory) {
                            target.addProduct(i);
                        }
                        inventory.clear();
                        currentWeight = 0;
                    }

                    currentWeight += product.getWeight();
                    inventory.add(product);
                    System.out.println(getName() + " загружает: " + product);
                }
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            } finally {
                end.countDown();
            }
        }
    }
}
