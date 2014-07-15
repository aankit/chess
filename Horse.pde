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

