package tigrantsat.me.read_tiff;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Hello world!
 * 
 */
public class App {
    public static void main(String[] args) throws IOException {
        File inputFile = new File(
                "path to my tif file");

        System.out.println("Completed");
    }

    public static void readUsingImageIO(File inputFile) throws IOException {
        BufferedImage image = ImageIO.read(inputFile);
        if (image == null) {
            throw new RuntimeException("Read failed");
        }
        ImageIO.write(image, "bmp", new File("/tmp/out.bmp"));
    }
}
