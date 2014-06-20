package ocr;

import java.io.*;
/*

*/
public class LetterReader implements Serializable {
	static LetterReader me = new LetterReader();
	private static final long serialVersionUID = 1L;
	// size of read image in pixels
	private static final int PIC_SIZE = 200;
	static LetterIterations li = me.new LetterIterations();
	static char[] supportedCharacters = 
		{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
		 'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	
	/* 
	 * A LetterIterations object keeps track of how many training letters
	 * have been processed thus far
	*/
	private class LetterIterations implements Serializable{
		private static final long serialVersionUID = 1L;
		// array keeping track of the number of training images.
		// the index is based on the character.
		int[] iterations = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		
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
			
			case 'A': ret = 26; break;
			case 'B': ret = 27; break;
			case 'C': ret = 28; break;
			case 'D': ret = 29; break;
			case 'E': ret = 30; break;
			case 'F': ret = 31; break;
			case 'G': ret = 32; break;
			case 'H': ret = 33; break;
			case 'I': ret = 34; break;
			case 'J': ret = 35; break;
			case 'K': ret = 36; break;
			case 'L': ret = 37; break;
			case 'M': ret = 38; break;
			case 'N': ret = 39; break;
			case 'O': ret = 40; break;
			case 'P': ret = 41; break;
			case 'Q': ret = 42; break;
			case 'R': ret = 43; break;
			case 'S': ret = 44; break;
			case 'T': ret = 45; break;
			case 'U': ret = 46; break;
			case 'V': ret = 47; break;
			case 'W': ret = 48; break;
			case 'X': ret = 49; break;
			case 'Y': ret = 50; break;
			case 'Z': ret = 51; break;
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
	/* 
	 * A training letter contains an ImageMap array and
	 * can add ImageMaps and average grey values
	 */
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
		// checks for missing characters based on list of supported characters. Returns char array of missing characters.
		public char[] checkForMissingCharacters(){
			char[] result = supportedCharacters.clone();
			for(ImageMap working : mapArray){
				result = remove(working.getChar(), result);
			}
			return result;
		}
	}
	/* 
	 * An ImageMap contains a PIC_SIZE x PIC_SIZE matrix containing grey values of an image
	 */
	private class ImageMap implements Serializable{
		private static final long serialVersionUID = 1L;
		double[][] map = new double[PIC_SIZE][PIC_SIZE];
		char c;
		// only add iter when ready to serialize
		int iter;
		// only get iter after deserialize
		
		// returns value of map at a given point
		public double getPoint(Coords coords){
			return map[coords.getY()][coords.getX()];
		}
		// returns the character that this ImageMap represents
		public char getChar(){
			return this.c;
		}
		// sets the character that this ImageMap represents
		public void setChar(char c){
			this.c = c;
		}
		// sets the value of a map at a point
		public void setPoint(Coords coords, double val){
			this.map[coords.getY()][coords.getX()] = val;
		}
		// prints the values of the map in a 2-dimensional matrix to a txt file of the given name
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
	
	public static TrainingLetters initTrainingLetters(){
		TrainingLetters tl = deSerialize();
		char[] missing = tl.checkForMissingCharacters();
		tl = getTrainingLetters(missing, tl);
		return tl;
	}
	
	public static void main(String[] args){
		// This is the picture that is read in (for debugging only)
		String tbrDir = new String("../ocr/toberead.jpg");
		Picture pic = new Picture(tbrDir);
		
		TrainingLetters tl = initTrainingLetters();
		
		// trims, darkens, and turns to grey-scale the picture to be read
		pic = prepPicture(pic);
		// show picture to be read in a new window (debugging purposes only)
		PicFrame f = new PicFrame(pic);
		f.buildFrame();
		// find the closest match to the picture to be read
		ImageMap result = closestMatch(createImageMap(pic), tl);
		// create new serial files 
		serializeTLMaps(tl);
		// print some info to console
		System.out.println("Total trainers: " + li.getIteration('z'));
		System.out.println("Guess:" +result.getChar());
	}
	// prepares an image by turning to greyscale, changing contrast, and trimming the picture.
	public static Picture prepPicture(Picture pic){
		if(pic == null) System.out.println("null pic");
		pic.convertToGreyscale();
		pic.absoluteGreyscaleContrast();
		pic.trimAspectSecure(.4);
		pic.scalePicture(PIC_SIZE, PIC_SIZE);
		return pic;
	}
	// grabs training letters as .jpg's, prepares them, then creates an ImageMap
	public static TrainingLetters getTrainingLetters(char[] neededLetters, TrainingLetters tl){
		// points to directory containing training images
		File dir = new File("../TrainingPictures");
		// iterates over every file in directory
		for (File child : dir.listFiles()) {
			// gets the last 4 letters of every file
			String type = child.getName().substring((int)child.getName().length() - 4, (int)child.getName().length());
		  	// gets the file name without the last 4 letters.
			String name = child.getName().substring(0, (int)child.getName().length() - 4);
		  	ImageMap im;
		  	// only applies to .jpg file types
		  	if(type.equals(".jpg")){
		  		char underscore = '_';
		  		char fileChar;
		  		// checks if file contains an uppercase or lowercase letter (uppercase begins with an underscore)
		  		if(name.charAt(0) != underscore) {
		  			fileChar = name.charAt(0);
				} else {
					fileChar = name.charAt(1);
				}
		  		if(isInArray(fileChar, neededLetters) ){
			  		// preps image.
			  		Picture temp = prepPicture(new Picture(child.getPath()));
			  		im = createImageMap(temp, fileChar);
			  		tl.addImageMap(im);
			  		System.out.println("File: " + name);
		  		}
		  	}
		}
		return tl;
	}
	// creates an ImageMap from a given picture
	public static ImageMap createImageMap(Picture p, char c){
		// considering making ImageMap its own file to prevent having to do workarounds like this
		ImageMap im = me.new ImageMap();
		// iterates over every pixel in an image
		for(int j = 0; j < p.getHeight(); j++){
			for(int i = 0; i < p.getWidth(); i++){
				Coords coords = new Coords(i,j);
				// sets the corresponding point in ImageMap to the grey value of the pixel
				im.setPoint(coords, p.getPixelGrey(coords));
			}
		}
		im.setChar(c);
		return im;
	}
	public static ImageMap createImageMap(Picture p){
		ImageMap im = me.new ImageMap();
		for(int j = 0; j < p.getHeight(); j++){
			for(int i = 0; i < p.getWidth(); i++){
				Coords coords = new Coords(i,j);
				im.setPoint(coords, p.getPixelGrey(coords));
			}
		}
		return im;
	}
	// gets the distance between two pictures
	public static int euclideanPictureDistance(ImageMap im1, ImageMap im2){
		int sumSqr = 0;
		for(int j = 0; j < PIC_SIZE; j++){
			for(int i = 0; i < PIC_SIZE; i++){
				sumSqr += Math.pow((im2.getPoint(new Coords(i,j)) - im1.getPoint(new Coords(i,j))), 2);
			}
		}
		return (int)Math.sqrt(sumSqr);
	}
	// finds the TrainingLetter with the closest ImageMap to the given image
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
				String fileName = new String();
				if(Character.isUpperCase(c)){
					fileName = "../SerialFiles/_" + c + ".ser";
				} else {
					fileName = "../SerialFiles/" + c + ".ser";
				}
				
				tl.getImageMap(i).iter = li.getIteration(c);
				FileOutputStream fout = new FileOutputStream(fileName);
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
	public static boolean isInArray(char c, char[] charArray){
		for(char working : charArray){
			if(working == c){
				return true;
			}
		}
		return false;
	}
	// returns a new array without given character.
	public char[] remove(char c, char[] charArray){
		char[] result = new char[charArray.length - 1];
		int i = 0;
		for(char working : charArray){
			if(working != c){
				result[i] = working;
			}
			i++;
		}
		return result;
	}
}

