import java.util.concurrent.atomic.*;

// Class to count and manage people inside and outside the club
public class PeopleCounter {
    private AtomicInteger peopleOutSide; // Counter for people arrived but not yet in the building
    private AtomicInteger peopleInside; // Counter for patrons inside the club
    private AtomicInteger peopleLeft; // Counter for patrons who have left the club
    private AtomicInteger maxPeople; // Maximum patrons allowed in the club at one time
    
    // Constructor to initialize counters with a maximum value
    PeopleCounter(int max) {
        peopleOutSide = new AtomicInteger(0);
        peopleInside = new AtomicInteger(0);
        peopleLeft = new AtomicInteger(0);
        maxPeople = new AtomicInteger(max);
    }
    
    // Get the number of people waiting outside
    public int getWaiting() {
        return peopleOutSide.get();
    }

    // Get the number of people currently inside the club
    public int getInside() {
        return peopleInside.get();
    }
    
    // Get the total number of people (inside, outside, and left)
    public int getTotal() {
        return (peopleOutSide.get() + peopleInside.get() + peopleLeft.get());
    }

    // Get the number of people who have left the club
    public int getLeft() {
        return peopleLeft.get();
    }
    
    // Get the maximum number of people allowed inside the club
    public int getMax() {
        return maxPeople.get();
    }
    
    // Increment the counter when someone arrives outside
    public void personArrived() {
        peopleOutSide.getAndIncrement();
    }
    
    // Increment inside and decrement outside counters when someone enters the club
    synchronized public void personEntered() {
        peopleOutSide.getAndDecrement();
        peopleInside.getAndIncrement();
    }

    // Increment left and decrement inside counters when someone leaves the club
    synchronized public void personLeft() {
        peopleInside.getAndDecrement();
        peopleLeft.getAndIncrement();
    }
    
    // Check if the club is over its maximum capacity
    synchronized public boolean overCapacity() {
        if (peopleInside.get() >= maxPeople.get())
            return true;
        return false;
    }
    
    // Method not used in the code
    public void resetScore() {
        peopleInside.set(0);
        peopleOutSide.set(0);
        peopleLeft.set(0);
    }
}
