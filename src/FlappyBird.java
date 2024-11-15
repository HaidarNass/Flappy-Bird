import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import java.util.ArrayList;


public class FlappyBird extends JPanel implements ActionListener, KeyListener{

    int boardWidth = 360;
    int boardHeight = 640;

    //IMAGES
    Image topPipeImg;
    Image bottomPipeImg;
    Image birdImg;
    Image backgroundImg;


    //Bird
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 32;
    int birdHeight = 24;

    class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;

        Image img;
        Bird(Image img){
            this.img = img;
        }
    }

    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeheight = 512;

    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeheight;

        Image img;
        boolean passed = false;
        Pipe(Image img){
            this.img = img;
        }
    }

    //Game Logic
    Bird bird;
    double gravity = 1;
    int velocityX = -4; // this is for the movement of pipes
    int velocityY = 0; // this is for the movement of bird

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver;

    ArrayList<Pipe> pipes;
    Random random = new Random();
    double score = 0;

    FlappyBird(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        //setBackground(Color.BLUE);

        setFocusable(true);
        addKeyListener(this);

        //Load Images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //bird
        bird = new Bird(birdImg);

        //pipes
        pipes = new ArrayList<Pipe>();
        //place pipes timer
        placePipesTimer = new Timer(1800, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                placePipes();
            }

        });
        placePipesTimer.start();

        //game timer
        gameLoop = new Timer(1000/45, this);
        gameLoop.start();

    }

    public void placePipes(){
        int randomPipeY = (int)(pipeY - pipeheight/4 - Math.random()*(pipeheight/2));
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y=randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y +pipeheight+ openingSpace;
        pipes.add(bottomPipe); 
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);

    }

    public void move(){
        velocityY += gravity;

        //bird
        bird.y += velocityY;
        bird.y = Math.max(bird.y,0);

        //pipes
        for(int i = 0; i<pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;
            if(!pipe.passed && bird.x>pipe.x+pipe.width){
                pipe.passed = true;
                score +=0.5;
            }

            if(collision(bird, pipe)){
                gameOver = true;
            }
        }
        if(bird.y>boardHeight){
            gameOver = true;
        }
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
               a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
               a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
    }
    
    public void draw(Graphics g){
        //background
        g.drawImage(backgroundImg, 0, 0,boardWidth,boardHeight,null);

        //bird
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);

        //pipes
        for(int i=0;i<pipes.size();i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if(gameOver){
            g.drawString("GameOver "+String.valueOf((int)(score)), 10, 35);
        }
        else{
            g.drawString(String.valueOf((int)(score)), 10, 35);
        }
        
        }
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            velocityY = -9;
            if(gameOver){
                bird.y = birdY;
                velocityY=0;
                pipes.clear();
                gameLoop.start();
                placePipesTimer.start();
                gameOver = false;
                score = 0;
            }
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
    
}
