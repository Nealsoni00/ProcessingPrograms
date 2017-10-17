void setup() {
  int temp = 0;
  int max = 0;
  for (int i = 1000; i < 9999; i++) {
    for (int j = 1000; j < 9999; j++) {
      temp = i*j;
      if (temp > max && palindrome(temp)) {
        max = temp;
      }
    }
  }
  println(max);
}
boolean palindrome(int _temp){
  String temp = str(_temp);
  boolean output = false;
  int tLength = temp.length();
  for (int i = 0; i < (temp.length()-1); i++) {
    println(i)
    if (temp.charAt(i)==temp.charAt(tLength-i)) {
      output = true;
    } else {
      output = false;
    }
  }

  return output;
}

