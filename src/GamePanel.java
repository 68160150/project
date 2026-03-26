import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;


public class GamePanel extends JPanel implements ActionListener, KeyListener {

    Timer timer;

    Player player;

    boolean left,right,up,down,shoot;

    int score = 0;
    int highScore = 0;

    boolean gameStarted=false;
    boolean paused=false;
    boolean gameOver=false;

    Random rand = new Random();

    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Enemy> enemies = new ArrayList<>();

    JButton startButton;

    int[] starX = new int[80];
    int[] starY = new int[80];

    int shootCooldown = 0;

    public GamePanel(){

        setFocusable(true);
        addKeyListener(this);
        setLayout(null);

        player = new Player(280,500);

        startButton = new JButton("START GAME");
        startButton.setBounds(220,260,160,40);

        startButton.addActionListener(e->{
            gameStarted=true;
            startButton.setVisible(false);
            requestFocusInWindow();
        });

        add(startButton);

        for(int i=0;i<starX.length;i++){
            starX[i]=rand.nextInt(600);
            starY[i]=rand.nextInt(600);
        }

        timer = new Timer(16,this);
        timer.start();
    }

    public void actionPerformed(ActionEvent e){

        if(!gameStarted || paused){
            repaint();
            return;
        }

        if(gameOver){
            repaint();
            return;
        }

        player.move(left,right,up,down);

        shootCooldown--;

        if(shoot && shootCooldown<=0){
            bullets.add(new Bullet(player.x+15,player.y));
            shootCooldown=12;
        }

        // spawn เหมือนเดิม
        if(rand.nextInt(15)==0){

            int amount = rand.nextInt(2)+1;

            for(int i=0;i<amount;i++){
                int type = rand.nextInt(3);
                enemies.add(new Enemy(rand.nextInt(550),0,type));
            }
        }

        // update bullets
        for(int i=0;i<bullets.size();i++){
            bullets.get(i).body.y -= 12;

            if(bullets.get(i).body.y<0){
                bullets.remove(i);
                i--;
            }
        }

        // update enemies
        for(int i=0;i<enemies.size();i++){

            Enemy en = enemies.get(i);

            en.body.y += en.speed;

            if(en.body.y>600){
                enemies.remove(i);
                i--;
            }
        }

        checkCollision();

        repaint();
    }

    void checkCollision(){

        Rectangle playerRect = player.getBounds();

        for(int i=0;i<enemies.size();i++){

            Enemy enemy = enemies.get(i);

            if(enemy.body.intersects(playerRect)){

                player.hp -= 20;

                enemies.remove(i);

                if(player.hp<=0){
                    gameOver=true;
                }

                i--;
                continue;
            }

            for(int j=0;j<bullets.size();j++){

                if(enemy.body.intersects(bullets.get(j).body)){

                    enemy.hp--;

                    bullets.remove(j);

                    if(enemy.hp<=0){
                        enemies.remove(i);
                        score+=10;

                        if(score>highScore)
                            highScore=score;

                        i--;
                    }

                    break;
                }
            }
        }
    }

    public void paintComponent(Graphics g){

        super.paintComponent(g);

        Graphics2D g2=(Graphics2D)g;

        GradientPaint space =
                new GradientPaint(0,0,new Color(10,10,40),
                        0,600,new Color(0,0,0));

        g2.setPaint(space);
        g2.fillRect(0,0,600,600);

        g.setColor(Color.white);

        for(int i=0;i<starX.length;i++){

            g.fillRect(starX[i],starY[i],2,2);

            starY[i]+=2;

            if(starY[i]>600){
                starY[i]=0;
                starX[i]=rand.nextInt(600);
            }
        }

        // player
        g.setColor(Color.cyan);

        int[] x = {player.x+20,player.x,player.x+40};
        int[] y = {player.y,player.y+40,player.y+40};

        g.fillPolygon(x,y,3);

        // bullets
        g.setColor(Color.yellow);

        for(Bullet b:bullets){
            g.fillRect(b.body.x,b.body.y,b.body.width,b.body.height);
        }

        // enemies
        for(Enemy en:enemies){

            if(en.type==0) g.setColor(Color.red);
            else if(en.type==1) g.setColor(Color.magenta);
            else g.setColor(Color.orange);

            g.fillRect(en.body.x,en.body.y,en.body.width,en.body.height);
        }

        g.setColor(Color.white);

        g.drawString("HP : "+player.hp,10,20);
        g.drawString("Score : "+score,10,40);
        g.drawString("HighScore : "+highScore,450,20);

        if(paused){
            g.setFont(new Font("Arial",Font.BOLD,40));
            g.setColor(Color.YELLOW);
            g.drawString("PAUSED",210,300);
        }

        if(gameOver){
            g.setColor(Color.RED);
            g.setFont(new Font("Arial",Font.BOLD,40));
            g.drawString("GAME OVER",170,300);
        }
    }

    public void keyPressed(KeyEvent e){

        int key=e.getKeyCode();

        if(key==KeyEvent.VK_LEFT) left=true;
        if(key==KeyEvent.VK_RIGHT) right=true;
        if(key==KeyEvent.VK_UP) up=true;
        if(key==KeyEvent.VK_DOWN) down=true;

        if(key==KeyEvent.VK_SPACE) shoot=true;

        if(key==KeyEvent.VK_ESCAPE) paused=!paused;

        if(key==KeyEvent.VK_R && gameOver){
            restartGame();
        }
    }

    public void keyReleased(KeyEvent e){

        int key=e.getKeyCode();

        if(key==KeyEvent.VK_LEFT) left=false;
        if(key==KeyEvent.VK_RIGHT) right=false;
        if(key==KeyEvent.VK_UP) up=false;
        if(key==KeyEvent.VK_DOWN) down=false;

        if(key==KeyEvent.VK_SPACE) shoot=false;
    }

    public void keyTyped(KeyEvent e){}

    void restartGame(){

        enemies.clear();
        bullets.clear();

        player.x=280;
        player.y=500;

        player.hp=100;
        score=0;

        gameOver=false;
    }
}
