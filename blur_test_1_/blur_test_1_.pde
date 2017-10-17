PImage img;
int radius = 7;
void setup() {
  size(1920, 1080);
  img = loadImage("Landscape.jpg"); // Load the original image
  noLoop();
} 

void draw() {

  image(img, 0, 0); // Displays the image from point (0,0) 
  img.loadPixels();

  // Create an opaque image of the same size as the original
  PImage edgeImg = createImage(img.width, img.height, RGB);

  // Loop through every pixel in the image
  for (int y = radius/2; y < img.height-radius/2; y++) {   // Skip top and bottom edges
    for (int x = radius/2; x < img.width-radius/2; x++) {  // Skip left and right edges
      float sumR = 0; // Kernel sum for this pixel
      float sumG = 0;
      float sumB = 0;
      for (int rY = -int(radius/2); rY <= int(radius/2); rY++) {
        for (int rX = -int(radius/2); rX <= int(radius/2); rX++) {
          // Calculate the adjacent pixel for this kernel point
          int pos = (y + rY)*img.width + (x + rX);
          // Image is grayscale, red/green/blue are identical
          float valR = red(img.pixels[pos]);
          float valG = green(img.pixels[pos]);
          float valB = blue(img.pixels[pos]);
          // Multiply adjacent pixels based on the kernel values
          sumR += valR;
          sumG += valG;
          sumB += valB;
        }
      }
      // For this pixel in the new image, set the gray value
      // based on the sum from the kernel
      edgeImg.pixels[y*img.width + x] = color(sumR/(radius*radius), sumG/(radius*radius), sumB/(radius*radius));
    }
  }
  // State that there are changes to edgeImg.pixels[]
  edgeImg.updatePixels();

  image(edgeImg, 0, 0); // Draw the new image
}