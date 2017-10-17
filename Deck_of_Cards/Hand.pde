class Hand {
  card[] cards = new card[5];
  boolean hasAllSuits = false;
  boolean hasClubs, hasSpades, hasHearts, hasDiamonds;
  Hand(card card1, card card2, card card3, card card4, card card5) {
    cards[0] = card1;   
    cards[1] = card2;
    cards[2] = card3;

    cards[4] = card5;
    for (int j = 0; j < cards.length; j ++) {
      card i = cards[j];
      if (i != null) {
        if (i.suit.equals("Hearts")) {
          hasHearts = true;
        }
        if (i.suit.equals("Diamonds")) {
          hasDiamonds = true;
        }
        if (i.suit.equals("Clubs")) {
          hasClubs = true;
        }
        if (i.suit.equals("Spades")) {
          hasSpades = true;
        }
      }else {
         println( "null"); 
      }
    }
    if (hasSpades && hasClubs && hasDiamonds && hasHearts) {
      hasAllSuits = true;
    } else {
      hasAllSuits = false;
    }
  }
}