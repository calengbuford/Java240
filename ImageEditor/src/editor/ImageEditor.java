package editor;

import com.sun.jdi.IntegerValue;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
import java.lang.Math;

@SuppressWarnings("CheckStyle")
public class ImageEditor {
  // Class variables
  public String fileInputName = "";
  public String fileOutputName = "";
  public String imageEditType = "";
  public int blurLength = 0;

  private ImageEditor(String fileInputName, String fileOutputName, String imageEditType, final int blurLength) {
    this.fileInputName = fileInputName;
    this.fileOutputName = fileOutputName;
    this.imageEditType = imageEditType;
    this.blurLength = blurLength;
  }

  public static void main(final String[] args) {
    // Check the number of arguments passed.
    if (args.length != 3 && args.length != 4) {
      System.out.println("USAGE: java ImageEditor: INCORRECT NUMBER OF ARGUMENTS PASSED");
      return;
    }

    String fileInputName = args[0];
    String fileOutputName = args[1];
    String imageEditType = args[2];
    int blurLength = 0;

    // Check if a 4th argument was passed for motionblur edit type.
    if (args.length < 4 && "motionblur".equals(imageEditType)) {
      System.out.printf("USAGE: java ImageEditor %s %s %s", fileInputName, fileOutputName, imageEditType);
      return;
    }
    else if (args.length == 4) {
      if ("motionblur".equals(imageEditType)) {
        blurLength = Integer.parseInt(args[3]);
      }
      else {
        System.out.printf("USAGE: java ImageEditor %s %s %s: TWO MANY ARGUMENTS PASSED", fileInputName, fileOutputName, imageEditType);
        return;
      }
    }

    // Construct ImageEditor object for editing fileInputName.
    ImageEditor imageEditor = new ImageEditor(fileInputName, fileOutputName, imageEditType, blurLength);

    // Create image
    Image srcImage = imageEditor.createImage(fileInputName);

    // Edit file according to imageEditType and create new file
    if ("invert".equals(imageEditType)) {
      Image newImage = imageEditor.Invert(srcImage);
      newImage.writeToFile(fileOutputName);
    }
    else if ("grayscale".equals(imageEditType)) {
      Image newImage = imageEditor.GrayScale(srcImage);
      newImage.writeToFile(fileOutputName);
    }
    else if ("emboss".equals(imageEditType)) {
      Image newImage = imageEditor.Emboss(srcImage);
      newImage.writeToFile(fileOutputName);
    }
    else if ("motionblur".equals(imageEditType)) {
      Image newImage = imageEditor.MotionBlur(srcImage, blurLength);
      newImage.writeToFile(fileOutputName);
    }

  }


  // Read .ppm file and and build a new Image object
  public Image createImage(String fileInputName) {
    try {
      // Read and begin scanning .ppm file
      File inputFile = new File(fileInputName);
      Scanner in = new Scanner(inputFile);
      in.next(); // Move past P3

      // Set height and width of image
      int width = Integer.parseInt(in.next());
      int height = Integer.parseInt(in.next());
      int max = Integer.parseInt(in.next());
      Image image = new Image(height, width);

      // Scan for all pixels
      for (int h = 0; h < height; h++) {
        for (int w = 0; w < width; w++) {
          String data = in.next();

          // Ignore comments
          if ("#".equals(data)) {
            in.nextLine();
            data = in.next();
          }

          // Create a new pixel and add it to the image
          int red = Integer.parseInt(data);
          int green = Integer.parseInt(in.next());
          int blue = Integer.parseInt(in.next());
          Pixel pixel = new Pixel(red, green, blue);
          image.addPixel(pixel, h, w);
        }
      }
      in.close();

      return image;
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }


  public Image Invert(Image srcImage) {
    Image newImage = new Image(srcImage.height, srcImage.width);

    // Invert every pixel
    for (int h = 0; h < newImage.height; h++) {
      for (int w = 0; w < newImage.width; w++) {
        int red = srcImage.image[h][w].red;
        int green = srcImage.image[h][w].green;
        int blue = srcImage.image[h][w].blue;
        newImage.image[h][w] = new Pixel(255 - red, 255 - green, 255 - blue);
      }
    }
    return newImage;
  }


  public Image GrayScale(Image srcImage) {
    Image newImage = new Image(srcImage.height, srcImage.width);

    // Average every pixel to grayscale it
    for (int h = 0; h < newImage.height; h++) {
      for (int w = 0; w < newImage.width; w++) {
        int red = srcImage.image[h][w].red;
        int green = srcImage.image[h][w].green;
        int blue = srcImage.image[h][w].blue;
        int avg = (red + green + blue) / 3;
        newImage.image[h][w] = new Pixel(avg, avg, avg);
      }
    }
    return newImage;
  }


  public Image Emboss(Image srcImage) {
    Image newImage = new Image(srcImage.height, srcImage.width);

    for (int h = newImage.height - 1; h >= 0; h--) {
      for (int w = newImage.width - 1; w >= 0; w--) {
        int v;
        // Check if on the leftest column or top row
        if (h == 0 || w == 0) {
          v = 128;
        }
        else {
          // Take the difference between current pixel and the upper-left corner pixel
          int redDiff = srcImage.image[h][w].red - srcImage.image[h - 1][w - 1].red;
          int greenDiff = srcImage.image[h][w].green - srcImage.image[h - 1][w - 1].green;
          int blueDiff = srcImage.image[h][w].blue - srcImage.image[h - 1][w - 1].blue;

          // Find the max of redDiff, greenDiff, blueDiff. If equal, take red first, then green, then blue.
          int maxDiff = redDiff;
          if (Math.abs(redDiff) < Math.abs(greenDiff)) {
            maxDiff = greenDiff;
            if (Math.abs(greenDiff) < Math.abs(blueDiff)) {
              maxDiff = blueDiff;
            }
          } else if (Math.abs(redDiff) < Math.abs(blueDiff)) {
            maxDiff = blueDiff;
          }

          // Scale the maxDiff by 128 or back into range 0-255 as needed
          v = maxDiff + 128;
          if (v < 0) {
            v = 0;
          } else if (v > 255) {
            v = 255;
          }
        }
        newImage.image[h][w] = new Pixel(v, v, v);
      }
    }
    return newImage;
  }


  public Image MotionBlur(Image srcImage, int blurLength) {
    Image newImage = new Image(srcImage.height, srcImage.width);

    for (int h = 0; h < newImage.height; h++) {
      for (int w = 0; w < newImage.width; w++) {
        // Determine if blurLength extends past the width of the image
        int blur = blurLength;
        if (w + blurLength > newImage.width) {
          blur = newImage.width - w;
        }

        // Take the averages of the pixels from current to blur
        int avgRed = 0;
        int avgGreen = 0;
        int avgBlue = 0;
        for (int i = 0; i < blur; i++) {
          avgRed += srcImage.image[h][w + i].red;
          avgGreen += srcImage.image[h][w + i].green;
          avgBlue += srcImage.image[h][w + i].blue;
        }
        avgRed /= blur;
        avgGreen /= blur;
        avgBlue /= blur;
        newImage.image[h][w] = new Pixel(avgRed, avgGreen, avgBlue);
      }
    }
    return newImage;
  }

}