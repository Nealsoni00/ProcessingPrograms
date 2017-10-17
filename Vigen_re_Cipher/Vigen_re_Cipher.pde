int cLength;
void draw(){
  long m1 = System.nanoTime();
  String message = "Skgex 007, TLLEEB EM VSAP!";
  String cypher = "SeCrETs";
  cLength = cypher.length();
  int[] cKey = new int[cLength];
  //convert it to numbers in an array
  for(int i = 0; i < cypher.length(); i++){
    int letter = cypher.charAt(i);
    if(letter >= 97 && letter <= 122){
      letter = letter - 96; // so a = 1, b = 2...
    }else if (letter >= 65 && letter <= 90){
      letter = letter - 64; // so A = 1, B = 2...
    }else{
      letter = 0;
    }
    cKey[i] = letter;
  }
  //println(cKey);
  
  //encode(message, cKey);
  decode(message, cKey);
  println(System.nanoTime()-m1);
 exit(); 
}

String str2 = "";
void encode(String message, int[] cKey){
  int passNumber = 0;
  for(int i = 0; i < message.length(); i++){
    int letter = message.charAt(i);
    if (letter == 32){}
    else if(letter >= 97 && letter <= 122){
      letter = letter + cKey[passNumber%cLength] - 1;
      if (letter > 122){
        letter = letter - 26; 
      }
      passNumber++;
    }else if (letter >= 65 && letter <= 90){
      letter = letter + cKey[passNumber%cLength]  - 1;
      if (letter > 90){
        letter = letter - 26; 
      }
      passNumber++;
    }else{letter = letter;}
    //println(passNumber%cLength);
    str2 = str2 + char(letter);
    println(str2);
  }
}


void decode(String message, int[] cKey){
  int passNumber = 0;
  for(int i = 0; i < message.length(); i++){
    int letter = message.charAt(i);
    if (letter == 32){}
    else if (letter >= 65 && letter <= 90){
      letter = letter - cKey[passNumber%cLength]  + 1;
      if (letter < 65){
        letter = letter + 26; 
      }
      passNumber++;
    }else if(letter >= 97 && letter <= 122){
      letter = letter - cKey[passNumber%cLength] + 1;
      //println(letter);
      if (letter < 97){
        letter = letter + 26; 
      }
      //println(letter);
      passNumber++;
    }else{letter = letter;}
    //println(passNumber%cLength);
    str2 = str2 + char(letter);
    println(str2);
  }
}
