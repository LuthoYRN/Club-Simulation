package clubSimulation;

import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

// Barman class responsible for serving drinks to patrons
public class Barman extends Thread {
    public static ClubGrid club;
    public PeopleLocation location;
    private Queue<Clubgoer> drinkQueue;
    public static int maxPeople;
    private int movingSpeed;
    private GridBlock currentBlock;
    private int ID; // Barman ID
    public static AtomicBoolean pause;

    // Constructor to initialize the Barman
    public Barman(int ID, PeopleLocation loc, int speed) {
        this.ID = ID;
        this.location = loc;
        this.movingSpeed = speed;
        pause = new AtomicBoolean(false);

        // Calculate the initial position for the barman in the center of the bar area
        int bar_x = club.getMaxX() / 2; // Middle of the x-axis
        int bar_y = club.getBar_y() + 1; // Top of the bar area
        currentBlock = club.whichBlock(bar_x, bar_y);
        this.location.setLocation(currentBlock);
        this.drinkQueue = new LinkedList<>();
    }

    // Add a patron to the drink queue
    public synchronized void addToQueue(Clubgoer patron) {
        drinkQueue.add(patron);
    }

    // Serve a drink to a patron
    public synchronized void serveDrink(Clubgoer patron) throws InterruptedException {
        getDrinks();
        headTowardsCustomer(patron);
        System.out.println("Bartender getting drink for thread " + patron.getID());
        sleep(100); // Serving time
        synchronized (patron) {
            patron.notifyAll(); // Notify the waiting patron
        }
        headTowardsOriginalPos();
    }

    // Move towards the customer's location
    private synchronized void headTowardsCustomer(Clubgoer patron) throws InterruptedException {
        int x_mv = Math.abs(patron.getX() - currentBlock.getX());
        if (x_mv != 0) {
            int x_step = (patron.getX() < currentBlock.getX()) ? -1 : 1;
            for (int i = 0; i < x_mv; i++) {
                checkPause();
                System.out.println("Bartender coming towards thread " + patron.getID() + "...");
                currentBlock = club.move(currentBlock, x_step, 0, location);
                sleep(movingSpeed);
                checkPause();
            }
        }
    }

    // Get drinks from the bar area
    private synchronized void getDrinks() throws InterruptedException {
        checkPause();
        currentBlock = club.move(currentBlock, 0, 1, location);
        sleep(movingSpeed);
        checkPause();
        currentBlock = club.move(currentBlock, 0, -1, location);
        sleep(movingSpeed);
    }

    // Move back to the original position after serving the drink
    private synchronized void headTowardsOriginalPos() throws InterruptedException {
        int x_mv = Math.abs((club.getMaxX() / 2) - currentBlock.getX());
        if (x_mv != 0) {
            int x_step = ((club.getMaxX() / 2) < currentBlock.getX()) ? -1 : 1;
            for (int i = 0; i < x_mv; i++) {
                checkPause();
                currentBlock = club.move(currentBlock, x_step, 0, location);
                sleep(movingSpeed);
                checkPause();
            }
        }
    }

    // Check to see if the pause button is pressed
    private void checkPause() {
        while (pause.get()) {
            try {
                synchronized (pause) {
                    pause.wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
                return;
            }
        }
    }

    // Getter for the Barman's ID
    public int getID() {
        return ID;
    }

    // Getter for the Barman's X position
    public int getX() {
        return currentBlock.getX();
    }

    // Getter for the Barman's Y position
    public int getY() {
        return currentBlock.getY();
    }

    // Run method for the Barman thread
    public void run() {
        while (!(club.counter.getLeft() == maxPeople)) {
            while (!drinkQueue.isEmpty()) {
                Clubgoer patron = null;
                synchronized (drinkQueue) {
                    patron = drinkQueue.poll(); // Get the next patron in the queue
                }
                if (patron != null) {
                    try {
                        serveDrink(patron);
                    } catch (InterruptedException e) {
                        System.err.println(e);
                    }
                }
            }
        }
        System.out.println("Barman served all " + club.counter.getLeft() + " patrons");
        System.out.println("Barman clocking out");
    }
}
