import shapes3d.utils.*;
import shapes3d.animation.*;
import shapes3d.*;
import peasy.*;

PeasyCam cam;
Projectile ball;
Box box;
PVector wind;
float dragC=0;//or .000015
Slider zWind;
void setup() {
  size(1000, 700, P3D); //  20 pixels = 1 meter
  frameRate(30);
  ellipseMode(CENTER);
  //projectile
  ball   = new Projectile(mstopf(40), 45, 0.01);//10 m/s = .166666 m/f = 3.33333 pix/frame
  wind = new PVector( random(-.03, .03), 0, .04);
  zWind = new Slider(0, 0, 100, 30);
  //Camera
  cam = new PeasyCam(this, width/2, height/2, 0, width*2/3);
  //cam = new PeasyCam(this, width/8, height/3, -PI/2, width*2/3);
  cam.setMinimumDistance(300);
  cam.setMaximumDistance(2000);
  //camera(width/2, height/2, (height/2) / tan(PI/6), width/2, height/2, 0, 0, 1, 0);
  box = new Box(this);
  box.setSize(4000, 4000, 4000);
  //  box.fill(color(135, 206, 250));
  //  box.stroke(color(190));
  box.setTexture("back.jpg", Box.FRONT);
  box.setTexture("front.jpg", Box.BACK);
  box.setTexture("left.jpg", Box.LEFT);
  box.setTexture("right.jpg", Box.RIGHT);
  box.setTexture("sky.jpg", Box.TOP);
  box.setTexture("korea.gif", Box.BOTTOM);
  box.moveTo(new PVector(600, -1290, -0000));
  //                forward, (up down), left right
  box.drawMode(S3D.TEXTURE);
  box.strokeWeight(1.2f);
}

void draw() {
  //  cam.lookAt((double)ball.x,(double)ball.y,(double)ball.z);
  box.draw();
  setLights();

  //background(135, 206, 250);
  //skybox.draw();
  ball.next();
  ball.points();

  ball.draw();
  cam.beginHUD();
  zWind.draw();
  zWind.upDate();
  drawText();
  text(zWind.value, 0, 0);
  cam.endHUD();
  wind.z = zWind.value;
  // cam.setActive(false);
}

float mtop(float v) {
  return(v*20);
}
float mstopf(float v) {
  return(v*20/60);
}
float msstopff(float a) {
  return(a*20/60/60);
}
void drawText() {
  textMode(SCREEN);
  cam.beginHUD();
  fill(0);
  int shift = 100;
  text("X: "+nf(ball.pos.x, 3, 2)+ " m", 10+shift, 10);
  text("X velocity: "+nf(convertVel("x"), 2, 2)+" m/s", 10+shift, 40);
  text("X acceleration: "+nf(ball.accel.x/20*60*60, 2, 2)+" m/s^2", 10+shift, 90);

  text("Y: "+nf(height-ball.pos.y, 3, 2)+ " m", 230+shift, 10);
  text("Y velocity: "+nf(-convertVel("y"), 2, 2)+" m/s", 230+shift, 40);
  text("Y acceleration: "+nf(ball.accel.y/20*60*60, 2, 2)+" m/s^2", 230+shift, 90);

  text("Z: "+nf(ball.pos.z, 3, 2)+ " m", 460+shift, 10);
  text("Z velocity: "+nf(convertVel("z"), 2, 2)+" m/s", 460+shift, 40);
  text("Z acceleration: "+nf(ball.accel.z/20*60*60, 2, 2)+" m/s^2", 460+shift, 90);

  text("Percent Drag: "+str(int(dragC/0.00009*100))+"%", 690+shift, 10);
  text("Wind X: "+str(int(wind.x/.01*100))+"%", 690+shift, 40);
  text("Wind Z: "+str(int(wind.z/.01*100))+"%", 690+shift, 90);
  fill(255);
  cam.endHUD();
}
float convertVel(String axis) {
  if (axis == "x") {
    return ball.vel.x/20*60;
  }  
  if (axis == "y") {
    return ball.vel.y/20*60;
  }  
  if (axis == "z") {
    return ball.vel.z/20*60;
  } else {
    return 0.0;
  }
}
void keyPressed() {
  if (keyCode== UP) {
    dragC+=.000008;
  }
  if (keyCode== DOWN) {
    dragC-=.000008;
  }
  if (keyCode == LEFT) {
    wind.x-=.001;
  }
  if (keyCode == RIGHT) {
    wind.x+=.001;
  }
  if (keyCode == SHIFT) {
    ball.points.clear();
    ball.clearExplosion();
  }
}

void setLights() {
  ambientLight(50, 50, 70);
  directionalLight(255, 255, 255, 0, 1, 0);
}