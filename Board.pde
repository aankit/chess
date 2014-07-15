class Board {
  color fillValue;
  color lightSq;
  color darkSq;
  color white;
  color black;

  Board() {
    lightSq = color(255, 255, 255);
    darkSq = color(225, 153, 51);
  }

  void display(PVector loc, PVector origin, boolean reviz, boolean isWhite) {
    for(int i=0;i<9;i++){
      stroke(155);
      strokeWeight(.5);
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
          pt2.set(pt1.x+(dirX*.87*squareSide/2), pt1.y);
          pt3.set(pt1.x, pt1.y+(dirY*.87*squareSide/2));
        } 
        else {
          pt2.set(pt1.x+(dirX*squareSide/2), pt1.y);
          pt3.set(pt1.x, pt1.y+(dirY*squareSide/2));
        }
      }

      stroke(0);
      strokeWeight(0.5);
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


  color displayColor(boolean isWhite) {
    color c = color(0);
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

  boolean layer(PVector pt1, float dirX, float dirY, boolean isWhite) {
    int offset=0;
    loadPixels();
    int testX = int((pt1.x+(dirX*squareSide/4)));
    int testY = int((pt1.y+(dirY*squareSide/4)));
    color test = pixels[testX+(testY*width)];
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

