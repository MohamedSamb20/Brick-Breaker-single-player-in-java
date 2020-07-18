import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.imageio.*;
import java.awt.image.*;
import java.io.*;


public class BrickBoard  extends JPanel implements Runnable, MouseListener
{
boolean ingame = false;//you can set to false to control start of game
private Dimension d;
int BOARD_WIDTH=500;
int BOARD_HEIGHT=500;
int x = 0;
BufferedImage img;
String message = "Click mouse to start game";
 private Thread animator;
int ballx = 250;//starting x value for ball
int bally = 430;//starting y value for ball
int xchange = 3;//change in x value per time frame
int ychange = -1;//change in y value per time frame
Brick[] bricks = new Brick[50];
boolean[] showBrick = new boolean[50];
int paddlex = 250;//starting paddle x coordinate
int paddley = 450;//starting paddle y coordinate
boolean paddleLeft = false;//variable to move paddle left
boolean paddleRight = false;//variable to move paddle right
boolean win = false;//tracks if you win the game
boolean play = false;//tracks if a game has already been played
int lives = 3;//three lives
String score = "";
int scores= 0;
 
    public BrickBoard()
    {
        addKeyListener(new TAdapter());
        addMouseListener(this);
        setFocusable(true);
        d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
        setBackground(Color.black);
        if (animator == null || !ingame)
        {
            animator = new Thread(this);
            animator.start();
        }
        int rowpos = 50;
        int colpos = 50;
        int count = 1;
        for(int i=0; i<bricks.length; i++)
        {
            bricks[i] = new Brick(rowpos, colpos, 30, 30);//creates 50 bricks
            showBrick[i] = true;//makes the bricks appear on the screen
            rowpos += 40;
            if(count%10 == 0)
            {
                rowpos = 50;
                colpos += 40;
            }
            count++;
        }
  
        setDoubleBuffered(true);
    }
    
    public void paint(Graphics g)
    {
        super.paint(g);
        
        g.setColor(Color.white);
        g.fillRect(0, 0, d.width, d.height);
        //g.fillOval(x,y,r,r);
       
       Font small = new Font("Helvetica", Font.BOLD, 14);
       FontMetrics metr = this.getFontMetrics(small);
       g.setColor(Color.black);
       g.setFont(small);
       g.drawString(message, 10, d.height-60);//sets up the message
       g.drawString("score:"+score, 400, 20);//draws the players score
       for(int i=0; i<bricks.length; i++)
        {
            if(showBrick[i])
            {
        g.setColor(Color.yellow);
        g.fillRect(bricks[i].x, bricks[i].y, 30, 30);//makes 50 multicolored blocks
            }
        }
       g.setColor(Color.green);
       g.fillRect(paddlex, paddley, 100, 50);//makes the paddle
       g.setColor(Color.blue);
       g.fillOval(ballx, bally, 20, 20);//makes the ball
       if(ingame) 
       {//active board appears in these brackets.
           bounceBall();
           if(paddleRight)
            {
                paddlex +=5;//moves paddle 5 units to the right
            }
            if(paddleLeft)
            {
                paddlex -= 5;//moves the paddle 5 units left
            }
    
        
       
        
            // g.drawImage(img,0,0,200,200 ,null);//if you want to display an image
     
    
   
       }
       
	   if(win && ingame == false && play)
	   {
	       small = new Font("Helvetica", Font.BOLD, 35);
	       metr = this.getFontMetrics(small);
	       g.setColor(Color.black);
	       g.setFont(small);
	       g.drawString("WINNER", 200, d.height-200);//message to say winner
	   }
	   else if(win == false && ingame == false && play)
	   {
	       small = new Font("Helvetica", Font.BOLD, 35);//
	       metr = this.getFontMetrics(small);
	       g.setColor(Color.black);
	       g.setFont(small);
	       g.drawString("LOSER", 200, d.height-200);//message to say loser
	   }
       Toolkit.getDefaultToolkit().sync();
       g.dispose();
}
public void bounceBall()
{
    if(ballx <= 0 || ballx >= BOARD_HEIGHT)
    {
        xchange = -1*xchange;//change in slope if it hits the sides
    }
    if(bally <= 0)
    {
        ychange = -1*ychange;//changes in slope it it hits the top
    }
    if(ballx>= paddlex && ballx<= paddlex+100 && bally>= paddley && bally<=paddley+50)
    {
        ychange = -1*ychange;//change in slope if it hits the paddle
    }
    if(bally >= 500)//if the ball hits the floor
    {
        lives--;//takes away a life
        scores -= 500;//takes aways 500 every time you lose a life
        if(lives == 0)
        {
        	win = false;//didnt win
        	ingame = false;//the game isn't on

        }
        else
        {
        	
        	ballx = 250;//moves ball to original spot
        	bally = 430;//moves ball to original spot
        	xchange += 2;//makes speed faster
        	ychange += -2;//makes speed faster
        	paddlex = 250;//moves paddle to original spot
        	paddley = 450;//moves paddle to original spot
        	paddleLeft = false;
        	paddleRight = false;
        	play = true;//game has ended
        	ingame = false;//the game isn't on
        }
        
    }
    for(int i=0; i<bricks.length; i++)
        {
            if(showBrick[i]== true && ballx + 10 > bricks[i].x && ballx < bricks[i].x + bricks[i].w && bally + 10 > bricks[i].y && bally < bricks[i].y + bricks[i].h)
            {
            	xchange = -1*xchange;//changes slope if it hits a brick
            	showBrick[i] = false;//makes the brick disappear
            	scores+= 50;//adds to the score when a block is it
            }
        }
    for(int i=0; i<bricks.length; i++)
    {
        if(i == bricks.length -1)
        {
            if(showBrick[i]==false)
            {
                ingame = false;//the game isn't on
                win = true;//did win
                play = true;//have played the game
            }
            else
            {
                i = bricks.length;
            }
        }
        if(showBrick[i]== true)
        {
            i = bricks.length;
        }
    }
        
    ballx += xchange;//changes position of ball
    bally += ychange;//changes position of ball
    score = Integer.toString(scores);
}

private class TAdapter extends KeyAdapter 
{

    public void keyReleased(KeyEvent e) 
    {
     int key = e.getKeyCode();
     paddleRight = false;//turns off right movement
     paddleLeft = false;//turns off left movement
     
    }

    public void keyPressed(KeyEvent e) 
    {
        //System.out.println( e.getKeyCode());
        // message = "Key Pressed: " + e.getKeyCode();
        int key = e.getKeyCode();
   
        if(key==39)
        {
            paddleRight = true;
        }
        if(key==37)
        {
            paddleLeft = true;
        }
        
       

    }

}




    public void mousePressed(MouseEvent e) 
    {
     int x = e.getX();
     int y = e.getY();
     
     message = "Game has started" + "(lives: " + lives + ")";//message that will be displayed
     ingame = true;//starts game


    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mouseClicked(MouseEvent e) 
    {   
    
    }

    public void run() 
    {

        long beforeTime, timeDiff, sleep;

        beforeTime = System.currentTimeMillis();
        int animationDelay = 10;//control FPS of board
        long time = 
        System.currentTimeMillis();
        while (true) 
        {//infinite loop
            // spriteManager.update();
            repaint();
            try 
            {
              time += animationDelay;
              Thread.sleep(Math.max(0,time - 
              System.currentTimeMillis()));
            }catch (InterruptedException e) 
            {
              System.out.println(e);
            }//end catch
        }//end while loop

    


    }//end of run

}//end of class
