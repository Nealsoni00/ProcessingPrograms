void setup() {
  size(900, 800);
  selectInput("Open a file: ", "fileSelected");
  surface.setSize(pic.width, pic.height);
}
PImage pic;
void fileSelected(File selection) {
  if (selection ==null) {
    println("u done wrong");
  } else {
    String filePath = selection.getAbsolutePath();
    pic = loadImage(filePath);
    size(pic.width, pic.height);
    greyScale(pic);
  }
}

PImage greyScale(PImage pic) {
  PImage greyScale = createImage(pic.width,pic.height, RGB);
  pic.loadPixels();
  greyScale.loadPixels();
  for (int x=0; x<pic.pixels.length; x++){
    int avg = int((red(pic.pixels[x])+green(pic.pixels[x])+blue(pic.pixels[x]))/3);
    color c  = color(avg,avg,avg);
    greyScale.pixels[x] = c;
    //for (int y=0; y<pic.pixels.height; y++){
    //  color c = pic.pixels[x][y];
    //  float red = red(c);
    //  float green = green(c);
    //  float blue = blue(c);
    //  float grey = (int)(red+green+blue)/3;
    //  greyScale.pixels[x][y] = grey;
    //}
  }
  return greyScale;
}

void draw() {
  background(255);
  if (pic != null) {
    if (keyPressed) {
      image(greyScale(pic), 0, 0);
    } else {
      image(pic, 0, 0);
    }
  }
}