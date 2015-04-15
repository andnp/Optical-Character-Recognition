package ocr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class LetteredPage {
	BufferedImage page;
	
	LetteredPage(String filepath){
		try{
			File file = new File(filepath);
			page = ImageIO.read(file);
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		// This is the picture that is read in (for debugging only)
		String tbrDir = new String("toberead.jpg");
		Picture pic = new Picture(tbrDir);
		
		LetterReader debugger = new LetterReader();
		
		debugger.initTrainingLetters();
		char result = debugger.read(pic);
		debugger.shutdownLetterReader();
		System.out.println("Guess:" +result);
	}
}
