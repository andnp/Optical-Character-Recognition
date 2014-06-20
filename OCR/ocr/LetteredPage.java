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
}
