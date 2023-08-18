#Concurrency Assignment: Club Simulation

This program simulates a club scenario using concurrency. Multiple patrons (threads) interact with the club environment, following specified behavior rules. The simulation offers insights into how patrons enter, move within, and leave the club while respecting occupancy limits and concurrency principles.

## Getting Started

1. **Download the Project:** Download the project folder containing all the required files.

2. **Compile the Code:** Open your terminal/command prompt and navigate to the project directory. Compile the Java code using the provided Makefile:

e.g
    make

3. Execute the program using the make run command, providing arguments for grid dimensions and the maximum club occupancy. 
e.g	
	make run ARGS="<no.Of.ClubGoers> <gridX> <gridY> <maxOccupancy>"

4. Output: Upon running the program, the simulation will display patrons' actions and movements within the club. Observe how patrons enter, interact, and exit the club while adhering to concurrency principles and behavior rules.
