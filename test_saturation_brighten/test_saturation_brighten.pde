PImage img;
void setup() {
  size(1920, 1080);
  colorMode(RGB);
  img = loadImage("Landscape.jpg");
  image(img, 0, 0);
}
float c = 1.1;
void draw() {
  //image(img, 0, 0);
  if (keyPressed) {
    loadPixels();
    if (key == '1') {
      brighten();
    }
    if (key == '2') {
      saturate();
    }
  }
  updatePixels();
}
void brighten() {
  loadPixels();
  for (int i = 0; i<pixels.length; i++) {
    float blue = blue(pixels[i]);
    float green = green(pixels[i]);
    float red = red(pixels[i]);
    color brightened = color(red*c, green*c, blue*c);
    pixels[i] = brightened;
    //float brightness = brightness(pixels[i]);
    //float s = saturation(pixels[i]);
    //float h = hue(pixels[i]);
  }
  updatePixels();
}
void saturate() {
  loadPixels();
  for (int i = 0; i<pixels.length; i++) {
    //colorMode(RGB);
    //float blue = blue(pixels[i]);
    //float green = green(pixels[i]);
    //float red = red(pixels[i]);


    colorMode(HSB);
    float b = brightness(pixels[i]);
    float s = saturation(pixels[i]);
    float h = hue(pixels[i]);
    color saturated = color(h, s*c, b);
    pixels[i] = saturated;
    colorMode(RGB);
  }
  updatePixels();
}