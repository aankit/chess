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

  void boardControl() {
    current.allMoves();
    opponent.allMoves();
  }

  void initPlayer() {
    p[0] = new Player(whiteTurn);
    p[1] = new Player(!whiteTurn);
    current = new Player();
    opponent = new Player();
    temp = new Player();
  }

  void startTurn() {
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

  boolean askForTurn() {
    return whiteTurn;
  }

  void mouseMaster() {
    if (!current.current.selected) {
      selectPiece();
    } 
    else {
      movePiece();
    }
  }

  void selectPiece() {
    mousePos.set(floor(mouseX/squareSide), floor(mouseY/squareSide));
    current.select(mousePos);
  }

  void movePiece() {
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

  int validSqTest(PVector _testPos, boolean _isWhite) {
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

