class HightMapGen {
  void HightMapGen(startRange = 0.0..20.0,variance = 9,roughness = 0.4){
    int internalWidth = 2 ** (log(width-1) / log(2)).ceil + 1;
    int startRange = startRange;
    int variance = variance.to_f;
    int roughness = roughness.to_f;
  }
  void gen(){
    int stepSize = internalWidth - 1;
    int[] map = Array.new(internalWidth);
    map[0] = random(startRange);
    map[-1] = random(startRange);
    int range = 0...(internalWidth-1);
    int variance = variance;
    while (stepSize > 1){
      range.step(stepSize) do |startIndex|
        int a = map[startIndex];
        int b = map[startIndex + stepSize];
        println( [startIndex..(startIndex+stepSize), startIndex]);
        int c =  (a + b) / 2 + rand(-variance..variance);
        map[stepSize / 2 + startIndex] = c;
        }
      variance *= roughness;
      stepSize /= 2;
      } 
    return map[0...width];
  }  
}
mapGen = false;
while (mapGen == false){
  int a = HightMapGen.new(156);
  map = a.gen;
  char[] world =  Array.new(10){ |y|
    Array.new(map.length){ |x|
      map[x] < y ? '█' : ' ';
    }
  }
  for (int i = 0; i < width; i++){
      if (world[0][i] == "█") {
        mapGen = true;
      }
  }
}
