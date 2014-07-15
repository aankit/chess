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

  void display() {
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
        image(img, imageX, imageY, squareSide*.5, squareSide*.5);
      }
    } 
    else {
      cp.displayDead(img, isWhite, pieceID);
    }
  }

  boolean areYouThere(PVector _mousePos) {
    if (_mousePos.x == pos.x && _mousePos.y == pos.y && alive) {
      return true;
    } 
    else {
      return false;
    }
  }

  //updating the position of the selected and validly moved piece
  boolean updatePos(PVector _mousePos) {
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
  void resetPos() {
    pos.set(lastPos);
  }

  //where selected pieces can move
  void validSquares() {
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

