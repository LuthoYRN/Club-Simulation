package clubSimulation;

import java.util.Queue;
import java.util.LinkedList;

public class Barman extends Thread {
    public static ClubGrid club;
    public PeopleLocation location;
    private Queue<Clubgoer> drinkQueue;

    public Barman(PeopleLocation loc) {
      this.location = loc;

        // Calculate the initial position for the barman in the center of the bar area
        int bar_x = club.getMaxX() / 2; // Middle of the x-axis
        int bar_y = club.getBar_y()+1;    // Top of the bar area
        this.location.setLocation(club.whichBlock(bar_x, bar_y));
        this.drinkQueue = new LinkedList<>();
    }

    public synchronized void addToQueue(Clubgoer patron) {
            drinkQueue.add(patron);
    }

    public synchronized void serveDrink(Clubgoer patron) {
        try {
            System.out.println("Bartender preparing drink for thread "+patron.getID());
            Thread.sleep(100); //serving time
        } catch (InterruptedException e) {
            System.err.println(e);
        }
        synchronized (patron) {
            patron.notifyAll(); // Notify the waiting patron
        }
    }

    public void run() {
        while (!(club.counter.getLeft()==club.counter.getMax())) {
            while (!drinkQueue.isEmpty()) {
                Clubgoer patron = null;
                synchronized (drinkQueue) {
                    patron = drinkQueue.poll(); // Get the next patron in the queue
                }
                if (patron != null) {
                    serveDrink(patron);
                }
            }
        }
    }
}