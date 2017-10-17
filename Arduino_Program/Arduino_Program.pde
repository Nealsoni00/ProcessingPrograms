
import ddf.minim.*;
import ddf.minim.ugens.*;

Minim minim;
AudioOutput out;
AudioRecorder recorder;

Oscil      wave;
Frequency  currentFreq;
import processing.serial.*;

Serial port;

void setup() { 
  println(toBinary(4));
  size(700, 240);
  surface.setResizable(true);
  // initialize the minim and out objects
  minim = new Minim(this);
  out   = minim.getLineOut();
  recorder = minim.createRecorder(out, "myrecording.wav");
  currentFreq = Frequency.ofPitch( "0" );
  wave = new Oscil( currentFreq, 0.6f, Waves.SINE);
  textFont(createFont("Arial", 12));
  wave.patch( out );
  //port = new Serial(this, Serial.list()[4], 9600);
}
int note = 0;
String playing = "";
int test;
void draw() { 
  background(25, 118, 210);

  playing = "";
  // REPLACE THE THREE LINES BELOW WITH YOUR CODE
  int key_int = simulateBytesFromSerial(); 
  if (key_int >= 0) {
    println("key_int: " + key_int);
    for (int i = 11; i >= 0; i--) {
      test = (int) Math.pow(2, i);  
      if (key_int >= test) {  
        key_int -= test; 
        playNote(i);  
        println("J: " + i);
      }
    }
    println(playing);
  }

  stroke(255);
  strokeWeight(2);
  textSize(20);
  text( "Current Frequency in Hertz: " + currentFreq.asHz(), 5, 20 );
  text( "Current Frequency as MIDI note: " + currentFreq.asMidiNote(), 5, 45 );
  text("Then note is: " + playing, 5, 70 );
  // draw the waves
  for ( int i = 0; i < out.bufferSize() - 1; i++ )
  {
    // find the x position of each buffer value
    float x1  =  map( i, 0, out.bufferSize(), 0, width );
    float x2  =  map( i+1, 0, out.bufferSize(), 0, width );
    // draw a line from one buffer position to the next for both channels
    line( x1, 140 + out.left.get(i)*50, x2, 140 + out.left.get(i+1)*50);
  }
  if ( recorder.isRecording() ) {
    text("Recording...", 5, 90);
  } else {
    text("Not recording.", 5, 90);
  }
}


// Simulates readBytesFromSerial sending the integers in pattern at 1000 ms intervals
int[] pattern = {1,2,3,4, 5, 6, 7, 8, 9, 10, 11, 12, 1};
int stage = 0, t = millis(), delay = 1000;
int simulateBytesFromSerial() {
  int data;
  if (millis() - t > delay) { // if delay has passed
    data = pattern[stage++]; // select next sound from pattern
    if (stage == pattern.length) stage = 0;
    t = millis();
  } else {
    data = -1; // otherwise simulate no data
  }
  return(data);
}

// Reads up to four bytes from the serial port and returns them as a single integer; Returns -1 if serial port has no data
int readBytesFromSerial() {
  byte[] data = port.readBytes();
  if (data != null) {  
    println("This is data: "+ data);
    println("This is the int value: " + byteArrayToInt(data, data.length)); 
    return(byteArrayToInt(data, data.length));
  }
  return(-1);
}

// Converts an array of N bytes to an integer
int byteArrayToInt(byte[] b, int size) {
  int result = 0;
  for (int i=size-1; i>=0; i--) {
    result |= (b[i] & 0xff) << 8*i;
  }
  return(result);
}
String[] notes= {"A3", "B3", "C4", "D4", "E4", "F4", "G4", "A4", "B4", "C5", "D5", "E5", "F5", "G5", "A6", "B6", "E6", "F6", "G6", "A7", "B7", "C7", "D7", "E7", "F7", "G7"} ;
int a;
void playNote(int note) {
  a = 0;
  if (note != 0) {
    a =  note;//Math.abs((int) (Math.log(note) / Math.log(2)));
  } else {
    currentFreq = Frequency.ofHertz(0);
  }
  playing = playing + " " + notes[a];
  //println(notes.length); 
  //println(note);
  currentFreq = Frequency.ofPitch(notes[a+1]);
  //if ( note == 0) {
  //  currentFreq = Frequency.ofHertz(0);
  //}

  //if ( note == 1 ) currentFreq = Frequency.ofPitch( "A3" );
  //if ( note == 2 ) currentFreq = Frequency.ofPitch( "B3" );
  //if ( note == 4 ) currentFreq = Frequency.ofPitch( "C4" );
  //if ( note == 8 ) currentFreq = Frequency.ofPitch( "D4" );
  //if ( note == 16 ) currentFreq = Frequency.ofPitch( "E4" );
  //if ( note == 32 ) currentFreq = Frequency.ofPitch( "F4" );
  //if ( note == 64 ) currentFreq = Frequency.ofPitch( "G4" );
  //if ( note == 128 ) currentFreq = Frequency.ofPitch( "A4" );
  //if ( note == 256 ) currentFreq = Frequency.ofPitch( "B4" );
  //if ( note == 512 ) currentFreq = Frequency.ofPitch( "C5" );
  //if ( note == 1024) currentFreq = Frequency.ofPitch( "D5" );
  //if ( note == 2048) currentFreq = Frequency.ofPitch( "E5");
  //if ( note == 4096) currentFreq = Frequency.ofPitch( "F5");
  //
  //note that there are two other static methods for constructing Frequency objects
  //currentFreq = Frequency.ofHertz( 440 );
  //currentFreq = Frequency.ofMidiNote( 69 ); 
  wave.setFrequency( currentFreq );
}

String toBinary(int note) {
  float a = 0;
  if (note != 0) {
    a =  Math.abs((int) (Math.log(note) / Math.log(2)));
  }
  float i = a;
  String binary = "";
  while (i > 0) {
    int remander = int(i%2);
    binary = remander + binary;
    i /= 2;
  }

  return binary;
}
void keyReleased() {
  if ( key == 'r' ) {
    if ( recorder.isRecording() ) {
      recorder.endRecord();
    } else {
      recorder.beginRecord();
    }
  }
  if ( key == 's' ) {
    recorder.save();
    text("Done saving.", 10, 85);
  }
}