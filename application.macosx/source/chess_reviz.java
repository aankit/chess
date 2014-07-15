import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class chess_reviz extends PApplet {

float squareSide;         //what is the size of each square, builds the board   
boolean revizMode;
boolean albersMode;

//initialize objects
Board b;
Master m;
ControlPanel cp;

public void setup() {
  //variables
  size(800, 640);
  squareSide=80;
  revizMode = false;

  b = new Board();
  m = new Master();
  cp = new ControlPanel();


  m.initPlayer();
}

public void draw() {
  background(128);
  m.startTurn();
  //revisualization
  for (int i=0;i<8;i++) {
    for (int j=0;j<8;j++) {
      PVector loc = new PVector(i, j);
      PVector origin = new PVector();
      //grab all the white moves for a square
      int[] current = PApplet.parseInt(split(m.current.allMoves.get(loc), ","));
      int[] opponent = PApplet.parseInt(split(m.opponent.allMoves.get(loc), ",")); 
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

public void mousePressed() {
  if (m.start) {
    m.mouseMaster();
  }
}

public void mouseClicked() {
  if (cp.revizPressed(mouseX, mouseY)) {
    revizMode =true;
  } 
  else {
    revizMode = false;
  }
}

public void keyPressed() {
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

public void square(float x, float y) {
  strokeWeight(.5f);
  rect(x*squareSide, y*squareSide, squareSide, squareSide);
}

public void smallSq(float x, float y) {
  rect(x*squareSide+squareSide/4, y*squareSide+squareSide/4, squareSide/2, squareSide/2);
}

class Bishop extends ChessPieces {

  Bishop(boolean isWhite, int _instance) {  
    super(isWhite);
    instance = _instance;
    //create pieces and place in start pos
    if (isWhite) {
      img = loadImage("wb.png");
      if (instance==0) {
        pos = new PVector(2, 7);
        pieceID = 5;
      }
      else {
        pos = new PVector(5, 7);
        pieceID = 7;
      }
    }
    else {
      img = loadImage("bb.png");
      if (instance==0) {
        pos = new PVector(2, 0);
        pieceID = 6;
      }
      else {
        pos = new PVector(5, 0);
        pieceID = 8;
      }
    }
    //movement
    maxValid = 4;
    validMoves = new PVector[maxValid];
    for (int i=0; i<maxValid; i++) {
      validMoves[i] = new PVector();
    }
    validMoves[0].set(1, -1);
    validMoves[1].set(1, 1);
    validMoves[2].set(-1, 1);
    validMoves[3].set(-1, -1);
  }
}

class Board {
  int fillValue;
  int lightSq;
  int darkSq;
  int white;
  int black;

  Board() {
    lightSq = color(255, 255, 255);
    darkSq = color(225, 153, 51);
  }

  public void display(PVector loc, PVector origin, boolean reviz, boolean isWhite) {
    for(int i=0;i<9;i++){
      stroke(155);
      strokeWeight(.5f);
      line(squareSide*i, 0, squareSide*i, height);
      line(0, squareSide*i, squareSide*8, squareSide*i);
    }
    if (reviz) {
      PVector pt1 = new PVector(loc.x*squareSide+squareSide/2, loc.y*squareSide+squareSide/2);
      PVector pt2 = new PVector();
      PVector pt3 = new PVector();
      //how far away is the piece, euclidean style
      float dist = abs(loc.dist(origin));
      //what is the x and y difference, used to determine direction
      PVector dir = PVector.sub(loc, origin);
      float dirX = -1*(dir.x/abs(dir.x));
      float dirY = -1*(dir.y/abs(dir.y));
      float s = sqrt(pow(squareSide/2, 2)/2);

      if (dir.x==0) {
        if (layer(pt1, dirX, dirY, isWhite)) {
          pt1.set(pt1.x, pt1.y+dirY*5);
        }
        pt2.set(pt1.x-s, pt1.y+(dirY*s));
        pt3.set(pt1.x+s, pt1.y+(dirY*s));
      }
      else if (dir.y==0) {
        if (layer(pt1, dirX, dirY, isWhite)) {
          pt1.set(pt1.x+dirX*5, pt1.y);
        }
        pt2.set(pt1.x+(dirX*s), pt1.y-s);
        pt3.set(pt1.x+(dirX*s), pt1.y+s);
      } 
      else {
        if (layer(pt1, dirX, dirY, isWhite)) {
          pt1.set(pt1.x+dirX*5, pt1.y+dirY*5);
          pt2.set(pt1.x+(dirX*.87f*squareSide/2), pt1.y);
          pt3.set(pt1.x, pt1.y+(dirY*.87f*squareSide/2));
        } 
        else {
          pt2.set(pt1.x+(dirX*squareSide/2), pt1.y);
          pt3.set(pt1.x, pt1.y+(dirY*squareSide/2));
        }
      }

      stroke(0);
      strokeWeight(0.5f);
      fill(displayColor(isWhite));
      triangle(pt1.x, pt1.y, pt2.x, pt2.y, pt3.x, pt3.y);
      noFill();
    } 
    else {
      for (int x=0; x<8; x++) {
        for (int y=0; y<8; y++) {
          if (x%2==0) {
            if (y%2==0) {
              fillValue = color(lightSq);
            }
            else {
              fillValue = color(darkSq);
            }
          }
          else {
            if (y%2==0) {
              fillValue = color(darkSq);
            }
            else {
              fillValue = color(lightSq);
            }
          }
          //draw squares
          fill(fillValue);
          //noStroke();
          square(x, y);
        }
      }
    }
  }


  public int displayColor(boolean isWhite) {
    int c = color(0);
    if (isWhite) {
      //c = color(83, 119, 122, 75);

      c = color(32, 47, 178, 125);
    } 
    else {
      c = color(255, 215, 72, 125);
      //c = color(217, 91, 67, 75);
    }
    return c;
  }

  public boolean layer(PVector pt1, float dirX, float dirY, boolean isWhite) {
    int offset=0;
    loadPixels();
    int testX = PApplet.parseInt((pt1.x+(dirX*squareSide/4)));
    int testY = PApplet.parseInt((pt1.y+(dirY*squareSide/4)));
    int test = pixels[testX+(testY*width)];
    if (test==color(128)) {
      offset=offset;
    } 
    else {
      offset++;
    }

    if (offset>0) {
      return true;
    }
    else {
      return false;
    }
  }
}

class Button {
  boolean press;
  float bwidth;
  float bheight;
  float bround;
  float x;
  float y;
  String btext;

  Button(float _x, float _y, String _btext) {
    x = _x;
    y = _y;
    btext = _btext;
    press = false;
    bwidth = 145;
    bheight = 30;
    bround = 7;
  }

  public void display() {
    if (press) {
      noStroke();
      fill(242, 235, 222);
      rect(x, y, bwidth, bheight, 7);
      fill(255, 0, 0);
      text(btext, x+8, y+3, bwidth, bheight);
    } 
    else {
      noStroke();
      fill(0);
      rect(x+3, y+2, bwidth, bheight, 7);
      fill(242, 235, 222);
      rect(x, y, bwidth, bheight, 7);
      fill(0);
      text(btext, x+5, y+3, bwidth-5, bheight-3);
    }
  }

  public boolean press(float xpos, float ypos) {
    if(xpos>x && ypos>y && xpos<(x+bwidth) && ypos<(y+bheight)){
      press = !press;
    }
    return press;
  }
}

class ChessPieces {
  boolean isWhite;                //is white or black
  boolean selected;               //is it being moved?
  boolean alive;                  //is it alive?  
  boolean last;                   //last piece moved
  PVector pos;                    //piece position
  PVector lastPos;                //last piece position
  PImage img;                     //piece image
  int maxValid;                   //maximum number of valid moves a piece can take 
  ArrayList<PVector> validSqs;    //the list of valid squares the piece selected can move

  PVector[] validMoves;           //the list of valid movement vectors for each piece   
  int instance;        //for bishops, horses, rooks, and pawns - which one on the side are we creating?
  int pieceID;         //ID populated in board[][] used by mousePressed to communicate to player what piece has been clicked on.
  int counter;
  ControlPanel cp;

  ChessPieces() {
  }

  ChessPieces(boolean _isWhite) {
    isWhite = _isWhite; 
    selected = false;
    alive = true;
    last = false;
    counter = 0;
    cp = new ControlPanel();
  }

  public void display() {
    //piece at rest
    if (alive) {
      if (selected) {
        float imageX = mouseX;
        float imageY = mouseY;
        imageMode(CENTER);
        image(img, imageX, imageY, squareSide, squareSide);
        validSquares();
        for (PVector _pos : validSqs) {
          noStroke();
          if (isWhite) {
            fill(32, 47, 178, 75);
          } 
          else {
            fill(255, 215, 72, 75);
          }
          square(_pos.x, _pos.y);
        }
        noFill();
      } 
      else {
        float imageX = pos.x*squareSide+squareSide/2;
        float imageY = pos.y*squareSide+squareSide/2;

        if ((pieceID==1 || pieceID==2)&&revizMode) {
          if (counter>squareSide) {
            counter=20;
          }
          strokeWeight(4);
          float diam=squareSide-counter; 
          if (pieceID==1) {
            noStroke();
            fill(255, 215, 72);
          } 
          else {
            noStroke();
            fill(32, 47, 178);
          }
          ellipseMode(CENTER);
          ellipse(imageX, imageY, diam, diam);
          counter+=3;
        }
        imageMode(CENTER);
        image(img, imageX, imageY, squareSide*.5f, squareSide*.5f);
      }
    } 
    else {
      cp.displayDead(img, isWhite, pieceID);
    }
  }

  public boolean areYouThere(PVector _mousePos) {
    if (_mousePos.x == pos.x && _mousePos.y == pos.y && alive) {
      return true;
    } 
    else {
      return false;
    }
  }

  //updating the position of the selected and validly moved piece
  public boolean updatePos(PVector _mousePos) {
    boolean moveMade = false;
    lastPos = new PVector(pos.x, pos.y);
    //move the piece as long as its valid
    if (_mousePos.x!=pos.x || _mousePos.y!=pos.y) {
      for (PVector _pos : validSqs) {
        if (_pos.x==_mousePos.x && _pos.y==_mousePos.y) {
          pos.set(_mousePos);
          moveMade = true;
        }
      }
    }
    selected = false;
    return moveMade;
  }

  //moving piece back to previous position
  public void resetPos() {
    pos.set(lastPos);
  }

  //where selected pieces can move
  public void validSquares() {
    validSqs = new ArrayList<PVector>();

    //is it a single move piece, king or horse?
    if (pieceID<=2 || pieceID>=9 && pieceID<=12) {
      for (int i=0; i<maxValid;i++) {
        PVector _pos= new PVector(pos.x, pos.y);
        _pos.add(validMoves[i]);
        if (selected) {                                //differs based on whether we are visualizing or moving
          if (m.validSqTest(_pos, isWhite)>1) {        //this is when we are moving
            validSqs.add(new PVector(_pos.x, _pos.y));
          }
        } 
        else {
          if (m.validSqTest(_pos, isWhite)>0) {        //this is when we are moving
            validSqs.add(new PVector(_pos.x, _pos.y));
          }
        }
      }
    }
    //is it a pawn && is it selected? - remember that moving a pawn for kills is different
    else if (pieceID>=17 && selected) {
      for (int i=1; i<validMoves.length;i++) {
        PVector _pos= new PVector(pos.x, pos.y);
        _pos.add(validMoves[i]);
        //determine if it can move forward
        if (i==1 && m.validSqTest(_pos, isWhite)>2) {
          validSqs.add(new PVector(_pos.x, _pos.y));
          //determine if it can do the two square move thing
          if (pos.y==1 || pos.y==6) {
            PVector _pos0= new PVector(pos.x, pos.y);
            _pos0.add(validMoves[0]);
            if (m.validSqTest(_pos0, isWhite)==3) {
              validSqs.add(new PVector(_pos0.x, _pos0.y));
            }
          }
        }
        // determine if it can also do the diagonal kill thing
        if (i>1 && m.validSqTest(_pos, isWhite)==2) {
          validSqs.add(new PVector(_pos.x, _pos.y));
        }
      }
    }
    else if (pieceID>=17 && !selected) {
      for (int i=2;i<4;i++) {
        PVector _pos= new PVector(pos.x, pos.y);
        _pos.add(validMoves[i]);
        if (m.validSqTest(_pos, isWhite)>0) {
          validSqs.add(new PVector(_pos.x, _pos.y));
        }
      }
    }
    //is it a multi move piece? queen, bishop, rook?
    else {
      for (int i=0; i<maxValid;i++) {
        boolean keepGoing = true;
        PVector _pos = new PVector(pos.x, pos.y);    //initiliaze temporary holder for board position test in for loop
        //need to keep check each square on a specific movement vector until its off board, blocked, or kill blocked
        while (keepGoing) {
          _pos.add(validMoves[i]);        //i doesn't change in the while loop, keepGoing becomes false when onBoard and openSqs not true, that's when we want to increment i
          if (selected) {
            if (m.validSqTest(_pos, isWhite)==3) {
              validSqs.add(new PVector(_pos.x, _pos.y));
            } 
            else if (m.validSqTest(_pos, isWhite)==2) {
              validSqs.add(new PVector(_pos.x, _pos.y));
              keepGoing = false;
            } 
            else
            {
              keepGoing = false;
            }
          }
          if (!selected) {
            if (m.validSqTest(_pos, isWhite)==3) {
              validSqs.add(new PVector(_pos.x, _pos.y));
            } 
            else if (m.validSqTest(_pos, isWhite)==1 || m.validSqTest(_pos, isWhite)==2) {
              validSqs.add(new PVector(_pos.x, _pos.y));
              keepGoing = false;
            } 
            else
            {
              keepGoing = false;
            }
          }
        }
      }
    }
  }
}

class ControlPanel {
  String whiteMove = "White to Move";
  String blackMove = "Black to Move";
  boolean _state;
  Button reviz;

  ControlPanel() {
    reviz = new Button(645, 10, "Revisualize");
    _state = false;
  }

  public void display() {
    reviz.display();
    fill(255, 215, 72, 125);
    triangle(645, 85, 665, 55, 685, 85);
    textSize(10);
    fill(255, 215, 72);
    text("Where Black Attacks!",  690, 75);
    fill(32, 47, 178, 125);
    triangle(645, 135, 665, 105, 685, 135);
    fill(32, 47, 178);
    text("Where White Attacks!",  690, 125);
    fill(0);
    textSize(14);
    text("Dead Pieces", 675, 155);
    stroke(0);
    strokeWeight(.5f);
    line(645, 160, 795, 160);
    noFill();
  }

  public void displayStart() {
    String objective = "Capture the King!";
    String start = "Hit Enter to Start the Game.";
    textSize(32);
    //shadow
    fill(0);
    text(objective, 9, 241, 300, 500);
    fill(176, 1, 0);
    text(objective, 10, 240, 300, 500);
    //text
    textSize(24);
    fill(0);
    text(start, 9, 291, 500, 100);
    fill(255, 241, 109); 
    text(start, 10, 290, 500, 100);
  }

  public void displayTurn(boolean whiteTurn) {

    if (whiteTurn) {
      fill(0);
      rect(645, 597, 145, 30, 7);
      fill(255);
      textSize(20);
      text(whiteMove, 650, 600, 200, 50);
    } 
    else {
      fill(255);
      rect(645, 597, 145, 30, 7);
      fill(0);
      textSize(20);
      text(blackMove, 650, 600, 200, 50 );
    }
  }

  public boolean revizPressed(float xpos, float ypos) {
    boolean pressed = false;
    if (reviz.press(xpos, ypos)) {
      pressed = true;
    } 
    else {
      pressed = false;
    }
    return pressed;
  }
  
  public void displayDead(PImage img, boolean isWhite, int pieceID){
    int piece = 0;
    int anchorX = 0;
    int anchorY = 150;
    if(isWhite){
      anchorX = 660;
    } else {
      anchorX = 690;
    }
    println(pieceID);
    int imageY = anchorY + (pieceID-1)*12;
    println(imageY);
    image(img, anchorX, imageY, 30,30);
  }
}

class Horse extends ChessPieces {

  Horse(boolean isWhite, int _instance) {  
    super(isWhite);
    instance = _instance;
    //create pieces
    if (isWhite) {
      img = loadImage("wh.png");
      if (instance==0) {
        pos = new PVector(1, 7);
        pieceID = 9;
      }
      else {
        pos = new PVector(6, 7);
        pieceID = 11;
      }
    }
    else {
      img = loadImage("bh.png");
      if (instance==0) {
        pos = new PVector(1, 0);
        pieceID = 10;
      }
      else {
        pos = new PVector(6, 0);
        pieceID = 12;
      }
    }
    //movement
    maxValid = 8;
    validMoves = new PVector[maxValid];
    for (int i=0; i<maxValid; i++) {
      validMoves[i] = new PVector();
    }
    validMoves[0].set(1, -2);
    validMoves[1].set(2, -1);
    validMoves[2].set(2, 1);
    validMoves[3].set(1, 2);
    validMoves[4].set(-1, 2);
    validMoves[5].set(-2, 1);
    validMoves[6].set(-1, -2);
    validMoves[7].set(-2, -1);
  }
}

class King extends ChessPieces {

  King(boolean isWhite) { 
    super(isWhite); 
    //create pieces and place in start position
    if (isWhite) {
      pos = new PVector(4, 7);
      img = loadImage("wk.png");
      pieceID = 1;
    }
    else {
      pos = new PVector(4, 0);
      img = loadImage("bk.png");
      pieceID = 2;
    }
    //movement
    maxValid = 8;
    validMoves = new PVector[maxValid];
    for (int i=0; i<maxValid; i++) {
      validMoves[i] = new PVector();
    }
    validMoves[0].set(0, -1);
    validMoves[1].set(1, -1);
    validMoves[2].set(1, 0);
    validMoves[3].set(1, 1);
    validMoves[4].set(0, 1);
    validMoves[5].set(-1, 1);
    validMoves[6].set(-1, 0);
    validMoves[7].set(-1, -1);
  }
}

class Master {
  boolean start;
  //turn state variables
  int turn;
  boolean whiteTurn;
  int inCheck;
  boolean checkMate;
  //movement variables
  PVector mousePos;
  PVector resetPos;

  //alert texts
  String check;
  boolean drawCheck;

  //player objects, might want to add conditionality for start of game
  Player[] p = new Player[2];
  Player current, opponent, temp;


  Master() {
    start = false;
    whiteTurn = true;
    mousePos = new PVector();
    resetPos = new PVector();
    check = "You are in check!";
  }

  public void boardControl() {
    current.allMoves();
    opponent.allMoves();
  }

  public void initPlayer() {
    p[0] = new Player(whiteTurn);
    p[1] = new Player(!whiteTurn);
    current = new Player();
    opponent = new Player();
    temp = new Player();
  }

  public void startTurn() {
    //determine who is the current player and the opponent
    if (whiteTurn) {
      current  = p[0];
      opponent = p[1];
    } 
    else {
      current = p[1];
      opponent = p[0];
    }

    boardControl();
    inCheck = opponent.check(current.k.pos);
    if (inCheck==2) {
      int checkMateCounter = 0;
      PVector[] killCheck = opponent.backTrack(current.k.pos);
      for (int i=0;i<killCheck.length;i++) {
        if (current.canReachSq(killCheck[i]).length==0) {
          checkMateCounter++;
        }
      }
      if (checkMateCounter!=0 && (checkMateCounter==killCheck.length)) {
        checkMate = true;
        println("checkMate pending");
      }
    }
    if (inCheck==1) {
      println("check!");
    }
  }

  public boolean askForTurn() {
    return whiteTurn;
  }

  public void mouseMaster() {
    if (!current.current.selected) {
      selectPiece();
    } 
    else {
      movePiece();
    }
  }

  public void selectPiece() {
    mousePos.set(floor(mouseX/squareSide), floor(mouseY/squareSide));
    current.select(mousePos);
  }

  public void movePiece() {
    boolean kill = false;
    boolean moveMade = false;
    mousePos.set(floor(mouseX/squareSide), floor(mouseY/squareSide));
    if (opponent.lookupPiece(mousePos)>0) {
      if (moveMade = current.move(mousePos)) {
        kill = true;
        opponent.kill(mousePos);
      }
    } 
    else {
      moveMade = current.move(mousePos);
    }
    boardControl();
    inCheck = opponent.check(current.k.pos);
    if (inCheck>0) {
      println("check!");
      current.reset();
      if (kill) {
        opponent.unKill();
      }
    }
    else if (moveMade) {
      whiteTurn = !whiteTurn;  //change turns
    }
  }

  public int validSqTest(PVector _testPos, boolean _isWhite) {
    int whatsThere;
    Player _current = new Player();
    Player _opponent = new Player();
    if (_isWhite) {
      _current = p[0];
      _opponent = p[1];
    } 
    else {
      _current = p[1];
      _opponent = p[0];
    }
    if (_testPos.x<8 && _testPos.x>=0 && _testPos.y<8 && _testPos.y>=0) {
      if (_current.lookupPiece(_testPos)>0) {
        whatsThere = 1;//own piece
      } 
      else if (_opponent.lookupPiece(_testPos)>0) {
        whatsThere = 2;//opponent piece
      } 
      else {
        whatsThere = 3;//empty square
      }
    }
    else {
      whatsThere = 0;//not on board
    }
    return whatsThere;
  }

  //  PVector squareWinner(PVector loc) {
  //    PVector tri=new PVector();
  //    String[] white = split(m.p[0].allMoves.get(loc), ",");
  //    String[] black = split(m.p[1].allMoves.get(loc), ",");
  //    //get rid of nulls
  //    //decide a winner
  //    if (white.length==1 && black.length==1) {
  //      if (int(white[0])==0 && int(black[0])==0) {
  //        ;
  //      }
  //      else if (int(white[0])!=0 && int(black[0])==0) {
  //        winner=-2;
  //      }
  //      else if (int(white[0])==0 && int(black[0])!=0) {
  //        winner=-3;
  //      }
  //      else {
  //        winner=3;
  //      }
  //    }
  //    else if (white.length==black.length) {
  //      winner = 3;
  //    }
  //    else if (white.length>black.length) {
  //      winner = -2;
  //    }
  //    else {
  //      winner = -3;
  //    }
  //    return winner;
  //  }

  //  int[] squareOccupants(PVector loc){
  //    String[] white = split(m.p[0].allMoves.get(loc), ",");
  //    String[] black = split(m.p[1].allMoves.get(loc), ",");
  //    int[] stuff={}
  //    return;
  //  }
}

class Pawn extends ChessPieces {

  Pawn(boolean isWhite, int _instance) {  
    super(isWhite);
    instance = _instance;
    //create pieces & place in start pos
    if (isWhite) {
      img = loadImage("wp.png");
      for (int i=0; i<8; i++) {
        if (instance==i) {
          pos= new PVector(i, 6);
          pieceID = 17+(i*2);
        }
      }
    }
    else {
      img = loadImage("bp.png");
      for (int i=0; i<8; i++) {
        if (instance==i) {
          pos= new PVector(i, 1);
          pieceID = 18+(i*2);
        }
      }
    }
    //movement
    maxValid = 4;
    validMoves = new PVector[maxValid];
    for (int i=0; i<maxValid; i++) {
      validMoves[i] = new PVector();
    }
    if (isWhite) {
      validMoves[0].set(0,-2);
      validMoves[1].set(0,-1);
      validMoves[2].set(-1,-1);
      validMoves[3].set(1,-1);
    } else {
      validMoves[0].set(0,2);
      validMoves[1].set(0,1);
      validMoves[2].set(-1,1);
      validMoves[3].set(1,1);
    }
  }
}

class Player {
  boolean isWhite;
  int state;
  boolean check;
  HashMap<PVector, String> allMoves = new HashMap<PVector, String>();
  ChessPieces current, killed, last;
  King k;
  Queen q;
  Bishop[] b = new Bishop[2];
  Horse[] h = new Horse[2];
  Rook[] r = new Rook[2];
  Pawn[] p = new Pawn[8];

  Player() {
  }

  Player(boolean _isWhite) {
    isWhite = _isWhite;
    check = false;
    current = new ChessPieces();
    k = new King(isWhite);
    q = new Queen(isWhite);
    for (int i=0; i<8; i++) {
      if (i<2) {
        b[i]=new Bishop(isWhite, i);
        h[i]=new Horse(isWhite, i);
        r[i]=new Rook(isWhite, i);
      }
      p[i]=new Pawn(isWhite, i);
    }
  }

  //displays the pieces
  public void displayPieces() {
    k.display();
    q.display();
    for (int i=0; i<8; i++) {
      if (i<2) {
        b[i].display();
        h[i].display();
        r[i].display();
      }
      p[i].display();
    }
  }

  //selects a piece
  public void select(PVector _mousePos) {
    int found = 0;
    if (k.areYouThere(_mousePos)) {
      current = k;
      found = 1;
    }
    if (q.areYouThere(_mousePos)) {
      current = q;
      found = 1;
      q.pos.set(_mousePos.x, _mousePos.y);
    } 
    for (int i=0;i<8;i++) {
      if (i<2) {
        if (b[i].areYouThere(_mousePos)) {    
          current = b[i];
          found = 1;
        }
        if (h[i].areYouThere(_mousePos)) {      
          current = h[i];
          found = 1;
        }
        if (r[i].areYouThere(_mousePos)) {      
          current = r[i];
          found = 1;
        }
      }
      if (p[i].areYouThere(_mousePos)) {      
        current = p[i];
        found = 1;
      }
    }
    if (found==1) {
      current.selected = true;
    }
  }

  //updates the values of the HashMap based on validsquare data passed in as 'pieceID' parameter
  public String updateSq(String hashVal, int pieceID) {
    boolean found=false;    //if a zero or the pieceID in question is found, we need to do specific things
    String _returnVal = new String(); 
    //parse String
    String[] parsedHash = split(hashVal, ",");
    for (int i=0; i<parsedHash.length; i++) {
      if (PApplet.parseInt(parsedHash[i])==0) {
        found = true;
        _returnVal = str(pieceID);
      }
      else if (PApplet.parseInt(parsedHash[i])==pieceID) {
        found = true;
        _returnVal = hashVal;
      }
    }  
    if (!found) {        //if zero or pieceID in question not found, do the append
      _returnVal = hashVal+","+str(pieceID);
    }
    return _returnVal;
  }


  //creates the HashMap of positions/squares of all pieces for this player
  public void allMoves() {
    initHash();
    String hashVal;
    if (k.alive) {
      k.validSquares();
      for (PVector pos: k.validSqs) {
        hashVal = allMoves.get(pos);
        allMoves.put(pos, updateSq(hashVal, k.pieceID));
      }
    }
    if (q.alive) {
      q.validSquares();
      for (PVector pos: q.validSqs) {
        hashVal = allMoves.get(pos);
        allMoves.put(pos, updateSq(hashVal, q.pieceID));
      }
    }
    for (int i=0; i<8; i++) {
      if (i<2) {
        if (b[i].alive) {
          b[i].validSquares();
          for (PVector pos: b[i].validSqs) {
            hashVal = allMoves.get(pos);
            allMoves.put(pos, updateSq(hashVal, b[i].pieceID));
          }
        }
        if (h[i].alive) {
          h[i].validSquares();
          for (PVector pos: h[i].validSqs) {
            hashVal = allMoves.get(pos);
            allMoves.put(pos, updateSq(hashVal, h[i].pieceID));
          }
        }
        if (r[i].alive) {
          r[i].validSquares();
          for (PVector pos: r[i].validSqs) {
            hashVal = allMoves.get(pos);
            allMoves.put(pos, updateSq(hashVal, r[i].pieceID));
          }
        }
      }
      if (p[i].alive) {
        p[i].validSquares();
        for (PVector pos: p[i].validSqs) {
          hashVal = allMoves.get(pos);
          allMoves.put(pos, updateSq(hashVal, p[i].pieceID));
        }
      }
    }
  }

  //moves a piece
  public boolean move(PVector _mousePos) {
    return current.updatePos(_mousePos);
  }

  //kills a piece
  public void kill(PVector _mousePos) {
    if (k.areYouThere(_mousePos)) {
      killed = k;
      k.alive = false;
    }
    if (q.areYouThere(_mousePos)) {
      killed = q;
      q.alive = false;
    } 
    for (int i=0;i<8;i++) {
      if (i<2) {
        if (b[i].areYouThere(_mousePos)) {    
          killed = b[i];
          b[i].alive = false;
        }
        if (h[i].areYouThere(_mousePos)) {      
          killed = h[i];
          h[i].alive = false;
        }
        if (r[i].areYouThere(_mousePos)) {      
          killed = r[i];
          r[i].alive = false;
        }
      }
      if (p[i].areYouThere(_mousePos)) {      
        killed = p[i];
        p[i].alive = false;
      }
    }
  }

  public void reset() {
    current.resetPos();
  }

  public void unKill() {
     killed.alive = true;
  }

  //looks up the piece at a position
  public int lookupPiece(PVector _testPos) {
    int found = 0;
    if (k.areYouThere(_testPos)) { 
      found=k.pieceID;
    }
    if (q.areYouThere(_testPos)) {
      found=q.pieceID;
    }
    for (int i=0;i<8;i++) {
      if (i<2) {
        if (b[i].areYouThere(_testPos)) {    
          found=b[i].pieceID;
        }
        if (h[i].areYouThere(_testPos)) {      
          found=h[i].pieceID;
        }
        if (r[i].areYouThere(_testPos)) {      
          found=r[i].pieceID;
        }
      }
      if (p[i].areYouThere(_testPos)) {      
        found=p[i].pieceID;
      }
    }
    return found;
  }

  //looks up the position of a piece
  public PVector lookupPos(int _pieceID) {
    PVector v = new PVector();
    if (k.pieceID== _pieceID) {
      v.set(k.pos.x, k.pos.y);
    } 
    if (q.pieceID==_pieceID) {
      v.set(q.pos.x, q.pos.y);
    } 
    for (int i=0;i<8;i++) {
      if (i<2) {
        if (b[i].pieceID==_pieceID) {    
          v.set(b[i].pos.x, b[i].pos.y);
        }
        if (h[i].pieceID==_pieceID) {      
          v.set(h[i].pos.x, h[i].pos.y);
        }
        if (r[i].pieceID==_pieceID) {      
          v.set(r[i].pos.x, r[i].pos.y);
        }
      }
      if (p[i].pieceID==_pieceID) {      
        v.set(p[i].pos.x, p[i].pos.y);
      }
    }
    return v;
  }

  public int check(PVector _oppKing) {
    int inCheck=0;
    int checkForMate=0;
    ArrayList<PVector> mateSqs = new ArrayList<PVector>();
    if (canReachSq(_oppKing).length>0) {
      inCheck++;
    }
    if (inCheck>0) {
      for (int i=0; i<k.maxValid;i++) {
        PVector _pos= new PVector(_oppKing.x, _oppKing.y);
        _pos.add(k.validMoves[i]);        
        if (m.validSqTest(_pos, !isWhite)>1) {
          mateSqs.add(new PVector(_pos.x, _pos.y));
          if (canReachSq(_pos).length>0) {
            checkForMate++;
          }
        }
      }
    }
    if (checkForMate!=0 && (checkForMate==mateSqs.size())) {
      inCheck++;
    }
    return inCheck;    //inCheck=1 means NOT surrounded, inCheck=2 means surrounded
  }

  //this returns the set of pieces that can reach the square passed in as parameter
  public int[] canReachSq(PVector pos) {
    boolean returnSomething = false;
    int[] canReach = PApplet.parseInt(split(allMoves.get(pos), ","));
    int[] nothing = {
    };
    for (int i=0;i<canReach.length;i++) {
      if (canReach[i]!=0) {
        returnSomething=true;
      }
    }
    if (returnSomething) {
      return canReach;
    } 
    else {
      return nothing;
    }
  }
  
  //backtrack tells us what squares can reach the square passed as parameter
  public PVector[] backTrack(PVector pos) {
    PVector[] sqCanReach = new PVector[canReachSq(pos).length];
    for (int i=0;i<canReachSq(pos).length;i++) {
      sqCanReach[i]=lookupPos(canReachSq(pos)[i]);
    }
    return sqCanReach;
  }

  //creates the HashMap of squares and all the pieces that can reach a square
  public void initHash() {
    for (int i=0;i<8;i++) {
      for (int j=0;j<8;j++) {
        PVector loc = new PVector(i, j);
        String _pieceID = str(lookupPiece(loc));
        allMoves.put(loc, str(0));
      }
    }
  }
}

class Queen extends ChessPieces {

  Queen(boolean isWhite) { 
    super(isWhite);
    //create pieces and place in start pos
    if (isWhite) {
      pos = new PVector(3, 7);
      img = loadImage("wq.png");
      pieceID = 3;
    }
    else {
      pos = new PVector(3, 0);
      img = loadImage("bq.png");
      pieceID = 4;
    }
    //movement
    maxValid = 8;
        validMoves = new PVector[maxValid];
    for (int i=0; i<maxValid; i++) {
      validMoves[i] = new PVector();
    }
    validMoves[0].set(0, -1);
    validMoves[1].set(1, -1);
    validMoves[2].set(1, 0);
    validMoves[3].set(1, 1);
    validMoves[4].set(0, 1);
    validMoves[5].set(-1, 1);
    validMoves[6].set(-1, 0);
    validMoves[7].set(-1, -1);
  }
}

class Rook extends ChessPieces {

  Rook(boolean isWhite, int _instance) {  
    super(isWhite);
    instance = _instance;
    //create pieces and place in start pos
    if (isWhite) {
      img = loadImage("wr.png");
      if (instance==0) {
        pos = new PVector(0, 7);
        pieceID = 13;
      }
      else {
        pos = new PVector(7, 7);
        pieceID = 15;
      }
    }
    else {
      img = loadImage("br.png");
      if (instance==0) {
        pos = new PVector(0, 0);
        pieceID = 14;
      }
      else {
        pos = new PVector(7, 0);
        pieceID = 16;
      }
    }
    //movement
    maxValid = 4;
    validMoves = new PVector[maxValid];
    for (int i=0; i<maxValid; i++) {
      validMoves[i] = new PVector();
    }
    validMoves[0].set(0, -1);
    validMoves[1].set(1, 0);
    validMoves[2].set(0, 1);
    validMoves[3].set(-1, 0);
  }
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "chess_reviz" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
