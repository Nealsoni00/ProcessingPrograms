class Entry { //entry class that stores 3 things, the word, its value or weight and a link to another entry for collisions
  String word;
  int value;
  Entry link;
  Entry(String _word, int _value) {
    word = _word;
    value = _value;
  }
}

public class Hashmap {
  Entry table[]; //create an array of entries to store the hashmap in
  int size; //so i can check if collisions work
  Hashmap(int s) {
    table = new Entry[s]; //^same as above
    size = s; //so in can see the size later
  }
  void insert(String word, int wKey) {
    int hashCode = Math.abs(word.hashCode()) % table.length; //generate the hasCode value to put into the array. Since .hashCode() gives a value that can be negative, i take the abs() of it
    Entry currentEntry = table[hashCode];
    if (currentEntry != null) { //if it is not nul then there is already a word in there. This intiates the linked list for collisions
      if (currentEntry.word.equals(word)) {
        currentEntry.value = wKey; //if they are the same word then it does not matter
      } else {
        while (currentEntry.link != null) { //keep linking it inside of the "link" untill u get a link that is empty or "null"
          currentEntry = currentEntry.link;
        }
        Entry previousEntry = new Entry(word, wKey); // create temperary entry
        currentEntry.link = previousEntry;
      }
    } else { 
      Entry replacementEntry = new Entry(word, wKey);//if it is null (nothing is in the array) then you just create a temp entry and place that in the hash
      table[hashCode] = replacementEntry; //
    }
  }
  String getfrom(String word) {
    int hashCode = Math.abs(word.hashCode()) % table.length;
    Entry currentEntry = table[hashCode];
    //make sure that that value is not "null"
    while (currentEntry != null) { //linked list search
      if (currentEntry.word.equals(word)) {
        return currentEntry.word;
      }
      currentEntry = currentEntry.link;
    }
    return "nothing";
  }
  int getvalue(String word) {
    int hashCode = Math.abs(word.hashCode()) % table.length;
    Entry currentEntry = table[hashCode];
    //make sure that that value is not "null"
    while (currentEntry != null) { //linked list search  
      if (currentEntry.word.equals(word)) {//keep going untill u find ur word
        return currentEntry.value; //return the value of the word to sort them by relivance
      }
      currentEntry = currentEntry.link;
    }
    return 1; //can return anything as long as it is not the value
  }
}


Hashmap replacementWords;
String[] dict, file, words; //define all the global variables
long t1, t2, tHash;
ArrayList<String> misspelledWords = new ArrayList <String>(); //use arraylist because you don't know how many misspelled words there will be
void setup() {
  //dict = loadStrings("dictionary2.txt");
  dict = loadStrings("englishOrder.txt"); //want to use the order 
  selectInput("Select a file to process:", "fileSelected");
}
void fileSelected(File selection) { //load text file from user
  if (selection == null) {
    println("Window was closed or the user hit cancel. Restarting in 3 seconds"); //so the program does not fail
    delay(3000); //
    selectInput("Select a file to process:", "fileSelected"); //keeps running untill u quit
  } else {
    println("User selected " + selection.getAbsolutePath());
    file = loadStrings(selection.getAbsolutePath()); //load programs
    t1 = System.nanoTime(); //so i know how long the program takes to run
    String string = ""; //intermediate variable so that everything is on one string even if they are on different lines
    for (int i = 0; i < file.length; i++) {
      string += file[i].replaceAll("\\p{Punct}", "").toLowerCase() + " "; //Strip punctuation with regex except ' and - from the rest of the elements and downcase it
    }
    file = split(string, ' ');
    Hashmap desWords = new Hashmap(dict.length); //change hashmap size to prove that collisions work 
    long t1Hash = System.nanoTime();
    for (int i = 0; i < dict.length; i++) {
      String[] temp = split(dict[i], ' ');
      desWords.insert(temp[0], int(temp[1])); //insert the word and its weight (key) into the hash
    }
    tHash = System.nanoTime()-t1Hash;
    spellCheck(file, desWords); //spell check the words
  }
}


void spellCheck(String[] words, Hashmap desWords) {
  for (int i = 0; i < file.length; i++) {
    if ((words[i].length() > 0) && (desWords.getfrom(words[i]).equals("nothing"))) {
      misspelledWords.add(words[i]);
      StringList possibleWords = correctWords(words[i], desWords); //generate possible spellings 
      String listOfWords = "";
      for (int j = 0; j < possibleWords.size (); j++) {
        listOfWords = listOfWords + possibleWords.get(j) + " "; //for desplaying in terminal
      }
      println(words[i].toUpperCase() + " is spelled wrong. Replacements are: " + listOfWords);
    }
  }
  String badWords = "";
  if (misspelledWords.size() > 0) {
    for (int i = 0; i < misspelledWords.size (); i++) {
      badWords = badWords + " " + misspelledWords.get(i); //for desplaying the misspelled words (if needed)
    }
  } else {
    println("all words are spelled correctly");
  }
  long t2 = System.nanoTime()-t1;
  //println("there are " + misspelledWords.size() + " misspelled words: " + badWords);
  println("Hashed in: " + tHash/1000 + " microseconds"); //print out all the stats
  println("HashMap size of: " + desWords.size);
  println("dictionary size of: " + dict.length);
  println("It took " + t2/1000000.0 + " miliseconds for whole program to run");
}
StringList correctWords(String badWord, Hashmap desWords) {
  StringList possibleWords = generateWords(badWord); //run the word generator to create a list of possible words
  StringList realWords = new StringList(); //store the words from the generated list into the 
  for (int i = 0; i < possibleWords.size (); i++) {
    if ((possibleWords.size() > 0) && (desWords.getfrom(possibleWords.get(i)).equals("nothing"))) { //check to see if it is in the dictionary
    } else {
      if (desWords.getvalue(possibleWords.get(i)) > 2000) { //if it is then you also want to see if it is a common word (all words have a weight in the dictionary
        realWords.append(possibleWords.get(i));//add it to the list of "real" words
      }
    }
  }
  return realWords; //return it
}

StringList generateWords(String badWord) {
  if (badWord == null) {
    return null;
  }
  StringList possibleWords = new StringList();
  String w = null;

  //deleate one letter from word
  for (int i = 0; i < badWord.length (); i++) {
    w = badWord.substring(0, i) + badWord.substring(i + 1); // skip one letter 
    possibleWords.append(w);
  }
  //swap adjacent letters 
  for (int i = 0; i <= badWord.length () - 2; i++) { 
    w = badWord.substring(0, i) + badWord.charAt(i + 1) + badWord.charAt(i) + badWord.substring(i + 2); //replace one letter with another from the same string 
    possibleWords.append(w);
  }
  //replace all letters with alphabet
  for (int i = 0; i < badWord.length (); i++) {
    for (char c='a'; c <='z'; c++) {
      w = badWord.substring(0, i) + c + badWord.substring(i + 1); // replacing every letter with every letter in the alphabet
      possibleWords.append(w);
    }
  }
  //add every letter in the alphabet to every position
  for (int i = 0; i <= badWord.length (); i++) { //you want to add the alphabet to all positions including the last one
    for (char c ='a'; c <='z'; c++) {
      w = badWord.substring(0, i) + c + badWord.substring(i); // insert the new char
      possibleWords.append(w);
    }
  }
  return possibleWords;
}

