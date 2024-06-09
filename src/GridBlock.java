// GridBlock class to represent a block in the club.
// only one thread at a time "owns" a GridBlock

public class GridBlock {
	private int isOccupied; 
	private final boolean isExit;  //is tthis the exit door?
	private final boolean isBar; //is it a bar block?
	private final boolean isDance; //is it the dance area?
	private int [] coords; // the coordinate of the block.
	
   // Constructor for creating a GridBlock
   GridBlock(boolean exitBlock, boolean barBlock, boolean danceBlock) throws InterruptedException {
      isExit = exitBlock;
      isBar = barBlock;
      isDance = danceBlock;
      isOccupied = -1;
    }
   
   // Overloaded constructor for creating a GridBlock with specific coordinates
   GridBlock(int x, int y, boolean exitBlock, boolean refreshBlock, boolean danceBlock) throws InterruptedException {
       this(exitBlock, refreshBlock, danceBlock);
       coords = new int[]{x, y};
   }
   
   // Get the X coordinate of the GridBlock
   public synchronized int getX() {
       return coords[0];
   }  
   
   // Get the Y coordinate of the GridBlock
   public synchronized int getY() {
       return coords[1];
   }
   
   // Attempt to occupy the GridBlock by a thread
   public synchronized boolean get(int threadID) throws InterruptedException {
       if (isOccupied == threadID) {
           return true; // Thread is already in this block
       }
       if (isOccupied >= 0) {
           return false; // Space is occupied by another thread
       }
       isOccupied = threadID; // Set ID of the occupying thread
       return true;
   }
       
   // Release the GridBlock so that it can be occupied by other threads
   public synchronized void release() {
       isOccupied = -1;
   }
   
   // Check if the GridBlock is currently occupied by a thread
   public synchronized boolean occupied() {
       return isOccupied != -1;
   }
   
   // Check if the GridBlock is marked as the exit door
   public boolean isExit() {
       return isExit;    
   }
   
   // Check if the GridBlock is marked as a bar block
   public boolean isBar() {
       return isBar;
   }
   
   // Check if the GridBlock is marked as the dance area
   public boolean isDanceFloor() {
       return isDance;
   }
}
