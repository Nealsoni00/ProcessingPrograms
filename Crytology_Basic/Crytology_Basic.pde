void draw(){
  String message = "Lipps xlmw mw e Geiwev Gmtliv!";
  int cypher = 4;
  //encode(message, cypher);
  long m1 = System.nanoTime();

  decode(message, cypher);
  println(str2);
  long m2 = System.nanoTime();

  println(m2-m1); 
}
String str2 = "";


void decode(String message, int cypher){
  for(int i = 0; i < message.length(); i++){
    int letter = message.charAt(i);
    if (letter == 32){}
    else if(letter >= 97 && letter <= 122){
      letter = letter - cypher;
      if (letter > 122){
        letter = letter - 26; 
      }
    }else if (letter >= 65 && letter <= 90){
      letter = letter - cypher;
      if (letter > 90){
        letter = letter - 26; 
      }
    }else{letter = letter;}
    str2 = str2 + char(letter);
    //println(str2);
    exit();
  }
}
