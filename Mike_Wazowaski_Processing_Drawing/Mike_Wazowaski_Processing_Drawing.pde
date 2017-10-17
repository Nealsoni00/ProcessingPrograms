void setup(){
  size(1280, 480); 
  background(255);
  stroke(0);
}

void draw(){
  int xshift = mouseX;
  int yshift = mouseY;

  //green
  fill(0, 153, 51);
  //body
  ellipse(88+xshift,88+yshift,156, 176);
  //eye
  ellipse(88+xshift,45+yshift,76,88);
  //white
  fill(255,255,255);
  //fill(105,245,255); //blueish white
  ellipse(88+xshift,45+yshift,76,80);
  fill(0, 153, 51);
  ellipse(88+xshift,45+yshift,36,36);
  //black for retna
  fill(0, 51, 0);
  ellipse(88+xshift,45+yshift,16,16);
  //mouth
  arc(88+xshift, 123+yshift, 56, 42, PI, 2*PI);
  arc(88+xshift, 123+yshift, 56, 20, 0, PI);
  //ears
  fill(0, 153, 51);
  line(40+xshift,17+yshift,43+xshift,0+yshift);
  line(43+xshift,0+yshift,55+xshift,8+yshift);
  line(120+xshift,6+yshift,132+xshift,0+yshift);
  line(132+xshift,0+yshift,134+xshift,19+yshift);
  fill(255,255,255);
  //arms
  //right arm
  line(167+xshift,80+yshift,179+xshift,183+yshift);
  line(162+xshift,122+yshift,168+xshift,183+yshift);
  arc(173+xshift,183+yshift, 11, 6, 0, PI);
  //left arm
  line(14+xshift,120+yshift,15+xshift,192+yshift);
  arc(10+xshift,140+yshift,20,120, PI/2+.54,3*PI/2);
  arc(10+xshift,190+yshift, 11, 6, 0, PI);
  //left leg
  line(40+xshift,17+yshift,43+xshift,0+yshift);
  line(79+xshift,177+yshift,73+xshift,210+yshift);
  line(60+xshift, 173+yshift ,60+xshift, 210+yshift);
  line(60+xshift,210+yshift,67+xshift,240+yshift);
  line(73+xshift,210+yshift,80+xshift,240+yshift);
  arc(73+xshift,240+yshift, 12,7, 0, PI);
  //right leg
  line(114+xshift,170+yshift, 125+xshift, 210+yshift);
  line(134+xshift,161+yshift,138+xshift,210+yshift);
  line(138+xshift,210+yshift,135+xshift, 240+yshift);
  line(125+xshift,210+yshift,122+xshift, 240+yshift);
  arc(129+xshift,240+yshift, 11,7, 0, PI);
}
