import processing.video.*;
 
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.core.Size;
 
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.Raster;
 
Capture cap;
int pixCnt;
BufferedImage bm;
PImage img;
 
void setup() {
  size(640, 480);
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
  println(Core.VERSION);
 
  cap = new Capture(this, width, height);
  cap.start();
  bm = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
  img = createImage(width, height, ARGB);
  pixCnt = width*height*4;
}
 
void convert(PImage _i) {
  bm.setRGB(0, 0, _i.width, _i.height, _i.pixels, 0, _i.width);
  Raster rr = bm.getRaster();
  byte [] b1 = new byte[pixCnt];
  rr.getDataElements(0, 0, _i.width, _i.height, b1);
  Mat m1 = new Mat(_i.height, _i.width, CvType.CV_8UC4);
  m1.put(0, 0, b1);
 
  Mat m2 = new Mat(_i.height, _i.width, CvType.CV_8UC1);
  Imgproc.cvtColor(m1, m2, Imgproc.COLOR_BGRA2GRAY);   
 
  Imgproc.GaussianBlur(m2, m2, new Size(7, 7), 1.5, 1.5);
  Imgproc.Canny(m2, m2, 0, 30, 3, false);
  Imgproc.cvtColor(m2, m1, Imgproc.COLOR_GRAY2BGRA);
 
  m1.get(0, 0, b1);
  WritableRaster wr = bm.getRaster();
  wr.setDataElements(0, 0, _i.width, _i.height, b1);
  bm.getRGB(0, 0, _i.width, _i.height, img.pixels, 0, _i.width);
  img.updatePixels();
  bm.flush();
  m2.release();
  m1.release();
}
 
void draw() {
  if (cap.available()) {
    background(0);
    cap.read();
    convert(cap);
    image(img, 0, 0);
  }
}
