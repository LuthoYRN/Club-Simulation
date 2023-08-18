package clubSimulation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import javax.swing.JPanel;

public class ClubView extends JPanel implements Runnable {
		private PeopleLocation[] patronLocations; //array of the locations of the patrons
		private PeopleLocation barpersonLocation; // where is the barperson?
		private int noPatrons;  //total number in the simulation
		private int [] exits; // where is the exit?
		private int wIncr; //width of each block
		private int hIncr; //height of each block
		private int maxY; //maximum Y  for the grid
		private int maxX; //Maximum X for the grid

		ClubGrid grid; //shared grid
		
		ClubView(PeopleLocation[] custs,  ClubGrid grid,int []exits) { //constructor
			this.patronLocations=custs; 
			noPatrons = custs.length;
			this.grid = grid;
			this.exits=exits;
			this.maxY = grid.getMaxY();
		    this.maxX= grid.getMaxX();
		    int width = getWidth();
		    int height = getHeight();
		    wIncr= width/(maxX+2); //1 space on either side
		    hIncr= height/(maxY+2);//2 spaces on bottom
		}
		
		public void paintComponent(Graphics g) {
			
		    int width = getWidth();
		    int height = getHeight();
		    wIncr= width/(maxX+2); //1 space on either side
		    hIncr= height/(maxY+2);//2 spaces on bottom

		    g.clearRect(0,0,width,height);
		    g.setColor(Color.black);
		    
	    
		  //highlight the entrance
		    g.setColor(Color.gray);
		    GridBlock entrance = grid.whereEntrance();
		    g.fillRect(entrance.getX()*wIncr+wIncr, entrance.getY()*hIncr, wIncr, hIncr);
		    g.drawString("Enter",entrance.getX()*wIncr+wIncr,entrance.getY()*hIncr+hIncr);
		    
		    //highlight the exit block
		    g.setFont(new Font("Helvetica", Font.BOLD, hIncr/2));
		    g.setColor(Color.pink);
		    g.fillRect(exits[0]*wIncr+wIncr, exits[1]*hIncr, wIncr, hIncr);
		    g.setColor(Color.red);
		    g.drawString("Exit",exits[0]*wIncr+wIncr,exits[1]*hIncr+hIncr);	
		    
		    		    
		    //draw and label Bar counter
		    g.setColor(Color.lightGray);
		    g.fillRect(wIncr, (grid.bar_y)*hIncr, wIncr*(maxX), hIncr*1);
		    g.setColor(Color.black);
		    g.setFont(new Font("Helvetica", Font.BOLD, hIncr));
		    g.drawString("Bar",(maxX-1)*wIncr/2,grid.bar_y*hIncr+hIncr);	
		    
		  //draw and dance floor
		    g.setColor(Color.yellow);
		    g.fillRect(wIncr*maxX/2, (3)*hIncr, wIncr*(maxX/2), hIncr*(maxY-8));
		    g.setColor(Color.black);
		   // ((i>x/2) && (j>3) &&(j< (y-5))) 
		    
		    //draw grid lines
		    for (int i=1;i<=(maxX+1);i++)  //columns 
		    		g.drawLine(i*wIncr, 0, i*wIncr, maxY*hIncr); //- leave space at bottom
		    for (int i=0;i<=maxY;i++) //rows 
		    		g.drawLine(wIncr, i*hIncr, (maxX+1)*wIncr, i*hIncr); //- leave space at sides
	
		   //draw the ovals representing people in middle of grid block
			int x,y;
			 g.setFont(new Font("Helvetica", Font.BOLD, hIncr/2));
			 
			 //barman should go here
			 
			 //patrons
		    for (int i=0;i<noPatrons;i++){	    	
		    		if (patronLocations[i].inRoom()) {
			    		g.setColor(patronLocations[i].getColor());
			    		x= (patronLocations[i].getX()+1)*wIncr;
			    		y= patronLocations[i].getY()*hIncr;
			    		g.fillOval(x+wIncr/4, y+hIncr/4 , wIncr/2, hIncr/2);
			    		g.drawString(patronLocations[i].getID()+"",x+wIncr/4, y+wIncr/4);
		    		}
		    		else {
		    			//if( customerLocations[i].getArrived()) System.out.println("customer " + i+" waiting outside"); //debug
		    		}
		    }
		   }
	
		public void run() {
			while (true) {
				repaint();
			}
		}

	}


