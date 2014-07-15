class Button {
  boolean press;
  float bwidth;
  float bheight;
  float bround;
  float x;
  float y;
  String btext;

  Button(float _x, float _y, String _btext) {
    x = _x;
    y = _y;
    btext = _btext;
    press = false;
    bwidth = 145;
    bheight = 30;
    bround = 7;
  }

  void display() {
    if (press) {
      noStroke();
      fill(242, 235, 222);
      rect(x, y, bwidth, bheight, 7);
      fill(255, 0, 0);
      text(btext, x+8, y+3, bwidth, bheight);
    } 
    else {
      noStroke();
      fill(0);
      rect(x+3, y+2, bwidth, bheight, 7);
      fill(242, 235, 222);
      rect(x, y, bwidth, bheight, 7);
      fill(0);
      text(btext, x+5, y+3, bwidth-5, bheight-3);
    }
  }

  boolean press(float xpos, float ypos) {
    if(xpos>x && ypos>y && xpos<(x+bwidth) && ypos<(y+bheight)){
      press = !press;
    }
    return press;
  }
}

