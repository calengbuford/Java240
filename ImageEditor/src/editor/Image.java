package editor;

import java.io.BufferedWriter;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.PrintWriter;   // Import the FileWriter class

public class Image {
  public Pixel[][] image;
  public int height;
  public int width;

  public Image(int height, int width) {
    image = new Pixel[height][width];
    this.height = height;
    this.width = width;
  }

  public void writeToFile(String outputFileName) {
    try {
      File outFile = new File(outputFileName);
      PrintWriter writer = new PrintWriter(outFile);

      writer.println("P3");
      writer.printf("%d %d\n", this.width, this.height);
      writer.println("255");

      for (int h = 0; h < this.height; h++) {
        for (int w = 0; w < this.width; w++) {
          writer.printf("%d %d %d\n", this.image[h][w].red, this.image[h][w].green, this.image[h][w].blue);
        }
      }
      writer.close();
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void addPixel(Pixel pixel, int h, int w) {
    this.image[h][w] = pixel;
  }

}
