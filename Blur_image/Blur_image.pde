PImage img, original, blur;
float cutoff = 2000;
int scale = 270;
import ddf.minim.*;
import ddf.minim.effects.*;
import ddf.minim.ugens.*;
Minim minim;
AudioOutput output;
FilePlayer  music;
LowPassFS   lpf;
BitCrush bitCrush;

void setup() {
  colorMode(RGB);
  img = loadImage("park.jpg"); //load image
  original = img; //keep the original unmodified
  image(img, 0, 0); //desplay to screen 
  surface.setSize(img.width, img.height+50); //define size of screen
  frameRate(30); 
  textSize(10);
  //Audio Declirations
  minim = new Minim(this);
  output = minim.getLineOut();
  music = new FilePlayer( minim.loadFileStream("music.mp3") );
  lpf = new LowPassFS(100, output.sampleRate());
  music.patch( lpf ).patch( output );
  music.loop();
  scale = height/3;
}
//initialize variables
float c = 1.1; //scale constant for brightness
int maximumRadius = 10; //set maxiumim blur radius
int brightness = 0; 
int saturation = 0;
boolean pixilated = false;
boolean effectStart = false;
boolean playOnce = true;
void draw() {
  background(0);
  if (effectStart) { //start effect when mouse is clicked
    blur = blured(img, maximumRadius);
    image(blur, 0, 0);
    text("Radius (pixilate + blur): " + maximumRadius, 0, height- 30);
    text("Saturation: " + saturation/img.pixels.length, 0, height - 20);
    text("Brightness: " + brightness/img.pixels.length, 0, height - 10);
    if (pixilated) { 
      text("Pixilated", 0, height);
    }
  } else {
    image(img, 0, 0);
  }
  //Audio line drawing
  stroke(255);
  for ( int i = 0; i < output.bufferSize() - 1; i++ ) {
    float x1 = map(i, 0, output.bufferSize(), 0, width);
    float x2 = map(i+1, 0, output.bufferSize(), 0, width);
    line(x1, 3*height/5+scale - output.right.get(i)*50, x2, 3*height/5+scale - output.right.get(i+1)*50);
  }
}
void keyReleased() { 
  //controle effect 
  //b=brighten, s=saturate,p = pixilate,o = original,r = radius
  if (key == 'b') {
    img = brighten(img, true);
  }
  if (key == 'v') {
    img = brighten(img, false);
  }
  if (key == 's') {
    img = saturate(img, true);
  }
  if (key == 'a') {
    img = saturate(img, false);
    saturation --;
  }
  if (key == 'p') {
    img = pixilate(img, maximumRadius);
    pixelateAudio();
  }
  if (key == 'o') {
    img = original;
    saturation = 0;
    brightness = 0;
    normalAudio();
    pixilated = false;
  }
  if (key == 'r') {
    maximumRadius++;
  }
  if (key == 'e') {
    maximumRadius--;
  }
}
PImage brighten(PImage img, boolean increase) {
  PImage brighten = createImage(img.width, img.height, RGB);
  for (int i = 0; i<img.pixels.length; i++) {
    float blue = blue(img.pixels[i]);//get all 3 color values
    float green = green(img.pixels[i]); 
    float red = red(img.pixels[i]);
    color brightened; 
    if (increase) { //determine if we want to increase or decrease brightness
      brightened = color(red*c, green*c, blue*c); //multiply by constant
      brightness ++;//number desplayed on screen
    } else {
      brightened = color(red/c, green/c, blue/c);
      brightness --;
    }
    brighten.pixels[i] = brightened; //replace pixel
  }
  brighten.updatePixels();
  return brighten;
}
PImage saturate(PImage img, Boolean increase) {
  loadPixels();
  PImage saturate = createImage(img.width, img.height, RGB);
  for (int i = 0; i<img.pixels.length; i++) {
    colorMode(HSB); //convert image to HSB mode
    float b = brightness(img.pixels[i]);//get HSB values
    float s = saturation(img.pixels[i]); 
    float h = hue(img.pixels[i]);
    color saturatedC; 
    saturatedC = color(h, s*c, b);//multiply by constant C
    if (increase) { //use boolean to detemine if increaseing or decreasing
      saturatedC = color(h, s*c, b);
      saturation ++;
    } else {
      saturatedC = color(h, s/c, b);
      saturation --;
    }
    saturate.pixels[i] = saturatedC; //replace pixle with saturated one
    colorMode(RGB);
  }
  saturate.updatePixels();
  return saturate; //return saturaged image
}
boolean inBounds(PImage img, int x, int y) { //detemin if pixle is inbounse
  return x >= 0 && y >= 0 && x < img.width && y < img.height;
}
PImage blured(PImage img, int maxradius) {
  PImage blur = createImage(img.width, img.height, RGB);
  blur.loadPixels();
  img.loadPixels();
  float sumR, sumG, sumB;
  float radius = maxradius;
  for (int y = 0; y < img.height; y++) {//skip the edges on top and bottom
    for (int x = 0; x < img.width; x++) { //left and right
      sumR = 0; //reset sums between radius
      sumG = 0;
      sumB = 0;
      int count = 0;
      radius = abs(abs((maxradius * (y * 1f / img.height - mouseY * 1f / height ))) ) ;
      for (int rY = -int (radius); rY <= int(radius); rY++) { //loop through the radius around the pixle
        for (int rX = -int (radius); rX <= int(radius); rX++) {//y portion of above
          if (inBounds(img, x+rX, y+rY) && sqrt(sq(rX)+sq(rY)) <= radius) {
            int pos = location(y + rY, x + rX); //get location using abstracted method
            sumR += red(img.pixels[pos]); //sum rgb values
            sumG += green(img.pixels[pos]);
            sumB += blue(img.pixels[pos]);
            count++;
          }
        }
      }
      blur.pixels[y*img.width + x] = color(sumR/count, sumG/count, sumB/count);//average all rgb values since it is a square it is radius*radius
    }
  }
  blur.updatePixels();
  return blur;
}
PImage pixilate(PImage img, int radius) {
  pixilated = true;
  PImage pixilated = createImage(img.width, img.height, RGB);
  pixilated.loadPixels();
  img.loadPixels();
  float sumR, sumG, sumB;
  for (int y = radius/10; y < (img.height-radius/20)/radius; y++) {//skip the edges on top and bottom
    for (int x = radius/10; x < (img.width-radius/40)/radius; x++) { //left and right
      sumR = 0; //reset sums between radius
      sumG = 0;
      sumB = 0;
      for (int rY = -int (radius/2); rY <= int(radius/2); rY++) { //loop through the radius around the pixle
        for (int rX = -int (radius/2); rX <= int(radius/2); rX++) {//y portion of above
          int pos = location(y*radius + rY, x*radius + rX); //get location using abstracted method
          sumR += red(img.pixels[pos]); //sum rgb values
          sumG += green(img.pixels[pos]);
          sumB += blue(img.pixels[pos]);
        }
      }
      for (int rY = -int (radius/2); rY <= int(radius/2); rY++) { //loop through the radius around the pixle
        for (int rX = -int (radius/2); rX <= int(radius/2); rX++) {//y portion of above
          pixilated.pixels[location(y*radius + rY, x*radius + rX)] = color(sumR/(radius*radius), sumG/(radius*radius), sumB/(radius*radius));//average all rgb values since it is a square it is radius*radius
        }
      }
    }
  }
  return pixilated;
}
int location(int row, int col) {//method for determining pixel location
  return(row*width + col);
}

//Audio Method
void mouseMoved() {
  if (effectStart) //make the audio have the tiltshift effect
    cutoff = 400;
  lpf.setFreq(cutoff);
}
void mouseClicked() {
  effectStart = true; //start the effect
}
void pixelateAudio() {
  if (pixilated) { // if sound is not glitching patch in the echo
    if (playOnce) {
      bitCrush = new BitCrush(4, output.sampleRate()); //4 = bit res
      music.patch(bitCrush); //Crush The bit
      bitCrush.patch(output);
      playOnce = false;
    }
  }
}
void normalAudio() {
  if (pixilated) {
    music.unpatch(bitCrush);  //undo all audio Effects
    bitCrush.unpatch(output);
    music.patch(output);
    println("Normal");
    music.patch( lpf ).patch( output );
  } else {
    if (effectStart) {
      cutoff = 400;
    }
    lpf.setFreq(cutoff);
  }
}