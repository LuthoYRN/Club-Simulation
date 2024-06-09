// the main class, starts all threads
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.*;

public class ClubSimulation {
    public static CountDownLatch startLatch;
    static int noClubgoers = 100;
    static int frameX = 400;
    static int frameY = 500;
    static int yLimit = 400;
    static int gridX = 30; // Number of X grids in club - default value if not provided on command line
    static int gridY = 30; // Number of Y grids in club - default value if not provided on command line
    static int max = 30; // Max number of customers - default value if not provided on command line

    static Clubgoer[] patrons; // Array for customer threads
    static PeopleLocation[] peopleLocations; // Array to keep track of where customers are
    static PeopleCounter tallys; // Counters for number of people inside and outside club

    static ClubView clubView; // Threaded panel to display terrain
    static ClubGrid clubGrid; // Club grid
    static CounterDisplay counterDisplay; // Threaded display of counters

    private static int maxWait = 1200; // For the slowest customer
    private static int minWait = 500; // For the fastest customer

    // Set up the graphical user interface
    public static void setupGUI(int frameX, int frameY, int[] exits) {
        // Frame initialize and dimensions
        JFrame frame = new JFrame("club Shay Shay");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(frameX, frameY);

        JPanel g = new JPanel();
        g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS));
        g.setSize(frameX, frameY);

        clubView = new ClubView(peopleLocations, clubGrid, exits);
        clubView.setSize(frameX, frameY);
        g.add(clubView);

        // Add all the counters to the panel
        JPanel txt = new JPanel();
        txt.setLayout(new BoxLayout(txt, BoxLayout.LINE_AXIS));
        JLabel maxAllowed = new JLabel("Max: " + tallys.getMax() + "    ");
        JLabel caught = new JLabel("Inside: " + tallys.getInside() + "    ");
        JLabel missed = new JLabel("Waiting:" + tallys.getWaiting() + "    ");
        JLabel scr = new JLabel("Left club:" + tallys.getLeft() + "    ");
        txt.add(maxAllowed);
        txt.add(caught);
        txt.add(missed);
        txt.add(scr);
        g.add(txt);
        counterDisplay = new CounterDisplay(caught, missed, scr, tallys); // Thread to update score

        JPanel b = new JPanel();
        b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS));
        JButton startB = new JButton("Start");

        // Action listener for the Start button
        startB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startLatch.countDown();
                startB.setEnabled(false);
            }
        });

        final JButton pauseB = new JButton("Pause ");;

        // Action listener for the Pause button
        pauseB.addActionListener(new ActionListener() {
        //implementing synchronization mechanism to pause threads
            public void actionPerformed(ActionEvent e) {
                if (!(Clubgoer.pause.get())) {
                    pauseB.setText("Resume");
                    Clubgoer.pause.set(true);
                    Barman.pause.set(true);
                } else {
                    pauseB.setText("Pause");
                    Clubgoer.pause.set(false);
                    Barman.pause.set(false);
                    //when threads are resumed, notifyAll() awakens the waiting threads
                    synchronized (Clubgoer.pause) {
                        Clubgoer.pause.notifyAll();
                    }
                    synchronized (Barman.pause) {
                        Barman.pause.notifyAll();
                    }
                }
            }
        });

        JButton endB = new JButton("Quit");
        // Action listener for the Quit button
        endB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        b.add(startB);
        b.add(pauseB);
        b.add(endB);

        g.add(b);

        frame.setLocationRelativeTo(null); // Center window on screen.
        frame.add(g); // Add contents to window
        frame.setContentPane(g);
        frame.setVisible(true);
    }

    // Main method to start the simulation
    public static void main(String[] args) throws InterruptedException {
        // Deal with command line arguments if provided
        if (args.length == 4) {
            noClubgoers = Integer.parseInt(args[0]); // Total people to enter the room
            gridX = Integer.parseInt(args[1]); // No. of X grid cells
            gridY = Integer.parseInt(args[2]); // No. of Y grid cells
            max = Integer.parseInt(args[3]); // Max people allowed in the club
        }

        // Hardcoded exit doors
        int[] exit = {0, (int) gridY / 2 - 1}; // One-cell wide door on the left

        tallys = new PeopleCounter(max); // Counters for people inside and outside the club
        clubGrid = new ClubGrid(gridX, gridY, exit, tallys); // Setup club with size, exits, and maximum limit for people
        clubGrid.setBarManID(noClubgoers);
        Clubgoer.club = clubGrid; // Grid shared with class
        Barman.club = clubGrid;
        peopleLocations = new PeopleLocation[noClubgoers + 1];
        peopleLocations[noClubgoers] = new PeopleLocation(noClubgoers);
        Clubgoer.andre = new Barman(noClubgoers, peopleLocations[noClubgoers], 300); //barman
        Clubgoer.andre.maxPeople = noClubgoers;
        patrons = new Clubgoer[noClubgoers];
        for (int i = 0; i < noClubgoers; i++) {
            peopleLocations[i] = new PeopleLocation(i);
            int movingSpeed = (int) (Math.random() * (maxWait - minWait) + minWait); // Range of speeds for customers
            patrons[i] = new Clubgoer(i, peopleLocations[i], movingSpeed);
        }

        setupGUI(frameX, frameY, exit); // Start Panel thread - for drawing animation
        // Start all the threads
        Thread t = new Thread(clubView);
        t.start();
        // Start counter thread - for updating counters
        Thread s = new Thread(counterDisplay);
        s.start();
        startLatch = new CountDownLatch(1);
        Clubgoer.andre.start();
        for (int i = 0; i < noClubgoers; i++) {
            patrons[i].start();
        }
        for (int i = 0; i < noClubgoers; i++) {
            patrons[i].join(); //used to make program terminate after all threads are done
        }
        Clubgoer.andre.join();
        System.out.println("Club closed");
        System.exit(0);
    }
}
