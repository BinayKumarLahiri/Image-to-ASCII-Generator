import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;

public class Renderer {
  private int width = 200;
  private int height = 100;
  private BufferedImage image;
  private double[][] pixelData;
  private char[] pixels = { ' ', '.', ':', '-', '=', '+', '*', '%', '#', '@' };

  /**
   * NOTES:
   * RGB = RRRRRRRRGGGGGGGGBBBBBBBB (8Bits: Red, 8Bits: Green, 8Bits: Blue)
   * Getting the color values:
   * Blue: (getrgb() && 0xff)
   * Green: (getrgb() && 0xff00) >> 8
   * Red: (getrgb() && 0xff0000) >> 16
   * 
   * 
   * Getting the average cummulative Brightness value using the rgb value
   * Brightness = (0.21)*Red + (0.72)*Green + (0.07)*Blue
   * The Brightness value will be a number >=0.0 && <=1.0
   * 0.0: Fully dark
   * 1.0: Fully Bright
   */

  /**
   * Renderer(): Overloaded Constructor for the Renderer Class
   * 
   * @param filePath : Sets the Image's File path in the renderer
   * @param width    : Sets the Width to be drawn in the terminal if not passed
   *                 Default value is used
   * @param height   : Sets the Height to be drawn in the terminal if not passed
   *                 Default value is used
   * 
   *                 width and height are also used to resize the image, width and
   *                 height are not the actual sizes of the images
   *                 These are the sizes to which the image will be resized
   *                 Greater width & height will result in higher memory usage and
   *                 time requirement
   */

  public Renderer(String filePath) {
    // @pixelData: Used to initialize the pixel data array: Stores all the
    // brightness value of every pixel
    this.pixelData = new double[width][height];
    try {
      // Creating a file object for creation of BufferedImage Object
      File img = new File(filePath);
      this.image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
      this.image = ImageIO.read(img); // Reading the File object into the BufferedImage object
      System.out.println("File Reading completed...");
      this.image = resize(image, width, height); // Reinitializing the image BufferedImage Object by the resized image
      System.out.println("Resize Completed...");
    } catch (IOException exp) {
      System.out.println("Can't read the file: " + exp);
    }
  }

  public Renderer(String filePath, int width, int height) {
    this.width = width;
    this.height = height;

    // @pixelData: Used to initialize the pixel data array: Stores all the
    // brightness value of every pixel
    this.pixelData = new double[width][height];
    try {
      // Creating a file object for creation of BufferedImage Object
      File img = new File(filePath);
      this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      this.image = ImageIO.read(img); // Reading the File object into the BufferedImage object
      System.out.println("File Reading completed...");
      this.image = resize(image, width, height); // Reinitializing the image BufferedImage Object by the resized image
      System.out.println("Resize Completed...");
    } catch (IOException exp) {
      System.out.println("Can't read the file: " + exp);
    }
  }

  /**
   * @param image  : Original Image to be resized
   * @param width  : Tartget width
   * @param height : Target Height
   * @return returns the resized image in form of a BufferedImage instance
   */
  public static BufferedImage resize(BufferedImage image, int width, int height) {
    BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics2D = resizedImage.createGraphics();
    graphics2D.drawImage(image, 0, 0, width, height, null);
    graphics2D.dispose();
    return resizedImage;
  }

  public void getPixelData() {
    for (int row = 0; row < this.height; row++) {
      for (int col = 0; col < width; col++) {
        int rgb = this.image.getRGB(col, row);
        int red = (rgb & 0xff0000) >> 16;
        int green = (rgb & 0xff00) >> 8;
        int blue = rgb & 0xff;
        double brightness = (0.21 * red + 0.72 * green + 0.07 * blue) / 255;
        this.pixelData[col][row] = brightness;
      }
    }
  }

  public void drawBlackAndWhite() {
    getPixelData();
    for (int row = 0; row < this.height; row++) {
      for (int col = 0; col < this.width; col++) {
        double pixelBrightness = this.pixelData[col][row];
        int index = (int) Math.floor(pixelBrightness * 10) == 10 ? 9 : (int) Math.floor(pixelBrightness * 10);
        System.out.print(this.pixels[index]);
      }
      System.out.println();
    }
  }

  public void drawColored() {
    for (int row = 0; row < this.height; row++) {
      for (int col = 0; col < width; col++) {
        int rgb = this.image.getRGB(col, row);
        /**
         * RGB = RRRRRRRRGGGGGGGGBBBBBBBB (8Bits: Red, 8Bits: Green, 8Bits: Blue)
         * Getting the color values:
         * Blue: (getrgb() && 0xff)
         * Green: (getrgb() && 0xff00) >> 8
         * Red: (getrgb() && 0xff0000) >> 16
         */
        int red = (rgb & 0xff0000) >> 16;
        int green = (rgb & 0xff00) >> 8;
        int blue = rgb & 0xff;
        /**
         * Getting the average cummulative Brightness value using the rgb value
         * Brightness = (0.21)*Red + (0.72)*Green + (0.07)*Blue
         * The Brightness value will be a number >=0.0 && <=1.0
         */
        double pixelBrightness = (0.21 * red + 0.72 * green + 0.07 * blue) / 255;
        // Calculating the index of the character to be printed based on the brightness
        // value
        int index = (int) Math.floor(pixelBrightness * 10) == 10 ? 9 : (int) Math.floor(pixelBrightness * 10);
        /**
         * Getting the ANSI Color codes to print the characters in color in the terminal
         * 
         * @NOTE: Generally emulated terminals inside most ide's may not support colors,
         *        in that case
         *        one must use another terminal to get the colored outputs correctly
         * 
         *        These ANSI Codes uses Escape sequence characters,Generally its
         *        "\u001b["
         *        Reset Color: "\u001b[0m"
         *        Using RGB values in the ANSI codes
         *        Text/Foreground: "\u001b[38;2;<Red value>;<Green Value>;<Blue value>m"
         */
        String clear = "\u001b[0m";
        String coloString = null;
        if (red <= 255 && green <= 255 && blue <= 255 && red >= 0 && green >= 0 && blue >= 0)
          coloString = "\u001B[38;2;" + red + ";" + green + ";" + blue + "m";
        else
          coloString = "\u001B[38;2;255;255;255m";
        System.out.print(coloString + this.pixels[index] + clear);
      }
      System.out.println();
    }
  }

  public void draw(boolean isColored) {
    if (isColored) {
      drawColored();
    } else {
      drawBlackAndWhite();
    }
  }

}
