void setup() {
  size(600, 300);
  background(25, 118, 210);
  textAlign(CENTER);
  textSize(30);
  frameRate(60);
  text("Press anything to start", width/2, height/2);
  //line(100,90,width-100, 90);
}
float w;
void draw() {
  //  stroke(0);
  //  println("1");
  //  delay(1000);
  //  stroke(255);
  //  line(width/2+w/2+2, height/3-30, width/2+w/2+2, height/3+10);
  //
}


String input = "";
void keyTyped() { //don't want to waist processing power by running the main loop the whole time, just update when a key is pressed
  if (key == 8 && input.length() > 0) {
    input = input.substring( 0, input.length()-1 );// deleate a value
  } else if ((key-48) >= 0 && (key-48) <=10) {
    input += (key-48);
  }
  background(25, 118, 210);
  textSize(30);
  textAlign(CENTER);
  fill(255);
  text("Please Input A Decimal Number Below:", width/2, 40);
  strokeWeight(40);
  rectMode(CENTER);
  fill(255);
  stroke(255);
  rect(width/2, height/3-10, width/2, 7);
  text("Your binary number is:", width/2, height/2);
  rect(width/2, height/3*2, width/4*3, 7);
  fill(0);
  text(input, width/2, height/3);
  w = textWidth(input);
  stroke(0);
  strokeWeight(2);
  if (focused){
    line(width/2+w/2+2, height/3-30, width/2+w/2+2, height/3+10);
  }
  //text("░", width/2+w/2+textWidth("░")/2, height/3);

  //cursor = "░";
    println(binary(int(input))); 

  if (convertToBinary(int(input)).length() < 24 && input.length() < 9) {
    text(convertToBinary(int(input)), width/2, height/3*2+10);
} else {
    text("ERROR: TOO LONG!", width/2, height/3*2+10);
    //  }if (input.length() == 0){
    //    text("0",width/2, height/3);
  }
}
String convertToBinary(int i) {
  String binary = "";
  while (i > 0) {
    int remander = i%2;
    binary = remander + binary;
    i /= 2;
  }
  while (binary.length () < 8) {
    binary = 0 + binary;
  }
  return binary;
}
void mousePressed() {
  keyTyped();
}