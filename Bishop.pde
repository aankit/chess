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

