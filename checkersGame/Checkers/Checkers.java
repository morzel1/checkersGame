/*Checkers base game influenced by https://www.youtube.com/watch?v=RrubifTAIDA */
/*AI and game modifications written by Michael Orzel 4/17/17*/
package Checkers;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Checkers extends JPanel implements ActionListener, MouseListener {
 private static final long serialVersionUID = 1L;
 public static int width = 800, height = width; 
 public static final int TileSize = width/8; 
 public static final int TileRows = width/TileSize;
 public static int[][] BoardLayout = new int[TileRows][TileRows]; //init board layout
 public static int[][] PieceLayout = new int[TileRows][TileRows]; //keeps track of pieces throughout the game
 public static final int EMPTY = 0, RED = 1, RED_KING = 2, WHITE = 3, WHITE_KING = 4;
 public static JFrame frame;
 public boolean gameInProgress = true;
 public int currentPlayer = RED;
 public boolean inPlay = false; 
 public static int[][] availablePlays = new int[TileRows][TileRows];
 public int storedRow, storedCol;
 public boolean isJump = false;
 public boolean tempJump = false;
 static BufferedImage crownImage = null;
 public boolean turnEND = false;
 public int choice1, choice2, choice3, choice4, choice5, choice6;
 public int choice7, choice8, choice9, choice10, choice11, choice12;
 public int tempScore1 = 0;
 public int tempScore2 = 0;
 public static int[][] possibleMoves = new int [TileRows][TileRows];
 public static int[][] TempLayout1 = new int[TileRows][TileRows];
 public static int[][] TempLayout2 = new int[TileRows][TileRows];
 public int jumpCol, jumpRow;
 public int ColTo, RowTo;
 public static int[][] evalPieces = new int [TileRows][TileRows];
 public static int[][] evalOpponent = new int [TileRows][TileRows];
 public int initialPiece;
 public static int[][] scoreEval = new int [TileRows][TileRows];
 public int testCounter = 0;
 
 public Checkers(){
  window(width, height, this);
  initializeBoard();
  repaint(); 
 }
 //End condition
 public boolean gameOver(){ 
  return gameOverInternal(0, 0, 0, 0);
 }
 //Checks if game is over
 public boolean gameOverInternal(int col, int row, int red, int white){ 
  if(PieceLayout[col][row] == RED || PieceLayout[col][row] == RED_KING)
   red += 1;
  if(PieceLayout[col][row] == WHITE || PieceLayout[col][row] == WHITE_KING)
   white += 1;
  if(col == TileRows-1 && row == TileRows-1){
   if(red == 0 || white == 0)
    return true;
   else return false;
  }
  if(col == TileRows-1){
   row += 1;
   col = -1;
  }
  return gameOverInternal(col+1, row, red, white);
 }
 
 //Parameters for the game window
 public void window(int width, int height, Checkers game){
  JFrame frame = new JFrame();
  frame.setSize(width, height);
  frame.setBackground(Color.white);
  frame.setLocationRelativeTo(null);
  frame.pack();
  Insets insets = frame.getInsets();
  int frameLeftBorder = insets.left;
  int frameRightBorder = insets.right;
  int frameTopBorder = insets.top;
  int frameBottomBorder = insets.bottom;
  frame.setPreferredSize(new Dimension(width + frameLeftBorder + frameRightBorder, height + frameBottomBorder + frameTopBorder));
  frame.setMaximumSize(new Dimension(width + frameLeftBorder + frameRightBorder, height + frameBottomBorder + frameTopBorder));
  frame.setMinimumSize(new Dimension(width + frameLeftBorder + frameRightBorder, height + frameBottomBorder + frameTopBorder));
  frame.setLocationRelativeTo(null);
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  frame.addMouseListener(this);
  frame.requestFocus();
  frame.setVisible(true);
  frame.add(game);
 }
 
 //Label the pieces
 public void initializeBoard(){
    for(int col=0; col < (TileRows); col+=2){
     PieceLayout[col][5] = RED;
     PieceLayout[col][7] = RED;
    }
    for(int col=1; col < (TileRows); col+=2)
     PieceLayout[col][6] = RED;
    for(int col=1; col < (TileRows); col+=2){
     PieceLayout[col][0] = WHITE;
     PieceLayout[col][2] = WHITE;
    } 
    for(int col=0; col < (TileRows); col+=2)
     PieceLayout[col][1] = WHITE;
 }
 
 public static void drawPiece(int col, int row, Graphics g, Color color){
  ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
     ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
  g.setColor(color);
  g.fillOval((col*TileSize)+2, (row*TileSize)+2, TileSize-4, TileSize-4);
 }
 
 //Colors stuff and adds crowns
 public void paint(Graphics g){
  super.paintComponent(g);
  for(int row = 0; row < TileRows; row++){
   for(int col = 0; col < TileRows; col++){
    if((row%2 == 0 && col%2 == 0) || (row%2 != 0 && col%2 != 0)){ // This assigns the checkerboard pattern
     BoardLayout[col][row] = 0;
     g.setColor(Color.white);
     g.fillRect(col*TileSize, row*TileSize, TileSize, TileSize);
    }
    else{
     BoardLayout[col][row] = 1;
     g.setColor(Color.darkGray);
     g.fillRect(col*TileSize, row*TileSize, TileSize, TileSize);
    }
    if(checkTeamPiece(col, row) ==  true){   
     g.setColor(Color.darkGray.darker());
     g.fillRect(col*TileSize, row*TileSize, TileSize, TileSize);
    }
    if(availablePlays[col][row] == 1){
     g.setColor(Color.CYAN.darker());
     g.fillRect(col*TileSize, row*TileSize, TileSize, TileSize);
    }
    if(PieceLayout[col][row] == WHITE)
     drawPiece(col, row, g, Color.white);
    else if(PieceLayout[col][row] == WHITE_KING){
     drawPiece(col, row, g, Color.white);
     g.drawImage(crownImage, (col*TileSize)+6, (row*TileSize)+6, TileSize-12, TileSize-12, null);
    }
    else if(PieceLayout[col][row] == RED)
     drawPiece(col, row, g, Color.red);
    else if(PieceLayout[col][row] == RED_KING){
     drawPiece(col, row, g, Color.red);
    g.drawImage(crownImage, (col*TileSize)+6, (row*TileSize)+6, TileSize-12, TileSize-12, null);
    }
   }
  }
  if(gameOver() == true)
   gameOverDisplay(g);
 } 
 
 public void gameOverDisplay(Graphics g) { 
   String msg = "Game Over";
      Font small = new Font("Arial", Font.BOLD, 20);
      FontMetrics metr = getFontMetrics(small);
      g.setColor(Color.white);
      g.setFont(small);
      g.drawString(msg, (width - metr.stringWidth(msg)) / 2, width / 2);
 }
 //stops impossible moves
 public void resetPlay(){
  storedCol = 0;
  storedRow = 0;
  inPlay = false;
  isJump = false;
  turnEND = false;
  for(int row = 0; row < TileRows; row++){
   for(int col = 0; col < TileRows; col++){
    availablePlays[col][row] = 0;
    
   }
  }
  repaint();
 }
 //mouse player controls
 public void mousePressed(java.awt.event.MouseEvent evt) {
     int col = (evt.getX()-8) / TileSize; 
        int row = (evt.getY()-30) / TileSize; 
  
  if(inPlay == false && PieceLayout[col][row] != 0 || inPlay == true && checkTeamPiece(col, row) == true){
   resetPlay();
   storedCol = col;
   storedRow = row;
   getAvailablePlays(col, row);
  }
  else if(inPlay == true && availablePlays[col][row] == 1){
   makeMove(col, row, storedCol, storedRow);
  }
  else if(inPlay == true && availablePlays[col][row] == 0){
   resetPlay();
  }
 }
 
 public void swapPlayer(){
  if(currentPlayer == RED){
   currentPlayer = WHITE;
  }else{
   currentPlayer = RED;
  }
  
  if(currentPlayer == WHITE){
  System.out.println("Computer turn");
  ComputerTurn();
  }
  
   
 }
 
 public void ComputerTurn(){
  currentPlayer = WHITE;
  //Fills array with all possible moves
  for(int i=0; i< TileRows; i++){
   for(int j=0; j<TileRows; j++){
    if(PieceLayout[i][j] == 3 || PieceLayout[i][j] == 4){
     getAvailablePlays(i,j);

     if(choice1 == 1 || choice3 == 1 || choice5 == 1 || choice7 == 1 || choice9 == 1 || choice11 == 1){
      possibleMoves[i][j] = 1;
      scoreEval[i][j] = 1;
     }
     if(choice2 == 1 || choice4 == 1 || choice6 == 1 || choice8 == 1 || choice10 == 1 || choice12 ==1){
      possibleMoves[i][j] = 0;
      scoreEval[i][j] = 0;
     }
     
     choice1 = 0;
     choice2 = 0;
     choice3 = 0;
     choice4 = 0;
     choice5 = 0;
     choice6 = 0;
     choice7 = 0;
     choice8 = 0;
     choice9 = 0;
     choice10 = 0;
     choice11 = 0;
     choice12 = 0;
     
         
    } //if statement checking for white pieces  
   } // j loop
  } // i loop
  RiskEvaluation();
  
  //final move code call
  
  //Clear loop
  for(int k=0; k< TileRows ; k++){
   for(int l=0; l<TileRows;l++){
   possibleMoves[k][l] = EMPTY;
   evalPieces[k][l] = 0;
    }
  } 
 }   //end loop
 
 public void RiskEvaluation(){
  Point[] scoreFinal = new Point[100];
  for(int start = 0; start<scoreFinal.length; start++){
   scoreFinal[start] = new Point();
  }
  
  int counterMoves = 0;
  
  //This part evaluates opponent's moves when computer moves
  for(int y=0; y<TileRows; y++){
   for(int z=0; z<TileRows; z++){
    //Resets temporary layout
    for(int k=0; k<TileRows; k++){
     for(int l = 0 ; l < TileRows; l++){
      TempLayout1[k][l] = PieceLayout[k][l];
      evalPieces[k][l] = 0; //resets evalPieces
      TEMPgetAvailablePlays(k,l); //re-populates evalPieces
      choice1 = 0;
      choice2 = 0;
      choice3 = 0;
      choice4 = 0;
      choice5 = 0;
      choice6 = 0;
      choice7 = 0;
      choice8 = 0;
      choice9 = 0;
      choice10 = 0;
      choice11 = 0;
      choice12 = 0;
     }
    } //end of k/l loop
    //System.out.println(choice1 + " " + choice2 + " " + choice3 + " " + choice4 + " " + choice5 + " " + choice6 + " " + choice7 + " " + choice8 + " " + choice9 + " " + choice10 + " " + choice11 + " " + choice12);
    
    int tempCounter = evalPieces[y][z];

    if(tempCounter > 0){
     if(inPlay == true && availablePlays[y][z] == 0){
      resetPlay();
     }
     //System.out.println(y + " Y " + z + " z " + jumpCol + " " + jumpRow);
     int beginningPiece = TempLayout1[y][z];
     if(beginningPiece == 3){ //This loop checks the moves for a regular white piece
     for(int compMoves = 0; compMoves < tempCounter; compMoves++){  
      if(compMoves == 0){
      TEMPgetAvailablePlays(y,z);
      tempMove(y,z,jumpCol,jumpRow);
      scoreFinal[counterMoves].x = y;
      scoreFinal[counterMoves].y = z;
      scoreFinal[counterMoves].jumpX = jumpCol;
      scoreFinal[counterMoves].jumpY = jumpRow;
      if(choice1 == 1 || choice3 == 1 || choice5 == 1 || choice7 == 1 || choice9 == 1 || choice11 == 1 && isJump==true){
       scoreFinal[counterMoves].score = 2;
      }
      if((choice1 == 1 || choice3 == 1 || choice5 == 1 || choice7 == 1 || choice9 == 1 || choice11 == 1) && (scoreFinal[counterMoves].jumpX == 0 || scoreFinal[counterMoves].jumpX == 7 || scoreFinal[counterMoves].jumpY == 7)){
       scoreFinal[counterMoves].score = 1;
      }
      if(scoreFinal[counterMoves].jumpY == 7){
       scoreFinal[counterMoves].score = 4;
      }
      /*if(choice2 == 1 || choice4 == 1 || choice6 == 1 || choice8 == 1 || choice10 == 1 || choice12 == 1){
       scoreFinal[counterMoves].score = 0;
      }*/
      counterMoves++;
      }
      testCounter++;
      if(compMoves == 1){
       TempLayout1[y][z] = beginningPiece;
       TempLayout1[jumpCol][jumpRow] = beginningPiece;
       TEMPgetAvailablePlays(y,z);
       tempMove(y,z,jumpCol,jumpRow);
       TempLayout1[y][z]=0;
       scoreFinal[counterMoves].x = y;
       scoreFinal[counterMoves].y = z;
       scoreFinal[counterMoves].jumpX = jumpCol;
       scoreFinal[counterMoves].jumpY = jumpRow;
       if(choice1 == 1 || choice3 == 1 || choice5 == 1 || choice7 == 1 || choice9 == 1 || choice11 == 1 && isJump==true){
        scoreFinal[counterMoves].score = 2;
       }
       if((choice1 == 1 || choice3 == 1 || choice5 == 1 || choice7 == 1 || choice9 == 1 || choice11 == 1) && (scoreFinal[counterMoves].jumpX == 0 || scoreFinal[counterMoves].jumpX == 7 || scoreFinal[counterMoves].jumpY == 7)){
        scoreFinal[counterMoves].score = 1;
       }
       if(scoreFinal[counterMoves].jumpY == 7){
        scoreFinal[counterMoves].score = 4;
       }
       counterMoves++;
      }
     }
     } //end of if 3
     if(beginningPiece == 4){
      tempCounter = tempCounter+tempCounter;
      for(int compMoves = 0; compMoves < tempCounter; compMoves++){
       if(compMoves == 0){
       TEMPgetUp(y,z);
       tempMove(y,z,jumpCol,jumpRow);
       scoreFinal[counterMoves].x = y;
       scoreFinal[counterMoves].y = z;
       scoreFinal[counterMoves].jumpX = jumpCol;
       scoreFinal[counterMoves].jumpY = jumpRow;
       if(choice1 == 1 || choice3 == 1 || choice5 == 1 || choice7 == 1 || choice9 == 1 || choice11 == 1){
        scoreFinal[counterMoves].score = 1;
       }
       counterMoves++;
       }
       testCounter++;
       if(compMoves == 1){
        TempLayout1[y][z] = beginningPiece;
        TempLayout1[jumpCol][jumpRow] = beginningPiece;
        TEMPgetUp(y,z);
        tempMove(y,z,jumpCol,jumpRow);
        TempLayout1[y][z]=0;
        scoreFinal[counterMoves].x = y;
        scoreFinal[counterMoves].y = z;
        scoreFinal[counterMoves].jumpX = jumpCol;
        scoreFinal[counterMoves].jumpY = jumpRow;
        if(choice1 == 1 || choice3 == 1 || choice5 == 1 || choice7 == 1 || choice9 == 1 || choice11 == 1){
         scoreFinal[counterMoves].score = 1;
        }
        counterMoves++;
       }
       if(compMoves == 2){
         TEMPgetDown(y,z);
         tempMove(y,z,jumpCol,jumpRow);
         scoreFinal[counterMoves].x = y;
         scoreFinal[counterMoves].y = z;
         scoreFinal[counterMoves].jumpX = jumpCol;
         scoreFinal[counterMoves].jumpY = jumpRow;
         if(choice1 == 1 || choice3 == 1 || choice5 == 1 || choice7 == 1 || choice9 == 1 || choice11 == 1){
          scoreFinal[counterMoves].score = 1;
         }
         counterMoves++;
       }
       if(compMoves == 3){
        TempLayout1[y][z] = beginningPiece;
        TempLayout1[jumpCol][jumpRow] = beginningPiece;
        TEMPgetDown(y,z);
        tempMove(y,z,jumpCol,jumpRow);
        TempLayout1[y][z]=0;
        scoreFinal[counterMoves].x = y;
        scoreFinal[counterMoves].y = z;
        scoreFinal[counterMoves].jumpX = jumpCol;
        scoreFinal[counterMoves].jumpY = jumpRow;
        if(choice1 == 1 || choice3 == 1 || choice5 == 1 || choice7 == 1 || choice9 == 1 || choice11 == 1){
         scoreFinal[counterMoves].score = 1;
        }
        counterMoves++;
       }
       counterMoves--; //check for pieces next to it
       counterMoves++;
      } // for loop for king
     } // if statement for king
    } //tempCounter>0 bracket
    //System.out.println("Eval piece value " + evalPieces[y][z]);
    //System.out.print("Before opponent with " + y + "," + z + " "+ scoreEval[y][z] + " ");
    
      for(int a=0; a<TileRows; a++){
     for(int b = 0 ; b < TileRows; b++){
      currentPlayer = RED;
      if( (b < TileRows-2 && a < TileRows-2 && TempLayout1[a+2][b+2] == 0 && (TempLayout1[a][b] == 1 || TempLayout1[a][b] == 2) && (TempLayout1[a+1][b+1] == 3||TempLayout1[a+1][b+1] == 4))){
       for(int i = 0; i<100; i++){
        if(scoreFinal[i].jumpX == a+1 && scoreFinal[i].jumpY == b+1 && scoreFinal[i].jumpX != TileRows-1 && scoreFinal[i].jumpY != TileRows-1){
         scoreFinal[i].score =  -1;
        }
       }
       //System.out.println("part 1");
      }else if( (a > 1 && b < TileRows-2 && TempLayout1[a-2][b+2] == 0 && (TempLayout1[a][b] == 1 || TempLayout1[a][b] == 2) && (TempLayout1[a-1][b+1] == 3 || TempLayout1[a-1][b+1] == 4))){
       scoreEval[a-1][b+1] -= 1;
        for(int i = 0; i<100; i++){
         if(scoreFinal[i].jumpX == (a-1) && scoreFinal[i].jumpY == (b+1) && scoreFinal[i].jumpX != 0 && scoreFinal[i].jumpY != 7){
          scoreFinal[i].score = -1;
         }
        }
        //System.out.println("part 2");
      }else if( (a < TileRows-2 && b > 1 && TempLayout1[a+2][b-2] == 0 && (TempLayout1[a][b] == 1 || TempLayout1[a][b] == 2) && (TempLayout1[a+1][b-1] == 3 || TempLayout1[a+1][b-1] == 4))){
        scoreEval[a+1][b-1] -= 1;
         for(int i = 0; i<100; i++){
         if(scoreFinal[i].jumpX == (a+1) && scoreFinal[i].jumpY == (b-1) && scoreFinal[i].jumpX != 7 && scoreFinal[i].jumpY != 0){
          scoreFinal[i].score = -1;
         }
        }
         //System.out.println("part 3");
       }else if( (a > 1 && b > 1 && (TempLayout1[a][b] == 1 || TempLayout1[a][b] == 2) && (TempLayout1[a-1][b-1] == 3 || TempLayout1[a-1][b-1] == 4) && TempLayout1[a-2][b-2] == 0)){
        scoreEval[a-1][b-1] -= 1;
         for(int i = 0; i<100; i++){
         if(scoreFinal[i].jumpX == (a-1) && scoreFinal[i].jumpY == (b-1) && scoreFinal[i].jumpX != 0 && scoreFinal[i].jumpY != 0){
          scoreFinal[i].score = -1;
         }
         }
         //System.out.println("part 4");
      }
      
         
     }// end of b loop
    } //end of a loop
      
    currentPlayer = WHITE;
    //System.out.println("After Opponent for " + y + "," + z + " " + scoreEval[y][z]);
    //System.out.println();
   }
  }//end of y/z loop
  
  //System.out.println();
  //System.out.println("There were a total of " + testCounter + " moves");
  
  Point[] bestMove = new Point[1];
  bestMove[0] = scoreFinal[0];
  boolean canImove = false;
  //System.out.println(bestMove[0].score);
  for(int ah = 0; ah<100; ah++){
   //Checks if it is trying to jump a piece that is next to it
   if( (scoreFinal[ah].x < 7 && scoreFinal[ah].y < 7 && (scoreFinal[ah].x+1 == scoreFinal[ah].jumpX)&& (scoreFinal[ah].y+1 == scoreFinal[ah].jumpY) && (PieceLayout[scoreFinal[ah].x+1][scoreFinal[ah].y+1] == 0)) || 
       (scoreFinal[ah].x > 0 && scoreFinal[ah].y > 0 && (scoreFinal[ah].x-1 == scoreFinal[ah].jumpX)&& (scoreFinal[ah].y-1 == scoreFinal[ah].jumpY)&& (PieceLayout[scoreFinal[ah].x-1][scoreFinal[ah].y-1] == 0)) || 
          (scoreFinal[ah].x > 0 && scoreFinal[ah].y < 7 && (scoreFinal[ah].x-1 == scoreFinal[ah].jumpX)&& (scoreFinal[ah].y+1 == scoreFinal[ah].jumpY)&& (PieceLayout[scoreFinal[ah].x-1][scoreFinal[ah].y+1] == 0)) || 
          (scoreFinal[ah].x < 7 && scoreFinal[ah].y > 0 && (scoreFinal[ah].x+1 == scoreFinal[ah].jumpX)&& (scoreFinal[ah].y-1 == scoreFinal[ah].jumpY)&& (PieceLayout[scoreFinal[ah].x+1][scoreFinal[ah].y-1] == 0)) ||
          (scoreFinal[ah].x == 7 && (scoreFinal[ah].x-1 == scoreFinal[ah].jumpX) && (scoreFinal[ah].y+1 == scoreFinal[ah].jumpY) && (PieceLayout[scoreFinal[ah].x-1][scoreFinal[ah].y+1] == 0))
     ){
    canImove = true;
   }
   //checks if it is doing a legitimate jump
   if( ((scoreFinal[ah].jumpX - scoreFinal[ah].x == 2) && (scoreFinal[ah].jumpY - scoreFinal[ah].y == 2))||
    ((scoreFinal[ah].jumpX - scoreFinal[ah].x == -2) && (scoreFinal[ah].jumpY - scoreFinal[ah].y == 2))||
    ((scoreFinal[ah].jumpX - scoreFinal[ah].x == 2) && (scoreFinal[ah].jumpY - scoreFinal[ah].y == -2))||
    ((scoreFinal[ah].jumpX - scoreFinal[ah].x == -2) && (scoreFinal[ah].jumpY - scoreFinal[ah].y == -2))
     ){
    
     if( (PieceLayout[(scoreFinal[ah].x + scoreFinal[ah+1].jumpX)/2][(scoreFinal[ah].y + scoreFinal[ah].jumpY)/2] != 3) &&
         (PieceLayout[(scoreFinal[ah].x + scoreFinal[ah+1].jumpX)/2][(scoreFinal[ah].y + scoreFinal[ah].jumpY)/2] != 3)
       ){
      if((PieceLayout[(scoreFinal[ah].x + scoreFinal[ah+1].jumpX)/2][(scoreFinal[ah].y + scoreFinal[ah].jumpY)/2] != 0)){
       break;
      }
    canImove = true;
     }
   }
   //System.out.println("Values " + scoreFinal[ah].x + "," + scoreFinal[ah].y + " " + scoreFinal[ah].jumpX + "," + scoreFinal[ah].jumpY);
   
    if(bestMove[0].score <= scoreFinal[ah].score){
     if(canImove == true){
    // System.out.println("AH?");
     if( (scoreFinal[ah].jumpX - scoreFinal[ah].x > 2) || (scoreFinal[ah].jumpY - scoreFinal[ah].y > 2) ){
     }else{
     //System.out.println("AHHHHHHHHHHHHHHH");
     bestMove[0] = scoreFinal[ah];
     }
    }
  //System.out.println(scoreFinal[ah].x + "," + scoreFinal[ah].y + " jumps to " + scoreFinal[ah].jumpX + "," + scoreFinal[ah].jumpY + " Final score is " + scoreFinal[ah].score);
   }
  
  if((scoreFinal[ah+1].x == 0) && (scoreFinal[ah+1].y == 0)){
   //System.out.println("stop loop");
   break;
  }
  canImove = false;
  } //end of for loop
  if(bestMove[0].x == 0 && bestMove[0].y == 0){
   bestMove[0].score = 1;
  }
  System.out.println("The best move is " + bestMove[0].x + "," + bestMove[0].y + " jump to " + bestMove[0].jumpX + "," + bestMove[0].jumpY + " The score is " + bestMove[0].score);
  makeMove(bestMove[0].x,bestMove[0].y,bestMove[0].jumpX,bestMove[0].jumpY);
 } //End of function 
 

 //Makes move in the Temporary layout so possible moves may be evaluated
 public void tempMove(int col, int row, int storeCol, int storeRow){
  initialPiece = 0;
  if(currentPlayer == RED){
   initialPiece = TempLayout1[storeCol][storeRow];
  }
  if(currentPlayer == WHITE){
   initialPiece = TempLayout1[col][row];
  }
  int x = TempLayout1[storeCol][storeRow];
  TempLayout1[col][row] = x;
  
  if(currentPlayer == WHITE){
   TempLayout1[storeCol][storeRow] = initialPiece;
  }
  
  if(currentPlayer == RED){
   TempLayout1[storeCol][storeRow] = initialPiece;
  }

  TEMPcheckKingWhite(storeCol,storeRow);
  TEMPcheckKingRed(col,row);
  
  if(currentPlayer == RED){
   TempLayout1[storeCol][storeRow] = 0;
  }
  
  if(isJump == true){
   if(currentPlayer == WHITE){
    TEMPremovePieceWhite(col, row, storeCol, storeRow);
   }
   if(currentPlayer == RED){
    TEMPremovePieceRed(col,row,storeCol,storeRow);
   }
  }
  
  resetPlay();
 }
 
 //
 public void makeMove(int col, int row, int storeCol, int storeRow){
  initialPiece = 0;
  //System.out.println("Inputs " + col + "," + row + " to " + storeCol + "," + storeRow);
  System.out.println("//////////////////////////////////////////////////////////////////////////////////////////////////////////");
  if(currentPlayer == RED){
   initialPiece = PieceLayout[storeCol][storeRow];
  }
  if(currentPlayer == WHITE){
   initialPiece = PieceLayout[col][row];
  }
  int x = PieceLayout[storeCol][storeRow];
  PieceLayout[col][row] = x;
  
  if(currentPlayer == WHITE){
   PieceLayout[storeCol][storeRow] = initialPiece;
  }
  
  if(currentPlayer == RED){
   PieceLayout[storeCol][storeRow] = initialPiece;
  }

  checkKingWhite(storeCol,storeRow);
  checkKingRed(col,row);
  
  if(currentPlayer == RED){
   PieceLayout[storeCol][storeRow] = 0;
  }
  
  if(isJump == true && ((col+2 == storeCol && row+2 == storeRow) || (col+2 == storeCol && row-2 == storeRow) || (col-2 == storeCol && row+2 == storeRow) || (col-2 == storeCol && row-2 == storeRow))){
   if(currentPlayer == WHITE){
    removePieceWhite(col,row,storeCol,storeRow);
   }
   if(currentPlayer == RED){
    removePieceRed(storeCol,storeRow,col,row);
   }
  }
  
  resetPlay();
  swapPlayer();
 }
 
 public boolean isKing(int col, int row){
  if(PieceLayout[col][row] == RED_KING || PieceLayout[col][row] == WHITE_KING){
   return true;
  }
  else return false;
 }
 
 public int checkOpponent(int col, int row){
  if(PieceLayout[col][row] == RED || PieceLayout[col][row] ==  RED_KING)
   return WHITE;
  else
   return RED;
 }
 
 public void checkExtraJumps(int col, int row){
  int opponent = checkOpponent(col, row);
  int opponentKing = checkOpponent(col, row) + 1;
  if(PieceLayout[col-1][row-1] == opponent || PieceLayout[col-1][row-1] == opponentKing){
   availablePlays[col-1][row-1] = 1;
  }
  else if(PieceLayout[col+1][row-1] == opponent || PieceLayout[col+1][row-1] == opponentKing){
   availablePlays[col+1][row-1] = 1;
  }
  else if(PieceLayout[col-1][row+1] == opponent || PieceLayout[col-1][row+1] == opponentKing){
   availablePlays[col-1][row+1] = 1;
  }
  else if(PieceLayout[col+1][row+1] == opponent || PieceLayout[col+1][row+1] == opponentKing){
   availablePlays[col+1][row+1] = 1;
  }
  repaint();
 }
 
 public void checkKingWhite(int col, int row){
  //System.out.println("Check King WHITE " + col + " " + row);
  if(PieceLayout[col][row] == WHITE && row == TileRows-1){
   PieceLayout[col][row] = WHITE_KING;
  }
  else return;
 }
 public void checkKingRed(int col, int row){
  //System.out.println("Check king RED " + col + " " + row);
  if(PieceLayout[col][row] == RED && row == 0){
   PieceLayout[col][row] = RED_KING;
  }
 }
 public void TEMPcheckKingWhite(int col, int row){
  //System.out.println("Check King WHITE " + col + " " + row);
  if(TempLayout1[col][row] == WHITE && row == TileRows-1){
   TempLayout1[col][row] = WHITE_KING;
  }
  else return;
 }
 public void TEMPcheckKingRed(int col, int row){
  //System.out.println("Check king RED " + col + " " + row);
  if(TempLayout1[col][row] == RED && row == 0){
   TempLayout1[col][row] = RED_KING;
  }
 }
 public void removePieceWhite(int col, int row, int storeCol, int storeRow){ //might be a better way to do this, but detects position of opponent piece based on destination and original position
  System.out.println("Piece Removal for White");
  int pieceRow = -1; 
  int pieceCol = -1;
  if(col > storeCol && row > storeRow){ //up left jump
   pieceRow = storeRow+1;
   pieceCol = storeCol+1;
   System.out.println("PART 1 " + col + " JUMP " + row);
   System.out.println("TO");
   System.out.println(storeCol + " JUMP " + storeRow);
   PieceLayout[pieceCol][pieceRow] = 0;
  }
  if(col > storeCol && row < storeRow){ //down left jump
   pieceRow = storeRow-1;
   pieceCol = storeCol+1;
   System.out.println("Part 2 " + col + " JUMP " + row);
   System.out.println("TO");
   System.out.println(storeCol + " JUMP " + storeRow);
   PieceLayout[pieceCol][pieceRow] = 0;
  }
  if(col < storeCol && row > storeRow){ //up right jump
   pieceRow = storeRow+1;
   pieceCol = storeCol-1;
   System.out.println("Part 3 " + col + " JUMP " + row);
   System.out.println("TO");
   System.out.println(storeCol + " JUMP " + storeRow);
   PieceLayout[pieceCol][pieceRow] = 0;
  }
  if(col < storeCol && row < storeRow){ //down right jump
   pieceRow = storeRow-1;
   pieceCol = storeCol-1;
   System.out.println("Part 4 " + col + " JUMP " + row);
   System.out.println("TO");
   System.out.println(storeCol + " JUMP " + storeRow);
   PieceLayout[pieceCol][pieceRow] = 0;
  }

 }
 
 public void removePieceRed(int col, int row, int storeCol, int storeRow){ //might be a better way to do this, but detects position of opponent piece based on destination and original position
  System.out.println("Piece Removal for RED");
  int pieceRow = -1; 
  int pieceCol = -1;
  //System.out.println(storeRow + " " + storeCol);
  if(col > storeCol && row > storeRow){ //down right jump
   pieceRow = storeRow+1;
   pieceCol = storeCol+1;
   //System.out.println("PART 1 " + col + " JUMP " + row);
   //System.out.println("TO");
   //System.out.println(storeCol + " JUMP " + storeRow);
   PieceLayout[pieceCol][pieceRow] = 0;
  }
  if(col > storeCol && row < storeRow){ //up right jump
   pieceRow = storeRow-1;
   pieceCol = storeCol+1;
   //System.out.println("Part 2 " + col + " JUMP " + row);
   //System.out.println("TO");
   //System.out.println(storeCol + " JUMP " + storeRow);
   PieceLayout[pieceCol][pieceRow] = 0;
  }
  if(col < storeCol && row > storeRow){ //down left jump
   pieceRow = storeRow+1;
   pieceCol = storeCol-1;
   //System.out.println("Part 3 " + col + " JUMP " + row);
   //System.out.println("TO");
   //System.out.println(storeCol + " JUMP " + storeRow);
   PieceLayout[pieceCol][pieceRow] = 0;
  }
  if(col < storeCol && row < storeRow){ //up left
   pieceRow = storeRow-1;
   pieceCol = storeCol-1;
   //System.out.println("Part 4 " + col + " JUMP " + row);
   //System.out.println("TO");
   //System.out.println(storeCol + " JUMP " + storeRow);
   PieceLayout[pieceCol][pieceRow] = 0;
  }

 }
 public void TEMPremovePieceWhite(int col, int row, int storeCol, int storeRow){ //might be a better way to do this, but detects position of opponent piece based on destination and original position
  //System.out.println("TEMP Piece Removal for White");
  int pieceRow = -1; 
  int pieceCol = -1;
  if(col > storeCol && row > storeRow){ //up left jump
   pieceRow = storeRow+1;
   pieceCol = storeCol+1;
   TempLayout1[pieceCol][pieceRow] = 0;
  }
  if(col > storeCol && row < storeRow){ //down left jump
   pieceRow = storeRow-1;
   pieceCol = storeCol+1;
   TempLayout1[pieceCol][pieceRow] = 0;
  }
  if(col < storeCol && row > storeRow){ //up right jump
   pieceRow = storeRow+1;
   pieceCol = storeCol-1;
   TempLayout1[pieceCol][pieceRow] = 0;
  }
  if(col < storeCol && row < storeRow){ //down right jump
   pieceRow = storeRow-1;
   pieceCol = storeCol-1;
   TempLayout1[pieceCol][pieceRow] = 0;
  }

 }
 
 public void TEMPremovePieceRed(int col, int row, int storeCol, int storeRow){ //might be a better way to do this, but detects position of opponent piece based on destination and original position
  //System.out.println("TEMP Piece Removal for RED");
  int pieceRow = -1; 
  int pieceCol = -1;
  if(col > storeCol && row > storeRow){ //down right jump
   pieceRow = storeRow+1;
   pieceCol = storeCol+1;
   TempLayout1[pieceCol][pieceRow] = 0;
  }
  if(col > storeCol && row < storeRow){ //up right jump
   pieceRow = storeRow-1;
   pieceCol = storeCol+1;
   TempLayout1[pieceCol][pieceRow] = 0;
  }
  if(col < storeCol && row > storeRow){ //down left jump
   pieceRow = storeRow+1;
   pieceCol = storeCol-1;
   TempLayout1[pieceCol][pieceRow] = 0;
  }
  if(col < storeCol && row < storeRow){ //up left
   pieceRow = storeRow-1;
   pieceCol = storeCol-1;
   TempLayout1[pieceCol][pieceRow] = 0;
  }

 }
 
 //shows moves when a piece is clicked
 public void getAvailablePlays(int col, int row){
  inPlay = true;
  if((checkTeamPiece(col, row) == true)){
   if(PieceLayout[col][row] == RED){  
    getUp(col, row);
   }
   if(PieceLayout[col][row] == WHITE){ 
    getDown(col, row);
   }
   if(PieceLayout[col][row] == RED_KING || PieceLayout[col][row] == WHITE_KING){ 
    getUp(col, row);
    getDown(col, row);
   }
  repaint();
  }
 }
 
 public void TEMPgetAvailablePlays(int col, int row){
  inPlay = true;
  if((checkTeamPiece(col, row) == true)){
   if(TempLayout1[col][row] == RED){  
    TEMPgetUp(col, row);
   }
   if(TempLayout1[col][row] == WHITE){ 
    TEMPgetDown(col, row);
   }
   if(TempLayout1[col][row] == RED_KING || TempLayout1[col][row] == WHITE_KING){ 
    TEMPgetUp(col, row);
    TEMPgetDown(col, row);
   }
  repaint();
  }
 }
 
 //Checks the upward movement
 public void getUp(int col, int row){ 
  int rowUp = row-1;
  if(col == 0 && row != 0){ 
   for(int i = col; i < col+2; i++){ 
    if(PieceLayout[col][row] != 0 && PieceLayout[i][rowUp] != 0){
     if(canJump(col, row, i, rowUp) == true){
      jumpCol = getJumpPos(col, row, i, rowUp)[0];
      jumpRow = getJumpPos(col, row, i, rowUp)[1];
      availablePlays[jumpCol][jumpRow] = 1;
      choice7 = 1;
      if(currentPlayer == WHITE || currentPlayer == WHITE_KING){
       evalPieces[col][row] += 1;
       }
       if(currentPlayer == RED || currentPlayer == RED_KING){
        evalOpponent[col][row] += 1;;
       }
     }
    }
    else if(BoardLayout[i][rowUp] == 1 && PieceLayout[i][rowUp] == 0){
     availablePlays[i][rowUp] = 1;
     jumpCol = i;
     jumpRow = rowUp;
     choice8 = 1;
     if(currentPlayer == WHITE || currentPlayer == WHITE_KING){
      evalPieces[col][row] += 1;
      }
      if(currentPlayer == RED || currentPlayer == RED_KING){
       evalOpponent[col][row] += 1;;
      }
    }
    }
   }
  else if(col == (TileRows - 1) && row != 0){
    if(PieceLayout[col][row] != 0 && PieceLayout[col-1][rowUp] != 0){
     if(canJump(col, row, col-1, rowUp) == true){
      jumpCol = getJumpPos(col, row, col-1, rowUp)[0];
      jumpRow = getJumpPos(col, row, col-1, rowUp)[1];
      availablePlays[jumpCol][jumpRow] = 1;
      choice9 = 1;
      if(currentPlayer == WHITE || currentPlayer == WHITE_KING){
       evalPieces[col][row] += 1;
       }
       if(currentPlayer == RED || currentPlayer == RED_KING){
        evalOpponent[col][row] += 1;;
       }
      
     }
    }
    else if(BoardLayout[col-1][rowUp] == 1 && PieceLayout[col-1][rowUp] == 0){
     availablePlays[col-1][rowUp] = 1;
     jumpCol = col-1;
     jumpRow = rowUp;
     choice10 = 1;
     if(currentPlayer == WHITE || currentPlayer == WHITE_KING){
      evalPieces[col][row] += 1;
      }
      if(currentPlayer == RED || currentPlayer == RED_KING){
       evalOpponent[col][row] += 1;;
      }
     
    }
  }
  else if(col != TileRows - 1 && col != 0 && row != 0){
   for(int i = col-1; i <= col+1; i++){
    if(PieceLayout[col][row] != 0 && PieceLayout[i][rowUp] != 0){
     if(canJump(col, row, i, rowUp) == true){
      jumpCol = getJumpPos(col, row, i, rowUp)[0];
      jumpRow = getJumpPos(col, row, i, rowUp)[1];
      availablePlays[jumpCol][jumpRow] = 1;
      choice11 = 1;
      if(currentPlayer == WHITE || currentPlayer == WHITE_KING){
       evalPieces[col][row] += 1;
      }
       if(currentPlayer == RED || currentPlayer == RED_KING){
        evalOpponent[col][row] += 1;;
       }
       
      
     }
    }
    else if(BoardLayout[i][rowUp] == 1 && PieceLayout[i][rowUp] == 0){
     availablePlays[i][rowUp] = 1;
     jumpCol = i;
     jumpRow = rowUp;
     choice12 = 1;
     if(currentPlayer == WHITE || currentPlayer == WHITE_KING){
      evalPieces[col][row] += 1;
      }
      if(currentPlayer == RED || currentPlayer == RED_KING){
       evalOpponent[col][row] += 1;;
      }
    }
   }
  }
 }

 //checks possible downward moves
 public void getDown(int col, int row){
  int rowDown = row+1;
  if(col == 0 && row != TileRows-1){
    if(PieceLayout[col][row] != 0 && PieceLayout[col+1][rowDown] != 0){ //checks down right col for jump
     if(canJump(col, row, col+1, rowDown) == true){
      jumpCol = getJumpPos(col, row, col+1, rowDown)[0];
      jumpRow = getJumpPos(col, row, col+1, rowDown)[1]; 
      availablePlays[jumpCol][jumpRow] = 1;
      choice1 = 1;
      
      if(currentPlayer == WHITE){
      evalPieces[col][row] += 1;
      }
      if(currentPlayer == RED){
       evalOpponent[col][row] += 1;;
      }
      
     }
    }
    else if(BoardLayout[col+1][rowDown] == 1 && PieceLayout[col+1][rowDown] == 0){ //move if empty space right
     availablePlays[col+1][rowDown] = 1;
     jumpCol = col+1;
     jumpRow = rowDown;
     choice2 = 1;
     //System.out.println("c2");
     if(currentPlayer == WHITE){
      evalPieces[col][row] += 1;
      }
      if(currentPlayer == RED){
       evalOpponent[col][row] += 1;;
      }
    }
  }
  else if(col == TileRows - 1 && row != TileRows-1){
    if(PieceLayout[col][row] != 0 && PieceLayout[col-1][rowDown] != 0){ //checks down left col for jump
     if(canJump(col, row, col-1, rowDown) == true){
      jumpCol = getJumpPos(col, row, col-1, rowDown)[0];
      jumpRow = getJumpPos(col, row, col-1, rowDown)[1]; 
      availablePlays[jumpCol][jumpRow] = 1;
      choice3 = 1;
      ColTo = jumpCol;
      RowTo = jumpRow;
      //System.out.println("c3");
      if(currentPlayer == WHITE){
       evalPieces[col][row] += 1;
       }
       if(currentPlayer == RED){
        evalOpponent[col][row] += 1;;
       }
     }
    }
    else if(BoardLayout[col-1][rowDown] == 1 && PieceLayout[col-1][rowDown] == 0){ //move if empty space left
     availablePlays[col-1][rowDown] = 1;
     jumpCol = col-1;
     jumpRow = rowDown;
     choice4 = 1;
     if(currentPlayer == WHITE){
      evalPieces[col][row] += 1;
      }
      if(currentPlayer == RED){
       evalOpponent[col][row] += 1;;
      }
     //System.out.println("c4");
    }
  }
  else if(col != TileRows-1 && col != 0 && row != TileRows-1){
   for(int i = col-1; i <= col+1; i++){
    if(PieceLayout[col][row] != 0 && PieceLayout[i][rowDown] != 0){
     if(canJump(col, row, i, rowDown) == true){
      jumpCol = getJumpPos(col, row, i, rowDown)[0];
      jumpRow = getJumpPos(col, row, i, rowDown)[1];
      availablePlays[jumpCol][jumpRow] = 1;
      choice5 = 1;
      if(currentPlayer == WHITE){
       evalPieces[col][row] += 1;
       }
       if(currentPlayer == RED){
        evalOpponent[col][row] += 1;;
       }
      ColTo = jumpCol;
      RowTo = jumpRow;
      //System.out.println("c5");
     }
    }
    else if(BoardLayout[i][rowDown] == 1 && PieceLayout[i][rowDown] == 0){
     availablePlays[i][rowDown] = 1;
     jumpCol = i;
     jumpRow = rowDown;
     choice6 = 1;
     if(currentPlayer == WHITE){
      evalPieces[col][row] += 1;
      }
      if(currentPlayer == RED){
       evalOpponent[col][row] += 1;;
      }
     //System.out.println("c6");
    }
   }
  }
 }
 
 //Checks the upward movement
  public void TEMPgetUp(int col, int row){ 
   int rowUp = row-1;
   if(col == 0 && row != 0){ 
    for(int i = col; i < col+2; i++){ 
     if(TempLayout1[col][row] != 0 && TempLayout1[i][rowUp] != 0){
      if(canJump(col, row, i, rowUp) == true){
       jumpCol = getJumpPos(col, row, i, rowUp)[0];
       jumpRow = getJumpPos(col, row, i, rowUp)[1];
       availablePlays[jumpCol][jumpRow] = 1;
       choice7 = 1;
       if(currentPlayer == WHITE || currentPlayer == WHITE_KING){
        evalPieces[col][row] += 1;
        }
        if(currentPlayer == RED || currentPlayer == RED_KING){
         evalOpponent[col][row] += 1;
        }
      }
     }
     else if(BoardLayout[i][rowUp] == 1 && TempLayout1[i][rowUp] == 0){
      availablePlays[i][rowUp] = 1;
      jumpCol = i;
      jumpRow = rowUp;
      choice8 = 1;
      if(currentPlayer == WHITE || currentPlayer == WHITE_KING){
       evalPieces[col][row] += 1;
       }
       if(currentPlayer == RED || currentPlayer == RED_KING){
        evalOpponent[col][row] += 1;
       }
     }
     }
    }
   else if(col == (TileRows - 1) && row != 0){
     if(TempLayout1[col][row] != 0 && TempLayout1[col-1][rowUp] != 0){
      if(canJump(col, row, col-1, rowUp) == true){
       jumpCol = getJumpPos(col, row, col-1, rowUp)[0];
       jumpRow = getJumpPos(col, row, col-1, rowUp)[1];
       availablePlays[jumpCol][jumpRow] = 1;
       choice9 = 1;
       if(currentPlayer == WHITE || currentPlayer == WHITE_KING){
        evalPieces[col][row] += 1;
        }
        if(currentPlayer == RED || currentPlayer == RED_KING){
         evalOpponent[col][row] += 1;
        }
       
      }
     }
     else if(BoardLayout[col-1][rowUp] == 1 && TempLayout1[col-1][rowUp] == 0){
      availablePlays[col-1][rowUp] = 1;
      jumpCol = col-1;
      jumpRow = rowUp;
      choice10 = 1;
      if(currentPlayer == WHITE || currentPlayer == WHITE_KING){
       evalPieces[col][row] += 1;
       }
       if(currentPlayer == RED || currentPlayer == RED_KING){
        evalOpponent[col][row] += 1;
       }
      
     }
   }
   else if(col != TileRows - 1 && col != 0 && row != 0){
    for(int i = col-1; i <= col+1; i++){
     if(TempLayout1[col][row] != 0 && TempLayout1[i][rowUp] != 0){
      if(canJump(col, row, i, rowUp) == true){
       jumpCol = getJumpPos(col, row, i, rowUp)[0];
       jumpRow = getJumpPos(col, row, i, rowUp)[1];
       availablePlays[jumpCol][jumpRow] = 1;
       choice11 = 1;
       if(currentPlayer == WHITE || currentPlayer == WHITE_KING){
        evalPieces[col][row] += 1;
       }
        if(currentPlayer == RED || currentPlayer == RED_KING){
         evalOpponent[col][row] += 1;
        }
        
       
      }
     }
     else if(BoardLayout[i][rowUp] == 1 && TempLayout1[i][rowUp] == 0){
      availablePlays[i][rowUp] = 1;
      jumpCol = i;
      jumpRow = rowUp;
      choice12 = 1;
      if(currentPlayer == WHITE || currentPlayer == WHITE_KING){
       evalPieces[col][row] += 1;
       }
       if(currentPlayer == RED || currentPlayer == RED_KING){
        evalOpponent[col][row] += 1;
       }
     }
    }
   }
  }

  //checks possible downward moves
  public void TEMPgetDown(int col, int row){
   int rowDown = row+1;
   if(col == 0 && row != TileRows-1){
     if(TempLayout1[col][row] != 0 && TempLayout1[col+1][rowDown] != 0){ //checks down right col for jump
      if(canJump(col, row, col+1, rowDown) == true){
       jumpCol = getJumpPos(col, row, col+1, rowDown)[0];
       jumpRow = getJumpPos(col, row, col+1, rowDown)[1]; 
       availablePlays[jumpCol][jumpRow] = 1;
       choice1 = 1;
       
       if(currentPlayer == WHITE){
       evalPieces[col][row] += 1;
       }
       if(currentPlayer == RED){
        evalOpponent[col][row] += 1;
       }
       
      }
     }
     else if(BoardLayout[col+1][rowDown] == 1 && TempLayout1[col+1][rowDown] == 0){ //move if empty space right
      availablePlays[col+1][rowDown] = 1;
      jumpCol = col+1;
      jumpRow = rowDown;
      choice2 = 1;
      //System.out.println("c2");
      if(currentPlayer == WHITE){
       evalPieces[col][row] += 1;
       }
       if(currentPlayer == RED){
        evalOpponent[col][row] += 1;
       }
     }
   }
   else if(col == TileRows - 1 && row != TileRows-1){
     if(TempLayout1[col][row] != 0 && TempLayout1[col-1][rowDown] != 0){ //checks down left col for jump
      if(canJump(col, row, col-1, rowDown) == true){
       jumpCol = getJumpPos(col, row, col-1, rowDown)[0];
       jumpRow = getJumpPos(col, row, col-1, rowDown)[1]; 
       availablePlays[jumpCol][jumpRow] = 1;
       choice3 = 1;
       ColTo = jumpCol;
       RowTo = jumpRow;
       //System.out.println("c3");
       if(currentPlayer == WHITE){
        evalPieces[col][row] += 1;
        }
        if(currentPlayer == RED){
         evalOpponent[col][row] += 1;
        }
      }
     }
     else if(BoardLayout[col-1][rowDown] == 1 && TempLayout1[col-1][rowDown] == 0){ //move if empty space left
      availablePlays[col-1][rowDown] = 1;
      jumpCol = col-1;
      jumpRow = rowDown;
      choice4 = 1;
      if(currentPlayer == WHITE){
       evalPieces[col][row] += 1;
       }
       if(currentPlayer == RED){
        evalOpponent[col][row] += 1;;
       }
      //System.out.println("c4");
     }
   }
   else if(col != TileRows-1 && col != 0 && row != TileRows-1){
    for(int i = col-1; i <= col+1; i++){
     if(TempLayout1[col][row] != 0 && TempLayout1[i][rowDown] != 0){
      if(canJump(col, row, i, rowDown) == true){
       jumpCol = getJumpPos(col, row, i, rowDown)[0];
       jumpRow = getJumpPos(col, row, i, rowDown)[1];
       availablePlays[jumpCol][jumpRow] = 1;
       choice5 = 1;
       if(currentPlayer == WHITE){
        evalPieces[col][row] += 1;
        }
        if(currentPlayer == RED){
         evalOpponent[col][row] += 1;;
        }
       ColTo = jumpCol;
       RowTo = jumpRow;
       //System.out.println("c5");
      }
     }
     else if(BoardLayout[i][rowDown] == 1 && TempLayout1[i][rowDown] == 0){
      availablePlays[i][rowDown] = 1;
      jumpCol = i;
      jumpRow = rowDown;
      choice6 = 1;
      if(currentPlayer == WHITE){
       evalPieces[col][row] += 1;
       }
       if(currentPlayer == RED){
        evalOpponent[col][row] += 1;;
       }
      //System.out.println("c6");
     }
    }
   }
  }
 //checks player
 public boolean checkTeamPiece(int col, int row){
  if(currentPlayer == RED && (PieceLayout[col][row] == RED || PieceLayout[col][row] == RED_KING)) //bottom
   return true;
  if(currentPlayer == WHITE && (PieceLayout[col][row] == WHITE || PieceLayout[col][row] == WHITE_KING)) //top
   return true;
  else
   return false;
 }
 
 public boolean isLegalPos(int col, int row){
  if(row < 0 || row >= TileRows || col < 0 || col >= TileRows)
   return false;
  else return true;
 }
 
 public boolean canJump(int col, int row, int opponentCol, int opponentRow){
  if(((PieceLayout[col][row] == WHITE || PieceLayout[col][row] == WHITE_KING) && (PieceLayout[opponentCol][opponentRow] == RED || PieceLayout[opponentCol][opponentRow] == RED_KING)) || (PieceLayout[col][row] == RED || PieceLayout[col][row] == RED_KING) && (PieceLayout[opponentCol][opponentRow] == WHITE || PieceLayout[opponentCol][opponentRow] == WHITE_KING)){ 
   if(opponentCol == 0 || opponentCol == TileRows-1 || opponentRow == 0 || opponentRow == TileRows-1)
    return false;
   int[] toData = getJumpPos(col, row, opponentCol, opponentRow);
      if(isLegalPos(toData[0],toData[1]) == false)
          return false;
      if(PieceLayout[toData[0]][toData[1]] == 0){
       isJump = true;
       
       return true;
      }
  }
  return false;
 }
 
 public int[] getJumpPos(int col, int row, int opponentCol, int opponentRow){
  if(col > opponentCol && row > opponentRow && PieceLayout[col-2][row-2] == 0)
   return new int[] {col-2, row-2};
  else if(col > opponentCol && row < opponentRow && PieceLayout[col-2][row+2] == 0)
   return new int[] {col-2, row+2};
  else if(col < opponentCol && row > opponentRow && PieceLayout[col+2][row-2] == 0)
   return new int[] {col+2, row-2};
  else
   return new int[] {col+2, row+2};
 }
 
 public static void main(String[] args){
  try {
   crownImage = ImageIO.read(new File("Crown.png"));
  } catch (IOException e) {
   e.printStackTrace();
  }
  new Checkers();
 }
 
 public void mouseClicked(MouseEvent e) {}
 public void mouseReleased(MouseEvent e) {}
 public void mouseEntered(MouseEvent e) {}
 public void mouseExited(MouseEvent e) {}
 public void actionPerformed(ActionEvent e) {}
}
