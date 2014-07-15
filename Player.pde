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
  void displayPieces() {
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
  void select(PVector _mousePos) {
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
  String updateSq(String hashVal, int pieceID) {
    boolean found=false;    //if a zero or the pieceID in question is found, we need to do specific things
    String _returnVal = new String(); 
    //parse String
    String[] parsedHash = split(hashVal, ",");
    for (int i=0; i<parsedHash.length; i++) {
      if (int(parsedHash[i])==0) {
        found = true;
        _returnVal = str(pieceID);
      }
      else if (int(parsedHash[i])==pieceID) {
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
  void allMoves() {
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
  boolean move(PVector _mousePos) {
    return current.updatePos(_mousePos);
  }

  //kills a piece
  void kill(PVector _mousePos) {
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

  void reset() {
    current.resetPos();
  }

  void unKill() {
     killed.alive = true;
  }

  //looks up the piece at a position
  int lookupPiece(PVector _testPos) {
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
  PVector lookupPos(int _pieceID) {
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

  int check(PVector _oppKing) {
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
  int[] canReachSq(PVector pos) {
    boolean returnSomething = false;
    int[] canReach = int(split(allMoves.get(pos), ","));
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
  PVector[] backTrack(PVector pos) {
    PVector[] sqCanReach = new PVector[canReachSq(pos).length];
    for (int i=0;i<canReachSq(pos).length;i++) {
      sqCanReach[i]=lookupPos(canReachSq(pos)[i]);
    }
    return sqCanReach;
  }

  //creates the HashMap of squares and all the pieces that can reach a square
  void initHash() {
    for (int i=0;i<8;i++) {
      for (int j=0;j<8;j++) {
        PVector loc = new PVector(i, j);
        String _pieceID = str(lookupPiece(loc));
        allMoves.put(loc, str(0));
      }
    }
  }
}

