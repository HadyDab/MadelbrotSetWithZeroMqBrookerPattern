/**
 * 
 */
package loadbalancingbroker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import loadbalancingbroker.zmqutils.MadelBrotResponce;
import loadbalancingbroker.zmqutils.MadelBrotSet;

/**
 * @author hamzahassan
 *
 */
public class MadelBrotService {

	public static Image getMadelBrotImage(MadelBrotResponce responce) throws IOException {
		if (responce == null) {
			System.out.println("No responce");
			return null;
		}
		
		

		BufferedImage madelBrotImage = new BufferedImage(responce.width, responce.height, BufferedImage.TYPE_INT_RGB);
		int[] colors = new int[responce.maxIteration];
		for (int i = 0; i < responce.maxIteration; i++) {
			colors[i] = Color.HSBtoRGB(i / 256f * 20, 1, i / (i + 40f) * 80);
		}

		for (MadelBrotSet ms : responce.madelBrotSets) {
			if (ms.iteration < responce.maxIteration) {
				madelBrotImage.setRGB(ms.col, ms.row, colors[ms.iteration]);
			} else {
				madelBrotImage.setRGB(ms.col, ms.row, Color.YELLOW.getRGB());
			}
		}
		
	
		
		Image madelbrotImageFX = SwingFXUtils.toFXImage(madelBrotImage, null);

		return madelbrotImageFX;

//		ImageIO.write(madelBrotImage, "png", new File("mandelbrot.png"));
	}

	public static Canvas getMadelBrotCanvas(MadelBrotResponce responce) throws IOException {
		if (responce == null) {
			System.out.println("No responce");
			return null;
		}

		Canvas canvas = new Canvas(responce.width, responce.height);
		GraphicsContext ctx = canvas.getGraphicsContext2D();
		PixelWriter pxwriter = ctx.getPixelWriter();

		javafx.scene.paint.Color[] colors2 = new javafx.scene.paint.Color[responce.maxIteration];
		for (int i = 0; i < responce.maxIteration; i++) {
			colors2[i] = javafx.scene.paint.Color.hsb(i / 255d * 0.80, 1, i / (i + 90d) * 0.8776);
		}

		for (MadelBrotSet ms : responce.madelBrotSets) {
			if (ms.iteration < responce.maxIteration) {
				pxwriter.setColor(ms.col, ms.row, colors2[ms.iteration]);
			} else {
				pxwriter.setColor(ms.col, ms.row, javafx.scene.paint.Color.YELLOW);
			}
		}
		return canvas;
	}

	
	public static boolean saveMadelBrotImage(MadelBrotResponce responce,String output) throws IOException {
		if (responce == null) {
			System.out.println("No responce");
			return false;
		}

		BufferedImage madelBrotImage = new BufferedImage(responce.width, responce.height, BufferedImage.TYPE_INT_RGB);
		int[] colors = new int[responce.maxIteration];
		for (int i = 0; i < responce.maxIteration; i++) {
			colors[i] = Color.HSBtoRGB(i / 256f * 20, 1, i / (i + 40f) * 80);
		}

		for (MadelBrotSet ms : responce.madelBrotSets) {
			if (ms.iteration < responce.maxIteration) {
				madelBrotImage.setRGB(ms.col, ms.row, colors[ms.iteration]);
			} else {
				madelBrotImage.setRGB(ms.col, ms.row, Color.YELLOW.getRGB());
			}
		}
		
		return ImageIO.write(madelBrotImage, "png", new File(output));

	}
	

}
