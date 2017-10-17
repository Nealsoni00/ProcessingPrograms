
Graph line;

void setup() {
  size(600, 600);
  line = new Graph(100, 100, 600, 600);
  for (int i = 0; i < 100; i+=10) {
    line.addPoint(i, i);
  }
}
void draw() {
  background(255);
  line.drawPoints();
}


class Graph {
  int x, y, w, h;
  int maxX, maxY;
  ArrayList<PVector> points = new ArrayList<PVector>();
  Graph(int _x, int _y, int _w, int _h) {
    x = _x;
    y = _y;
    w = _w;
    h = _h;
    maxX = w;
    maxY = h;
  }
  void addPoint(int x, int y) {
    points.add(new PVector(x, y));
    if (x > maxX) {
      maxX = x;
    }
    if (y > maxY) {
      maxY = y;
    }
  }
  void drawPoints() {
    for (int i = 0; i < points.size(); i++) {
      ellipse(scalePoint(points.get(i)).x, scalePoint(points.get(i)).y, 5, 5);
    }
  }

  void drawLine() {
  }
  PVector scalePoint(PVector xy) {
    PVector temp = new PVector();
    temp.x = x+(xy.x);
    temp.y = y+(xy.y);
    println(h+y-(xy.y+y));
    return temp;
  }
}