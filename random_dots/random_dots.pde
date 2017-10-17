int i;
float x;

void setu(){
  size(1000,1000);
  background(0);
  frameRate(100);
  fill(0);
}

void draw(){
  if (random(0.1)< .5){
    stroke(180,180,255);
  }else{
    stroke(200,200,255);
  }
  for (i = 0; i<100; i++){
    x = random(10,20);
    ellipse(random(1,499), random(1,499),x,x);
  
  }
}
