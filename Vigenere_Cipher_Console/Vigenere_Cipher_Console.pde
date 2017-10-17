int cLength; //this is to know the key word length for use in encode and decode
void console() {
  puts("GIVE ME YOUR MESSAGE!"); //get imput
  String message = gets();
  puts("WHAT IS THE CYPHER WORD?!?");
  String cypher = gets();
  //convert the cypher to numbers in an array
  cLength = cypher.length();
  int[] cKey = new int[cLength];
  //convert it to numbers in an array
  for (int i = 0; i < cypher.length(); i++) {
    int letter = cypher.charAt(i);
    if (letter >= 97 && letter <= 122) {
      letter = letter - 96; // so a = 1, b = 2...
    } else if (letter >= 65 && letter <= 90) {
      letter = letter - 64; // so A = 1, B = 2...
    } else {
      letter = 0;
    }
    cKey[i] = letter; //store the next value in the key array
  }
  println(cKey);
  //get input
  puts("What do you want to do? type \"1\" to encode \"2\" to decode or \"3\" to quit");
  String input = gets();
  if (input.equals("1")) { //1 is encode
    encode(message, cKey);
    puts(str2);
  } else if (input.equals("2")) { //2 is decode
    decode(message, cKey);
    puts(str2);
  } else {
    exit();
  }
}
String str2 = "";
void encode(String message, int[] cKey) {
  int passNumber = 0; // so we know how many times it has gone around the loop (not including spaces or other non letter charicters)
  for (int i = 0; i < message.length (); i++) {
    int letter = message.charAt(i); //select each indevigaul letter
    if (letter == 32) { // u don't want it to be a space. This is not really needed at all.
    } else if (letter >= 97 && letter <= 122) {
      letter = letter + cKey[passNumber%cLength] - 1; // subtract one because stuff is zero index while others are 1st index.
      if (letter > 122) { // make it rap around
        letter = letter - 26;
      }
      passNumber++; //only increase pass number if it is a letter
    } else if (letter >= 65 && letter <= 90) { // same for lower case letters
      letter = letter + cKey[passNumber%cLength]  - 1;
      if (letter > 90) {
        letter = letter - 26;
      }
      passNumber++;
    }
    println(passNumber%cLength);
    str2 = str2 + char(letter); //add the encodeded letter to the end of the global veriable str2 as the output
    println(str2);
  }
}


void decode(String message, int[] cKey) { // everything here is just reversed from the encode method
  int passNumber = 0;
  for (int i = 0; i < message.length (); i++) {
    int letter = message.charAt(i); //select each indevigaul letter 
    if (letter == 32) {
    } else if (letter >= 65 && letter <= 90) {
      letter = letter - cKey[passNumber%cLength]  + 1; //insted of +1 it is -1
      if (letter < 65) {
        letter = letter + 26;
      }
      passNumber++;
    } else if (letter >= 97 && letter <= 122) {
      letter = letter - cKey[passNumber%cLength] + 1;
      println(letter);
      if (letter < 97) {
        letter = letter + 26;
      }
      println(letter);
      passNumber++;
    }
    //println(passNumber%cLength);
    str2 = str2 + char(letter); //add the decodeded letter to the end of the global veriable str2
    println(str2);
  }
}









































// GLOBALS
ArrayList<String> consoleLog = new ArrayList<String>();
String keyBuffer = "";

int fontSize = 22;
String fontFamily = "Courier New";
int textHeight = fontSize + 4;
int textColor = 0;
int backColor = 255;

float barX() { 
  return(width - 15);
}
float barY = 5;
float barWidth = 8;
float fullBarHeight() { 
  return(height - 10);
}
float contentBarHeight; // Calculated

// MAIN FUNCTIONS
void puts(String output) {
  consoleLog.add(0, output);
}

void cls() {
  consoleLog.clear();
}

String gets() {
  recording = true;
  while (recording) { 
    delay(1);
  }

  String input = keyBuffer;
  keyBuffer = "";

  consoleLog.add(0, "=> " + input);    
  return(input);
}

void finish() { 
  puts("");
  puts("(press any key to quit)");  
  keyPressed = false;
  while (!keyPressed) { 
    delay(1);
  }
  exit();
}

// HELPER FUNCTIONS

boolean mouseOnContentBar() {
  boolean mouseInContentBarWidth = (mouseX > barX() && mouseX < barX() + barWidth);
  boolean mouseInContentBarHeight = (mouseY > barY + (fullBarHeight() - contentBarHeight) - scrollY && mouseY < barY + fullBarHeight() - scrollY);
  return (mouseInContentBarWidth && mouseInContentBarHeight);
}

float scrollY = 0;
void scroll(int delY) {
  scrollY -= delY;

  if (scrollY < 0) {
    scrollY = 0;
  } else if (scrollY > fullBarHeight() - contentBarHeight) {
    scrollY = fullBarHeight() - contentBarHeight;
  }
}

// EVENTS
boolean recording = false;
boolean controlMode = false;
void keyPressed() {
  if (keyCode == 17) {
    controlMode = true;
  }

  if (controlMode) {
    if (keyCode == 67) {
      cp.copyString(consoleLog.get(0));
    } else if (keyCode == 86) {
      keyBuffer += cp.pasteString();
    }
  } else {
    if (key == ENTER || key == RETURN) {
      recording = false;
    }

    if (recording) {
      if (key == BACKSPACE) {
        if (keyBuffer.length() > 0) {
          keyBuffer = keyBuffer.substring(0, keyBuffer.length()-1);
        }
      } else if (key >= ' ' && key <= '~') {
        keyBuffer += str(key);
      }
    }
  }
}

void keyReleased() {
  if (keyCode == 17) {
    controlMode = false;
  }
}

int clickY = -1;
void mousePressed() {  
  if (mouseOnContentBar()) {
    cursor(HAND);
    clickY = mouseY;
  }
}

void mouseDragged() {   
  if (clickY > 0) {
    int delY = mouseY - clickY;

    scroll(delY);

    clickY = mouseY;
  }
}

void mouseReleased() {
  cursor(ARROW);
  clickY = -1;
}

void mouseWheel(MouseEvent event) {
  float dir = event.getCount();

  scroll(int(dir * 30));
}

// SETUP
void setup() {
  size(800, 600);
  frame.setResizable(true);

  frameRate(60);  
  noSmooth();

  PFont fixedWidthFont = createFont(fontFamily, fontSize);
  textFont(fixedWidthFont);

  thread("console");
}


// DRAW
void drawScrollbar() {  
  // Full Bar
  stroke(230);
  fill(230);  
  rect(barX(), barY, barWidth, fullBarHeight());

  // Content Bar
  stroke(180);
  fill(180);
  float contentHeight = textHeight/2 + consoleLog.size()*textHeight;
  contentBarHeight = (height / contentHeight) * fullBarHeight();
  rect(barX(), barY + (fullBarHeight() - contentBarHeight) - scrollY, barWidth, contentBarHeight);
}

int last_blink = 0;
String cursor = "";
void drawConsoleText() {
  fill(textColor);
  float contentHeight = textHeight/2 + consoleLog.size()*textHeight;
  float scrollOffset = scrollY / (fullBarHeight() - contentBarHeight) * (contentHeight - height);  
  int i = 0;

  if (recording) {    
    // Cursor
    if (last_blink + 500 < millis()) {      
      last_blink = millis();
      if (focused && cursor == "") {
        cursor = "█";
      } else  if (!focused && cursor == "") {
        cursor = "░";
      } else {
        cursor = "";
      }
    }
    // Input
    text("=> " + keyBuffer + cursor, 6, height + scrollOffset - textHeight/2 - textHeight*i++);
  }

  // Output
  for (String line : consoleLog) {   
    text(line, 6, height + scrollOffset - textHeight/2 - textHeight*i++);
  }
}

int copied_timer = 0;
void drawCopied() {
  if (copied_timer > 0) {
    textAlign(CENTER);
    textSize(30);
    text("Copied", width/2, height/4);
    textAlign(LEFT);
    textSize(fontSize);
  }

  if (copied_timer + 500 < millis()) {      
    copied_timer = 0;
  }
}

void draw() {
  background(backColor);  

  drawScrollbar();  
  drawConsoleText();
  drawCopied();
}

///////////////////////////////////////////////////////
// Clipboard class for Processing                    //
// by seltar, modified by adamohern                  //
// v 0115AO                                          //
// only works with programs. applets require signing //
///////////////////////////////////////////////////////

import java.awt.datatransfer.*;
import java.awt.Toolkit; 

ClipHelper cp = new ClipHelper();

// CLIPHELPER OBJECT CLASS:

class ClipHelper {
  Clipboard clipboard;

  ClipHelper() {
    getClipboard();
  }

  void getClipboard () {
    // this is our simple thread that grabs the clipboard
    Thread clipThread = new Thread() {
      public void run() {
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      }
    };

    // start the thread as a daemon thread and wait for it to die
    if (clipboard == null) {
      try {
        clipThread.setDaemon(true);
        clipThread.start();
        clipThread.join();
      }  
      catch (Exception e) {
      }
    }
  }

  void copyString (String data) {
    copyTransferableObject(new StringSelection(data));
    copied_timer = millis();
  }

  void copyTransferableObject (Transferable contents) {
    getClipboard();
    clipboard.setContents(contents, null);
  }

  String pasteString () {
    String data = null;
    try {
      data = (String)pasteObject(DataFlavor.stringFlavor);
    }  
    catch (Exception e) {
      System.err.println("Error getting String from clipboard: " + e);
    }
    return data;
  }

  Object pasteObject (DataFlavor flavor)  
    throws UnsupportedFlavorException, IOException
  {
    Object obj = null;
    getClipboard();

    Transferable content = clipboard.getContents(null);
    if (content != null)
      obj = content.getTransferData(flavor);

    return obj;
  }
}

