package clubSimulation;

import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Barman extends Thread {
    public static ClubGrid club;
    public PeopleLocation location;
    private Queue<Clubgoer> drinkQueue;
    public static int maxPeople;
    private int movingSpeed;
    private GridBlock currentBlock;
    private int ID; //barman ID 
    public AtomicBoolean pause;
       
    public Barman(int ID,PeopleLocation loc,int speed) {
      this.ID = ID;
      this.location = loc;
      this.movingSpeed = speed;
      pause = new AtomicBoolean(false);
      // Calculate the initial position for the barman in the center of the bar area
      int bar_x = club.getMaxX() / 2; // Middle of the x-axis
      int bar_y = club.getBar_y()+1;    // Top of the bar area
      currentBlock = club.whichBlock(bar_x, bar_y);
      this.location.setLocation(currentBlock);
      this.drinkQueue = new LinkedList<>();
    }

    public synchronized void addToQueue(Clubgoer patron) {
      drinkQueue.add(patron);
    }

    public synchronized void serveDrink(Clubgoer patron) throws InterruptedException{
      headTowardsCustomer(patron);
      System.out.println("Bartender preparing drink for thread "+patron.getID());
      Thread.sleep(100); //serving time
      synchronized (patron) {
        patron.notifyAll(); // Notify the waiting patron
      }
      headTowardsOriginalPos();
     }
    
    private synchronized void headTowardsCustomer(Clubgoer patron) throws InterruptedException {
		int x_mv = Math.abs(patron.getX()-currentBlock.getX());
      if (x_mv!=0){
         int x_step = (patron.getX()<currentBlock.getX())?-1:1;
         for (int i=0;i<x_mv;i++){
            checkPause();
            System.out.println("Bartender coming towards thread "+patron.getID()+"...");
            currentBlock=club.move(currentBlock,x_step,0,location);
            sleep(movingSpeed); 
            checkPause();
        }
	  }
   }
   
    private synchronized void headTowardsOriginalPos() throws InterruptedException {
       int x_mv= Math.abs((club.getMaxX() / 2)-currentBlock.getX());
         if (x_mv!=0){
            int x_step = ((club.getMaxX() / 2)<currentBlock.getX())?-1:1;
            for (int i=0;i<x_mv;i++){
            checkPause();
            currentBlock=club.move(currentBlock,x_step,0,location);
            sleep(movingSpeed);
            checkPause();
        }
	  }
    }
   public void pauseThread(){
      pause.set(true);
   }
   
   public void resumeThread(){
      pause.set(false);
   }
   
   //check to see if user pressed pause button
	private void checkPause() {
		// my code
     while (pause.get()){
     try{
      sleep(100);
      }
     catch (InterruptedException e) {
        Thread.currentThread().interrupt();
         e.printStackTrace();
        return;
     }
    }    
   }
    
   public int getID(){
     return ID;
   }
	//getter
	public int getX() { return currentBlock.getX();}	
	
	//getter
	public int getY() {	return currentBlock.getY();	}
	
   public void run(){
      while (!(club.counter.getLeft()== maxPeople)) {
         while (!drinkQueue.isEmpty()) {
            Clubgoer patron = null;
            synchronized (drinkQueue) {
               patron = drinkQueue.poll(); // Get the next patron in the queue
            }
            if (patron != null) {
               try{
                    serveDrink(patron);
                  }
               catch (InterruptedException e){System.err.println(e);}
                }
            }
        }
    }
}