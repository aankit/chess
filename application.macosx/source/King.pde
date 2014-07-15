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

