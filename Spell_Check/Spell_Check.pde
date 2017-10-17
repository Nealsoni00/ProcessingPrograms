//hash dict values to find the key at which each letter exists at. Then check if the array at the index equals the current word
import java.util.Arrays;

class MyHashTable {
  int size = englishDictArray.length;
  MyHashEntry myTable[] = new MyHashEntry[size];

  class MyHashEntry {
    String key, value;
    MyHashEntry nextEntry; //for linear probing purposes, helps to have reference to next entry in list
    MyHashEntry(String keyInput, String valueInput) {
      key = keyInput;
      value = valueInput;
    }
  }

  void put(String keyInput, String valueInput) {

    int hashCode = Math.abs(keyInput.hashCode()) % size;
    MyHashEntry currentEntry = myTable[hashCode];
    if (currentEntry != null) {
      //duplicate b/c there should be only be unique keys
      if (currentEntry.key.equals(keyInput)) {
        currentEntry.value = valueInput;
      } else {
        while (currentEntry.nextEntry != null) {
          currentEntry = currentEntry.nextEntry;
        }
        MyHashEntry oldEntry = new MyHashEntry(keyInput, valueInput);
        currentEntry.nextEntry = oldEntry;
      }
    } else {
      //no duplicates, and simply create a new key value pair (MyHashEntry object)
      MyHashEntry newEntry = new MyHashEntry(keyInput, valueInput);
      myTable[hashCode] = newEntry;
    }
  }

  MyHashEntry get(String keyInput) {
    int hashCode = Math.abs(keyInput.hashCode()) % size;
    MyHashEntry currentEntry = myTable[hashCode];

    while (currentEntry != null) {
      if (currentEntry.key.equals(keyInput)) {
        return currentEntry;
      }
      currentEntry = currentEntry.nextEntry;
    }
    return null;
  }
}

//GLOBALS
String [] englishDictArray;
ArrayList<String> misspelledWords;
MyHashTable englishDictHash;
void setup() {
  //code for creating dictionary hash
  englishDictArray = loadStrings("dictionary.txt");
  englishDictHash = new MyHashTable();
  for (int i = 0; i < englishDictArray.length; i++) {
    englishDictHash.put(englishDictArray[i].replaceAll("\\p{Punct}", ""), Integer.toString(i));// maybe .toLowerCase() here also...tbd
  }

  selectInput("Please choose your file:", "chosenFile");
}

void chosenFile(File chosen) {  
  if (chosen == null) {
    println("You didnt' select a file");
  } else {
    String path = chosen.getAbsolutePath();
    StringBuilder builder = new StringBuilder();
    for (String s : loadStrings (path)) {
      builder.append(s + " ");
    }
    String rawInputWords = builder.toString();
    String[] words = rawInputWords.split("\\s+");
    for (int i = 0; i < words.length; i++) {
      words[i] = words[i].replaceAll("\\p{Punct}", "").toLowerCase();
    }
    spellCheck(words);
  }
}

void spellCheck(String [] myWords) {
  misspelledWords = new ArrayList <String>();
  for (int i = 0; i < myWords.length; i++) {
    if ((myWords[i].trim().length() > 0) && (englishDictHash.get(myWords[i]) == null)) {
      misspelledWords.add(myWords[i]);
    }
  }
  if (misspelledWords.size() > 0) {
    println("MISSSPELLINGS:");
    for (int i = 0; i < misspelledWords.size (); i++) {
      println(misspelledWords.get(i));
    }
  } else {
    println("NO MISSPELLED WORDS :)");
  }
}