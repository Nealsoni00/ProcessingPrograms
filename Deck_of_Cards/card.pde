class card{
  String suit;
  String value;
  card(String card){
    String[] info = split(card, ' ');
    //println(info);
    suit = info[2];
    value = info[0];
  }
  
}