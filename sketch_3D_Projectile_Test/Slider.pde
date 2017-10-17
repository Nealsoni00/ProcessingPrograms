class Slider { //slider method 
  int x, y, w, h, sliderX, sliderY, sliderW;
  float value;
  boolean moving = false;
  Slider(int _x, int _y, int _w, int _h) {
    x = _x;
    y = _y;
    w = _w;
    h = _h;
    sliderX = x;
    sliderY = y;
    sliderW = w/10;
  } 
  void draw() {
    fill(255, 0, 0);//draw slider
    rect(x, y, w, h);
    fill(0, 255, 0);
    rect(sliderX, sliderY, sliderW, h);
    textSize(10);
    fill(255);
    text(value, x, y);
  }
  void upDate() { //update slider with mouse location
    if (mouseY < y + h && mouseY > y && mouseX < x+w && mouseX > x-sliderW/2 && mousePressed) {
      moving = true;
      cam.setMouseControlled(false);  //PeasyCam off
    } else {
      moving = false;
      cam.setMouseControlled(true);  //PeasyCam on
    }
    if (moving) {
      sliderX = mouseX-sliderW/2;
    }
    value = map((float) sliderX/w, 0, 1, -.2, .2);//map slider with value
    //println(value);
    draw();
  }
}

