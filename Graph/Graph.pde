




class Graph{
  int x,y, w,h;
  int maxX, maxY;
  ArrayList<PVector> points = new ArrayList<PVector>();
  Graph(int _x, int _y, int _w, int _h){
    x = _x;
    y = _y;
    w = _w;
    h = _h;
    maxX = x + w;
    maxY = y + h;
  }
  void addPoint(int x, int y){
    points.add(new PVector(x,y));
  }
  void drawPoints(){
    for (int i = 0; i < points.size(); i++){
     point(points.get(i).x,points.get(i).y); 
    }
  }
  void drawLine(){
    
  }
}