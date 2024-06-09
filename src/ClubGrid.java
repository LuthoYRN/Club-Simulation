// Grid for the club
// This class represents the club as a grid of GridBlocks
public class ClubGrid {
    private GridBlock[][] Blocks;
    private final int x;
    private final int y;
    public final int bar_y;
    private GridBlock exit;
    private GridBlock entrance; // Hard-coded entrance
    private final static int minX = 5; // Minimum x dimension
    private final static int minY = 5; // Minimum y dimension
    private int barManID;

    public PeopleCounter counter;

    ClubGrid(int x, int y, int[] exitBlocks, PeopleCounter c) throws InterruptedException {
        if (x < minX) x = minX; // Minimum x
        if (y < minY) y = minY; // Minimum y
        this.x = x;
        this.y = y;
        this.bar_y = y - 3;
        Blocks = new GridBlock[x][y];
        this.initGrid(exitBlocks);
        entrance = Blocks[getMaxX() / 2][0];
        counter = c;
    }

    // Initialize the grid, creating all the GridBlocks
    private void initGrid(int[] exitBlocks) throws InterruptedException {
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                boolean exit_block = false;
                boolean bar = false;
                boolean dance_block = false;
                if ((i == exitBlocks[0]) && (j == exitBlocks[1])) {
                    exit_block = true;
                } else if (j >= (y - 3)) {
                    bar = true;
                } else if ((i > x / 2) && (j > 3) && (j < (y - 5))) {
                    dance_block = true;
                }
                Blocks[i][j] = new GridBlock(i, j, exit_block, bar, dance_block);
                if (exit_block) {
                    this.exit = Blocks[i][j];
                }
            }
        }
    }

    // Get the maximum x dimension of the grid
    public int getMaxX() {
        return x;
    }

    // Get the maximum y dimension of the grid
    public int getMaxY() {
        return y;
    }

    // Get the entrance block
    public synchronized GridBlock whereEntrance() {
        return entrance;
    }

    // Check if a specific position is within the grid
    public boolean inGrid(int i, int j) {
        if ((i >= x) || (j >= y) || (i < 0) || (j < 0))
            return false;
        return true;
    }

    // Check if a specific position is within the patron area
    public boolean inPatronArea(int i, int j) {
        if ((i >= x) || (j > bar_y) || (i < 0) || (j < 0))
            return false;
        return true;
    }

    // Method for a patron to enter the club
    public GridBlock enterClub(PeopleLocation myLocation) throws InterruptedException {
        synchronized (entrance) {
            counter.personArrived(); // Add to counter of people waiting
            while (entrance.occupied() == true) { //ensuring patrons enter in an orderly manner
                entrance.wait();
            }
            entrance.notifyAll(); //telling waiting thread that entrance is no longer occupied
            entrance.get(myLocation.getID());
            while ((counter.getInside() >= counter.getMax())) { //enforcing club limit
                entrance.wait();
            }
            counter.personEntered(); // Add to counter
            myLocation.setLocation(entrance);
            myLocation.setInRoom(true);
            return entrance;
        }
    }

    // Method for a patron to leave the club
    public void leaveClub(GridBlock currentBlock, PeopleLocation myLocation) {
        synchronized (entrance) {
            currentBlock.release();
            counter.personLeft(); // Add to counter
            myLocation.setInRoom(false);
            entrance.notify();
        }
    }

    // Method for a patron to move to a new block
    public synchronized GridBlock move(GridBlock currentBlock, int step_x, int step_y, PeopleLocation myLocation)
            throws InterruptedException {
        int c_x = currentBlock.getX();
        int c_y = currentBlock.getY();

        int new_x = c_x + step_x; // New block x coordinates
        int new_y = c_y + step_y; // New block y coordinates

        // Restrict i an j to grid
        if (myLocation.getID() != barManID) { // BarMan exception to this rule
            if (!inPatronArea(new_x, new_y)) {
                // Invalid move to outside - ignore
                return currentBlock;
            }
        }

        if ((new_x == currentBlock.getX()) && (new_y == currentBlock.getY())) {// Not actually moving
            return currentBlock;
        }

        GridBlock newBlock = Blocks[new_x][new_y];
        if (!newBlock.get(myLocation.getID())) {
            return currentBlock;
        } // Stay where you are

        currentBlock.release(); // Must release current block
        myLocation.setLocation(newBlock);
        return newBlock;
    }

    // Get the exit block
    public GridBlock getExit() {
        return exit;
    }

    // Method to find the block at a specific position
    public synchronized GridBlock whichBlock(int xPos, int yPos) {
        if (inGrid(xPos, yPos)) {
            return Blocks[xPos][yPos];
        }
        System.out.println("Block " + xPos + " " + yPos + " not found");
        return null;
    }

    // Set the exit block
    public void setExit(GridBlock exit) {
        this.exit = exit;
    }

    // Get the y-coordinate of the bar area
    public int getBar_y() {
        return bar_y;
    }

    // Set the ID of the barman
    public void setBarManID(int ID) {
        this.barManID = ID;
    }
}
