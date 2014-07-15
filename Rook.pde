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

