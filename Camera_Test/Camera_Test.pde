import processing.video.*;

Capture video;

void setup() {
  size(1280, 720);
  video = new Capture(this, 1280, 720, 30);
  video.start();
  println(video.list());
}

void draw() {
  if(video.available()) {
    video.read();
  }
  image(video, 0, 0);
}

