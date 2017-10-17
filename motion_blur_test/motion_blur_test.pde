PImage img;
PImage temp;
float[][] kernel = {{1, 2, 1}, {2, 4, 2}, {1, 2, 1}};
int blurRect = 50;
float blurRage = dist(blurRect-10, blurRect-10, 0, 0); //don't change this
void setup() {
  img=loadImage("Theo2.jpg.png");
  temp = createImage(img.width, img.height, ARGB);
  temp.set(0, 0, img);
  surface.setSize(img.width, img.height);
}
void draw() {
  image(blured(img, 100), 0, 0);
}
void keyReleased() {
  if (key == '1') {
    img = blured(img, 10);
  }
}
PImage blured(PImage img, float maxradius) {
  int imgWidth = img.width;
  PImage blur = createImage(img.width, img.height, RGB);
  blur.loadPixels();
  img.loadPixels();
  float sumR, sumG, sumB;
  int range = 100;
  float radius = maxradius;
  int maxRange = mouseY+range;
  int minRange = mouseY-range;
  //for (int y = int(maxradius/2); y < minRange; y++) {//skip the edges on top and bottom
  //  for (int x = int(radius/2); x < int(img.width-radius/2); x++) {
  //    if (y%200 == 0){
  //    radius --;
  //    }
  //    //radius = abs(int((minRange - y)/100));
  //    sumR = 0; //reset sums between radius
  //    sumG = 0;
  //    sumB = 0;
  //    for (int rY = -int(radius/2); rY <= int(radius/2); rY++) { //loop through the radius around the pixle
  //      for (int rX = -int(radius/2); rX <= int(radius/2); rX++) {//y portion of above
  //        int pos = location(y + rY, x + rX); //get location using abstracted method
  //        sumR += red(img.pixels[pos]); //sum rgb values
  //        sumG += green(img.pixels[pos]);
  //        sumB += blue(img.pixels[pos]);
  //      }
  //    }
  //    blur.pixels[y*img.width + x] = color(sumR/(radius*radius), sumG/(radius*radius), sumB/(radius*radius));//average all rgb values since it is a square it is radius*radius
  //  }
  //}
  for (int y = int(radius/2); y < int(img.height-radius/2); y++) {//skip the edges on top and bottom
    for (int x = int(radius/2); x < int(img.width-radius/2); x++) { //left and right
      //if (y%(img.height/4) == 0) {
      //  radius --;
      //}
      radius = abs(radius);
      sumR = 0; //reset sums between radius
      sumG = 0;
      sumB = 0;
      if (y > maxRange || y < minRange) {
        for (int rY = -int(radius/2); rY <= int(radius/2); rY++) { //loop through the radius around the pixle
          for (int rX = -int(radius/2); rX <= int(radius/2); rX++) {//y portion of above
            int pos = location(y + rY, x + rX); //get location using abstracted method
            sumR += red(img.pixels[pos]); //sum rgb values
            sumG += green(img.pixels[pos]);
            sumB += blue(img.pixels[pos]);
          }
        }
        blur.pixels[y*img.width + x] = color(sumR/(radius*radius), sumG/(radius*radius), sumB/(radius*radius));//average all rgb values since it is a square it is radius*radius
      } else {
        blur.pixels[y*img.width + x] = img.pixels[y*img.width + x];
      }
    }
  }
  blur.updatePixels();
  return blur;
}
int location(int row, int col) {
  return(row*width + col);
}
//void mouseDragged(){
//  for(int y = max(mouseY-blurRect-1,1);y<min(mouseY+blurRect+1,height-1);y++){
//    for(int x = max(mouseX-blurRect-1,1);x<min(mouseX+blurRect+1,width-1);x++){
//      float rsum = 0;
//      float gsum = 0;
//      float bsum = 0;
//      float a = min((dist(mouseX,mouseY,x,y)+5)/blurRage,1);
//      for(int h = 0; h<3;h++){
//        for(int w = 0; w<3;w++){
//          color c = img.get(x+w-1,y+h-1);
//          rsum += red(c)*kernel[w][h];
//          gsum += green(c)*kernel[w][h];
//          bsum += blue(c)*kernel[w][h];
//        }  
//      }
//      color c = img.get(x,y);
//      rsum /= 15.95;
//      gsum /= 15.95;
//      bsum /= 15.95;
//      rsum = (rsum*(1-a)+red(c)*a);
//      gsum = (gsum*(1-a)+green(c)*a);
//      bsum = (bsum*(1-a)+blue(c)*a);
//      temp.set(x,y,color(rsum,gsum,bsum));
//    }
//  }
//  img.set(0,0,temp);
//}