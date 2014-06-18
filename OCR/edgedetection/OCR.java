package edgedetection;

import java.io.*;
/*

*/
public class OCR implements Serializable {
	static OCR me = new OCR();
	private static final long serialVersionUID = 1L;
	// size of read image in pixels
	private static final int PIC_SIZE = 200;
	static LetterIterations li = me.new LetterIterations();
	
	private class LetterIterations implements Serializable{
		private static final long serialVersionUID = 1L;
		int[] iterations = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		
		// returns the position in the iteration vector, given a letter.
		public int getPosition(char c){
			int ret = 0;
			switch(c) {
			case 'a': ret = 0; break;
			case 'b': ret = 1; break;
			case 'c': ret = 2; break;
			case 'd': ret = 3; break;
			case 'e': ret = 4; break;
			case 'f': ret = 5; break;
			case 'g': ret = 6; break;
			case 'h': ret = 7; break;
			case 'i': ret = 8; break;
			case 'j': ret = 9; break;
			case 'k': ret = 10; break;
			case 'l': ret = 11; break;
			case 'm': ret = 12; break;
			case 'n': ret = 13; break;
			case 'o': ret = 14; break;
			case 'p': ret = 15; break;
			case 'q': ret = 16; break;
			case 'r': ret = 17; break;
			case 's': ret = 18; break;
			case 't': ret = 19; break;
			case 'u': ret = 20; break;
			case 'v': ret = 21; break;
			case 'w': ret = 22; break;
			case 'x': ret = 23; break;
			case 'y': ret = 24; break;
			case 'z': ret = 25; break;
			}
			
			return ret;
		}
		// returns the number of iterations have passed for given letter.
		public int getIteration(char c){
			int pos = this.getPosition(c);
			return iterations[pos];
		}
		// increments the iteration number of a character.
		public void incIteration(char c){
			int pos = this.getPosition(c);
			iterations[pos] += 1;
		}
		// sets the iteration number of a character with the given number.
		public void setIteration(char c, int val){
			int pos = this.getPosition(c);
			iterations[pos] = val;
		}
	}
	private class TrainingLetters{
		ImageMap[] mapArray = {};
		// no argument constructor
		TrainingLetters(){
			
		}
		// creates an ImageMap array with the given ImageMap array.
		TrainingLetters(ImageMap[] imArray){
			mapArray = imArray;
		}
		// add an ImageMap to mapArray. Pushes if character is not present, averages grey-value pixels otherwise.
		public void addImageMap(ImageMap im){
			// check if an ImageMap already exists for a given letter
			boolean exists = false;
			int index = this.mapArray.length;
			for(int i = 0; i < this.mapArray.length; i++){
				if(this.getImageMap(i).getChar() == im.getChar()){
					index = i;
					exists = true;
					break;
				}
			}
			// if exists-average pixels, else-push to mapArray
			if(!exists){
				// push to mapArray
				this.Push(im);
			} else {
				// average pixels
				ImageMap working = getImageMap(index);
				// iterate over every pixel in working image.
				for(int j = 0; j < PIC_SIZE; j++){
					for(int i = 0; i < PIC_SIZE; i++){
						double n, m;
						n = im.getPoint(new Coords(i,j));
						m = working.getPoint(new Coords(i,j)) * li.getIteration(working.getChar());
						double val = (n + m) / (li.getIteration(working.getChar()) + 1);
						working.setPoint(new Coords(i,j), val);
					}
				}
				li.incIteration(working.getChar());
				//working.WriteMap(working.getChar() +""+ li.getIteration(working.getChar()));
			}
		}
		// returns ImageMap at specified index
		public ImageMap getImageMap(int index){
			return mapArray[index];
		}
		// adds an ImageMap to the last element of mapArray.
		public void Push(ImageMap im){
			ImageMap[] newarray = new ImageMap[mapArray.length + 1];
			for(int i = 0; i < mapArray.length; i++){
				newarray[i] = mapArray[i];
			}
			newarray[newarray.length - 1] = im;		
			this.mapArray = newarray;
		}
	}
	private class ImageMap implements Serializable{
		private static final long serialVersionUID = 1L;
		double[][] map = new double[PIC_SIZE][PIC_SIZE];
		char c;
		// only add iter when ready to serialize
		int iter;
		// only get iter after deserialize
		
		public double getPoint(Coords coords){
			return map[coords.getY()][coords.getX()];
		}
		public char getChar(){
			return this.c;
		}
		public void setChar(char c){
			this.c = c;
		}
		public void setPoint(Coords coords, double val){
			this.map[coords.getY()][coords.getX()] = val;
		}
		private void WriteMap(String name){
			String str = "";
			double[][] cur = map;
			try {
				PrintWriter writer = new PrintWriter("../txtOutput/" + name +".txt");
				for(int j = 0; j < PIC_SIZE; j++){
					for(int i = 0; i < PIC_SIZE; i++){
						int val = (int)Math.round(cur[j][i]);
						if(val > 9 && val < 100){
							str = val +" ";
						} else if(val >= 0 && val < 10){
							str = val + "  ";
						}else {
							str = val +"";
						}
						writer.print(str + " ");
					}
					writer.println();
				}
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args){
		OCR me = new OCR();
		String tbrDir = new String("../edgedetection/toberead.jpg");
		Picture pic = new Picture(tbrDir);
		
		TrainingLetters tl = null;
		tl = deSerialize();
		if(tl.mapArray.length <= 25){
			System.out.println("hi");
			tl = getTrainingLetters(me.new TrainingLetters());
		}
		
		pic = prepPicture(pic);
		PicFrame f = new PicFrame(pic);
		f.buildFrame();
		ImageMap result = closestMatch(createImageMap(pic), tl);
		serializeTLMaps(tl);
		//deSerialize();
		System.out.println("Total trainers: " + li.getIteration('z'));
		System.out.println("Guess:" +result.getChar());
	}
	public static Picture prepPicture(Picture pic){
		pic.convertToGreyscale();
		pic.absoluteGreyscaleContrast();
		pic.trimAspectSecure(.4);
		pic.scalePicture(PIC_SIZE, PIC_SIZE);
		return pic;
	}
	public static TrainingLetters getTrainingLetters(TrainingLetters tl){
		File dir = new File("C://Users/Andy/workspace/ImageProcessing/src/TrainingPictures/");
		for (File child : dir.listFiles()) {
			String type = child.getName().substring((int)child.getName().length() - 4, (int)child.getName().length());
		  	String name = child.getName().substring(0, 1);
//		  	System.out.println(type);
		  	int num = Integer.parseInt(child.getName().substring(1, (int)child.getName().length() - 4));
		  	if(type.equals(".jpg")){
		  		Picture temp = prepPicture(new Picture(child.getPath()));
		  		ImageMap im = createImageMap(temp, name.charAt(0));
		  		tl.addImageMap(im);
		  		System.out.println("File: " + name + num);
		  	}
		}
		return tl;
	}
	public static ImageMap createImageMap(Picture p, char c){
		OCR me = new OCR();
		ImageMap im = me.new ImageMap();
		for(int j = 0; j < p.getHeight(); j++){
			for(int i = 0; i < p.getWidth(); i++){
				Coords coords = new Coords(i,j);
				im.setPoint(coords, p.getPixelGrey(coords));
			}
		}
		im.setChar(c);
		return im;
	}
	public static ImageMap createImageMap(Picture p){
		OCR me = new OCR();
		ImageMap im = me.new ImageMap();
		for(int j = 0; j < p.getHeight(); j++){
			for(int i = 0; i < p.getWidth(); i++){
				Coords coords = new Coords(i,j);
				im.setPoint(coords, p.getPixelGrey(coords));
			}
		}
		return im;
	}
	public static int euclideanPictureDistance(ImageMap im1, ImageMap im2){
		int sumSqr = 0;
		for(int j = 0; j < PIC_SIZE; j++){
			for(int i = 0; i < PIC_SIZE; i++){
				sumSqr += Math.pow((im2.getPoint(new Coords(i,j)) - im1.getPoint(new Coords(i,j))), 2);
			}
		}
		return (int)Math.sqrt(sumSqr);
	}
	public static ImageMap closestMatch(ImageMap im, TrainingLetters tl){
		//Find the smallest euclidean distance between source image and all training images
		double lowest = Double.MAX_VALUE;
		double[] arr = new double[tl.mapArray.length];
		double cur = 0, average = 0;
		double standev = 0;
		int index = tl.mapArray.length;
		for(int i = 0; i < tl.mapArray.length; i++){
			cur = euclideanPictureDistance(im, tl.getImageMap(i));
			System.out.println("Letter: " + tl.getImageMap(i).getChar()+ " strength: " + (1-(cur / 51000))*100+"%");
			tl.getImageMap(i).WriteMap(tl.getImageMap(i).getChar() + "");
			if(cur < lowest){
				lowest = cur;
				index = i;
			}
			arr[i] = cur;
			average = ((average * i) + cur) / (i + 1);
		}
		for(int i = 0; i < tl.mapArray.length; i++){
			standev += Math.pow((arr[i] - average), 2);
		}
		standev = Math.sqrt(standev / tl.mapArray.length);
		System.out.println("avg: "+average+" standev: "+standev);
		for(int i = 0; i < tl.mapArray.length; i++){
			System.out.println("Letter: " + tl.getImageMap(i).getChar()+ " z-score: " + -((arr[i] - average) / standev));
		}
		return tl.getImageMap(index);
	}
	public static void serializeTLMaps(TrainingLetters tl){
		// sends the final map to a serial file to be stored for later use
		// this way the program does not need to recompile library for every use
		try{
			for(int i = 0; i < tl.mapArray.length; i++){
				char c = tl.getImageMap(i).getChar();
				tl.getImageMap(i).iter = li.getIteration(c);
				FileOutputStream fout = new FileOutputStream("../SerialFiles/"+ c +".ser");
				ObjectOutputStream oos = new ObjectOutputStream(fout);
				oos.writeObject(tl.getImageMap(i));
				oos.close();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	public static TrainingLetters deSerialize(){
		File dir = new File("../SerialFiles");
		ImageMap[] imArray = new ImageMap[0];
		for (File child : dir.listFiles()) {
			String type = child.getName().substring((int)child.getName().length() - 4, (int)child.getName().length());
//		  	System.out.println(type);
//		  	String num = child.getName().substring(1, 2);
		  	if(type.equals(".ser")){
		  		try {
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(child));
					imArray = Push((ImageMap)ois.readObject(), imArray);
					ois.close();
		  		} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
		  	}
		}
		for(int i = 0; i < imArray.length; i++){
			li.setIteration(imArray[i].getChar(), imArray[i].iter);
		}
		return me.new TrainingLetters(imArray);
	}
	public static ImageMap[] Push(ImageMap im, ImageMap[] array){
		ImageMap[] newarray = new ImageMap[array.length + 1];
		for(int i = 0; i < array.length; i++){
			newarray[i] = array[i];
		}
		newarray[newarray.length - 1] = im;		
		return newarray;
	}
}

