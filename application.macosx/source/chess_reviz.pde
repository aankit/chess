float squareSide;         //what is the size of each square, builds the board   
boolean revizMode;
boolean albersMode;

//initialize objects
Board b;
Master m;
ControlPanel cp;

void setup() {
  //variables
  size(800, 640);
  squareSide=80;
  revizMode = false;

  b = new Board();
  m = new Master();
  cp = new ControlPanel();


  m.initPlayer();
}

void draw() {
  background(128);
  m.startTurn();
  //revisualization
  for (int i=0;i<8;i++) {
    for (int j=0;j<8;j++) {
      PVector loc = new PVector(i, j);
      PVector origin = new PVector();
      //grab all the white moves for a square
      int[] current = int(split(m.current.allMoves.get(loc), ","));
      int[] opponent = int(split(m.opponent.allMoves.get(loc), ",")); 
      //send them to the board one by one with some extra info
      for (int x=0;x<current.length;x++) {
        if (current[x]!=0) {
          origin = m.current.lookupPos(current[x]);
          b.display(loc, origin, revizMode, m.current.isWhite);
        }
      }

      //send them to the board one by one with some extra info
      for (int x=0;x<opponent.length;x++) {
        if (opponent[x]!=0) {
          origin = m.opponent.lookupPos(opponent[x]);
          b.display(loc, origin, revizMode, m.opponent.isWhite);
        }
      }
    }
  }
  //display the pieces
  m.current.displayPieces();
  m.opponent.displayPieces();
  //display the control panel
  cp.displayTurn(m.whiteTurn);
  cp.display();
  if (!m.start) {
    cp.displayStart();
  }
  //  PVector temp = new PVector(3, 3);
  //  println(m.p[1].allMoves.get(temp));
}

void mousePressed() {
  if (m.start) {
    m.mouseMaster();
  }
}

void mouseClicked() {
  if (cp.revizPressed(mouseX, mouseY)) {
    revizMode =true;
  } 
  else {
    revizMode = false;
  }
}

void keyPressed() {
  if (key==BACKSPACE) {
    m.whiteTurn = !m.whiteTurn;
  }
  if (key==ENTER) {
    m.start = true;
  }
  if (key=='u') {
    m.opponent.unKill();
  }
}

void square(float x, float y) {
  strokeWeight(.5);
  rect(x*squareSide, y*squareSide, squareSide, squareSide);
}

void smallSq(float x, float y) {
  rect(x*squareSide+squareSide/4, y*squareSide+squareSide/4, squareSide/2, squareSide/2);
}

