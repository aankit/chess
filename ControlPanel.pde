class ControlPanel {
  String whiteMove = "White to Move";
  String blackMove = "Black to Move";
  boolean _state;
  Button reviz;

  ControlPanel() {
    reviz = new Button(645, 10, "Revisualize");
    _state = false;
  }

  void display() {
    reviz.display();
    fill(255, 215, 72, 125);
    triangle(645, 85, 665, 55, 685, 85);
    textSize(10);
    fill(255, 215, 72);
    text("Where Black Attacks!",  690, 75);
    fill(32, 47, 178, 125);
    triangle(645, 135, 665, 105, 685, 135);
    fill(32, 47, 178);
    text("Where White Attacks!",  690, 125);
    fill(0);
    textSize(14);
    text("Dead Pieces", 675, 155);
    stroke(0);
    strokeWeight(.5);
    line(645, 160, 795, 160);
    noFill();
  }

  void displayStart() {
    String objective = "Capture the King!";
    String start = "Hit Enter to Start the Game.";
    textSize(32);
    //shadow
    fill(0);
    text(objective, 9, 241, 300, 500);
    fill(176, 1, 0);
    text(objective, 10, 240, 300, 500);
    //text
    textSize(24);
    fill(0);
    text(start, 9, 291, 500, 100);
    fill(255, 241, 109); 
    text(start, 10, 290, 500, 100);
  }

  void displayTurn(boolean whiteTurn) {

    if (whiteTurn) {
      fill(0);
      rect(645, 597, 145, 30, 7);
      fill(255);
      textSize(20);
      text(whiteMove, 650, 600, 200, 50);
    } 
    else {
      fill(255);
      rect(645, 597, 145, 30, 7);
      fill(0);
      textSize(20);
      text(blackMove, 650, 600, 200, 50 );
    }
  }

  boolean revizPressed(float xpos, float ypos) {
    boolean pressed = false;
    if (reviz.press(xpos, ypos)) {
      pressed = true;
    } 
    else {
      pressed = false;
    }
    return pressed;
  }
  
  void displayDead(PImage img, boolean isWhite, int pieceID){
    int piece = 0;
    int anchorX = 0;
    int anchorY = 150;
    if(isWhite){
      anchorX = 660;
    } else {
      anchorX = 690;
    }
    println(pieceID);
    int imageY = anchorY + (pieceID-1)*12;
    println(imageY);
    image(img, anchorX, imageY, 30,30);
  }
}

