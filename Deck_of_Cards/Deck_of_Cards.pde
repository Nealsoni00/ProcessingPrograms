void setup(){
  String lines[] = loadStrings("deck.txt");
  ArrayList<card> cards = new ArrayList<card>();
  println("there are " + lines.length + " Cards");
  
  
    long sum = 0;
  for (int i = 0 ; i < lines.length; i++) {
    String[] card = split(lines[i], ' ');
    long number = 0;
    if (card[0].equals("Ace")){number = 1;}
    else if (card[0].equals("2")){number = 2;}
    else if (card[0].equals("3")){number = 3;}
    else if (card[0].equals("4")){number = 4;}
    else if (card[0].equals("5")){number = 5;}
    else if (card[0].equals("6")){number = 6;}
    else if (card[0].equals("7")){number = 7;}
    else if (card[0].equals("8")){number = 8;}
    else if (card[0].equals("9")){number = 9;}
    else if (card[0].equals("10")){number = 10;}
    else if (card[0].equals("Jack")){number = 11;}
    else if (card[0].equals("Queen")){number = 12;}
    else if (card[0].equals("King")){number = 13;}
    else {println("There is a card that is not part of the deck. Please remove it now.");}
    if (card[2].equals("Hearts")){number = number;}
    else if (card[2].equals("Diamonds")){number = number*1000;}
    else if (card[2].equals("Spades")){number = number*1000000;}
    else if (card[2].equals("Clubs")){number = number*1000000000;}
    else {println("There is a card that is not part of the deck. Please remove it now.");}
    sum = sum + number;
  }
  long difference = (sum - 91091091091L)*-1;
  String suit;
  if (difference >= 1000000000){difference = difference/1000000000; suit = "Clubs";}
  else if (difference >= 1000000){difference = difference/1000000; suit = "Spades";}
  else if (difference >= 1000){difference = difference/1000; suit = "Diamonds";}
  else{suit = "Hearts";}
  String card = "";
  if (difference == 13){card = "King";}
  else if (difference == 12){card = "Queen";}
  else if (difference == 11){card = "Jack";}
  else if (difference == 1){card = "Ace";}
  else {card = str(difference);}
  if (difference == 0){println("nothing is missing");}
  else {println("The " + card + " of " + suit + " is missing from the Deck");}
  
  
  
  
  for (String i: lines){
    cards.add(new card(i));
    println(i);
  }
  //println(cards);
  ArrayList<Hand> hands = new ArrayList<Hand>();
  for (int i = 0; i < cards.size(); i++){
    for (int j = i+1; j < cards.size(); j++){
       for (int k = j+1; k < cards.size(); k++){
          for (int b = k+1; b < cards.size(); b++){
             for (int a = b+1; a < cards.size(); a++){
                hands.add(new Hand(cards.get(i),cards.get(j),cards.get(k),cards.get(b),cards.get(a))); 
             }
          }
       }
    }
  }
  int count = 0;
  for (Hand i: hands){
    if  (i.hasAllSuits){
     count++ ;
    }
  }
  println(hands.size());
  println(count);
}


















 