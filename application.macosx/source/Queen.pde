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

