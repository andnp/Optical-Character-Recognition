package edgedetection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Picture {
	private BufferedImage pic;

	Picture() {
		try {
			File file = new File(
					"C://Users/Andy/workspace/ImageProcessing/src/edgedetection/BlackSquare.jpg");
			pic = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	Picture(String filepath) {
		try {
			File file = new File(filepath);
			pic = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	Picture(BufferedImage img){
		pic = img;
	}
	public Picture copyImage(){
		BufferedImage source = this.pic;
	    BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
	    Graphics g = b.getGraphics();
	    g.drawImage(source, 0, 0, null);
	    g.dispose();
	    return new Picture(b);
	}
	public int getHeight() {
		return pic.getHeight();
	}
	public int getWidth() {
		return pic.getWidth();
	}
	public Color getPixelColor(Coords coords){
		int rgb = pic.getRGB(coords.getX(), coords.getY());
		Color c = new Color(rgb);
		return c;
	}
	public int getPixelGrey(Coords coords){
		Color c = this.getPixelColor(coords);
		int grey = (c.getBlue()+c.getRed()+c.getGreen()) / 3;
		return grey;
	}
	public void setPixelColor(Coords coords, Color c){
		this.pic.setRGB(coords.getX(), coords.getY(), c.getRGB());
	}
	public BufferedImage getPic(){
		return this.pic;
	}
	public void absoluteGreyscaleContrast() {
		// find darkest and brightest pixel values in picture
		int darkest = 300, brightest = 0, cur = 0;
		for (int i = 0; i < this.getWidth(); i++) {
			for (int j = 0; j < this.getHeight(); j++) {
				Coords coords = new Coords(i, j);
				cur = this.getPixelGrey(coords);
				if (cur < darkest)
					darkest = cur;
				if (cur > brightest)
					brightest = cur;
			}
		}

		// set all pixels below average to white, all above black
		int avg = (darkest + brightest) / 2;
		for (int i = 0; i < this.getWidth(); i++) {
			for (int j = 0; j < this.getHeight(); j++) {
				Coords coords = new Coords(i, j);
				int g = this.getPixelGrey(coords);
				if (g < avg)
					this.setPixelColor(coords, Color.black);
				if (g > avg)
					this.setPixelColor(coords, Color.white);
			}
		}
	}
	public void convertToGreyscale() {
		for (int i = 0; i < this.getWidth(); i++) {
			for (int j = 0; j < this.getHeight(); j++) {
				Coords coords = new Coords(i, j);
				int g = this.getPixelGrey(coords);
				this.setPixelColor(coords, new Color(g, g, g));
			}
		}
	}
	public void scalePicture(int width, int height) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.drawImage(this.getPic(), 0, 0, width, height, null);
		g.dispose();
		this.pic = bi;
	}
	public void trimPicture(double threshold) {
		double maxDist = Math.sqrt(3*(255*255));
		Color baseColor = new Color(this.getPic().getRGB(0, 0));
		int w = this.getWidth();
		int h = this.getHeight();
		int topY = Integer.MAX_VALUE, topX = Integer.MAX_VALUE;
		int bottomY = -1, bottomX = -1;

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if ((euclideanColorDistance(new Color(this.getPic().getRGB(x, y)), baseColor)/maxDist) > threshold) {
					if (x < topX)
						topX = x;
					if (y < topY)
						topY = y;
					if (x > bottomX)
						bottomX = x;
					if (y > bottomY)
						bottomY = y;
				}
			}
		}
		
		topX = (int)(topX * .97);
		topY = (int)(topY * .97);
		bottomX = (int)(bottomX * 1.03);
		bottomY = (int)(bottomY * 1.03);
		if(bottomX > this.getWidth()) bottomX = this.getWidth();
		if(bottomY > this.getHeight()) bottomY = this.getHeight();
		if(topX > this.getWidth()) topX = this.getWidth();
		if(topY > this.getHeight()) topY = this.getHeight();


		BufferedImage destination = new BufferedImage((bottomX - topX + 1),
				(bottomY - topY + 1), BufferedImage.TYPE_INT_ARGB);

		destination.getGraphics().drawImage(this.getPic(), 0, 0,
				destination.getWidth(), destination.getHeight(), topX, topY,
				bottomX, bottomY, null);
		this.pic = destination;
	}
	public void trimAspectSecure(double threshold) {
		double maxDist = Math.sqrt(3*(255*255));
		Color baseColor = new Color(this.getPic().getRGB(0, 0));
		int w = this.getWidth();
		int h = this.getHeight();
		int topY = Integer.MAX_VALUE, topX = Integer.MAX_VALUE;
		int bottomY = -1, bottomX = -1;

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if ((euclideanColorDistance(new Color(this.getPic().getRGB(x, y)), baseColor)/maxDist) > threshold) {
					if (x < topX)
						topX = x;
					if (y < topY)
						topY = y;
					if (x > bottomX)
						bottomX = x;
					if (y > bottomY)
						bottomY = y;
				}
			}
		}
		
		topX = (int)(topX * .97);
		topY = (int)(topY * .97);
		bottomX = (int)(bottomX * 1.03);
		bottomY = (int)(bottomY * 1.03);
		if(bottomX > this.getWidth()) bottomX = this.getWidth();
		if(bottomY > this.getHeight()) bottomY = this.getHeight();
		if(topX > this.getWidth()) topX = this.getWidth();
		if(topY > this.getHeight()) topY = this.getHeight();

		int newWidth = (bottomX - topX + 1);
		int newHeight = (bottomY - topY + 1);
		
		if(newWidth > newHeight){
			newHeight = newWidth;
		} else {
			newWidth = newHeight;
		}
		
		BufferedImage destination = new BufferedImage(newWidth,
				newHeight, BufferedImage.TYPE_INT_ARGB);
		destination.getGraphics().drawImage(this.getPic(), 0, 0,
				destination.getWidth(), destination.getHeight(), topX, topY,
				bottomX, bottomY, null);		
		this.pic = destination;
	}
	public double euclideanColorDistance(Color c1,Color c2){
		int a = c1.getRGB(), b = c2.getRGB();
	    int aRed    = (int)((a & 0x00FF0000) >>> 16);   // Red level
	    int aGreen  = (int)((a & 0x0000FF00) >>> 8);    // Green level
	    int aBlue   = (int)(a & 0x000000FF);            // Blue level

	    int bRed    = (int)((b & 0x00FF0000) >>> 16);   // Red level
	    int bGreen  = (int)((b & 0x0000FF00) >>> 8);    // Green level
	    int bBlue   = (int)(b & 0x000000FF);
		return Math.sqrt((aRed-bRed)*(aRed-bRed) +
						(aGreen-bGreen)*(aGreen-bGreen) +
						(aBlue-bBlue)*(aBlue-bBlue));
	}
}
