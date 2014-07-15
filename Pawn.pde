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

