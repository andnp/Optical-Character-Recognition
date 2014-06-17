package edgedetection;

public class Coords{
	private int[] coords = new int[2];
	Coords(int x, int y){
		this.coords[0] = x;
		this.coords[1] = y;
	}
	public int getX(){
		return this.coords[0];
	}
	public int getY(){
		return this.coords[1];
	}
	public boolean equals(Coords coords){
		return (this.getX() == coords.getX() && this.getY() == coords.getY());
	}
}