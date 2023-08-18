package clubSimulation;

import java.awt.Color;
import java.util.Random;
import java.util.concurrent.atomic.*;

public class PeopleLocation  { // this is a separate class so don't have to access thread
	
	private final int ID; //each person has an ID
	private Color myColor; //colour of the person
	private AtomicBoolean inRoom; //are they in the club?
	private AtomicBoolean arrived; //have they arrived at the club?
	private GridBlock location; //which GridBlock are they on?
	
	PeopleLocation(int ID ) {
		Random rand = new Random();
		float c = rand.nextFloat(); //bit of a hack to get different colours
		myColor = new Color(c, rand.nextFloat(), c);	//only set at beginning	by thread
		inRoom = new AtomicBoolean(false); //not in club
		arrived = new AtomicBoolean(false); //have not arrive outside
		this.ID=ID;
	}
	
	//setter
	public  void setInRoom(boolean in) {
		this.inRoom.set(in);
	}
	
	//getter and setter
	public boolean getArrived() {
		return arrived.get();
	}
	public void setArrived() {
		this.arrived.set(true);;
	}

//getter and setter
	public GridBlock getLocation() {
		return location;
	}
	public  void setLocation(GridBlock location) {
		this.location = location;
	}

	//getter
	public  int getX() { return location.getX();}	
	
	//getter
	public  int getY() {	return location.getY();	}
	
	//getter
	public  int getID() {	return ID;	}

	//getter
	public boolean inRoom() {
		return inRoom.get();
	}
	//getter and setter
	public Color getColor() { return myColor; }
	public void setColor(Color myColor) { this.myColor= myColor; }
}
