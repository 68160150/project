import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
// ใช้ Timer ของ Swing สำหรับ game loop
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    Timer timer; // ใช้ควบคุมการอัปเดตเกม

    Player player; // ตัวผู้เล่น

    // ตัวแปรรับ input จาก keyboard
    boolean left,right,up,down,shoot;

    int score = 0; // คะแนนปัจจุบัน
    int highScore = 0; // คะแนนสูงสุด

    // สถานะของเกม
    boolean gameStarted=false;
    boolean paused=false;
    boolean gameOver=false;

    Random rand = new Random(); // ใช้สุ่มตำแหน่ง/ศัตรู

    // เก็บกระสุนและศัตรู
    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Enemy> enemies = new ArrayList<>();

    JButton startButton; // ปุ่มเริ่มเกม

    // ดาวพื้นหลัง
    int[] starX = new int[80];
    int[] starY = new int[80];

    int shootCooldown = 0; // หน่วงเวลาการยิง

    public GamePanel(){

        setFocusable(true); // ให้ panel รับ keyboard ได้
        addKeyListener(this);
        setLayout(null);

        player = new Player(280,500); // สร้างผู้เล่น

        // สร้างปุ่มเริ่มเกม
        startButton = new JButton("START GAME");
        startButton.setBounds(220,260,160,40);

        // เมื่อกดปุ่ม → เริ่มเกม
        startButton.addActionListener(e->{
            gameStarted=true;
            startButton.setVisible(false);
            requestFocusInWindow();
        });

        add(startButton);

        // สุ่มตำแหน่งดาว
        for(int i=0;i<starX.length;i++){
            starX[i]=rand.nextInt(600);
            starY[i]=rand.nextInt(600);
        }

        // เริ่ม game loop (ประมาณ 60 FPS)
        timer = new Timer(16,this);
        timer.start();
    }

    public void actionPerformed(ActionEvent e){

        // ถ้ายังไม่เริ่มหรือ pause → หยุด logic เกม
        if(!gameStarted || paused){
            repaint();
            return;
        }

        // ถ้าเกมจบ → ไม่อัปเดตต่อ
        if(gameOver){
            repaint();
            return;
        }

        // เคลื่อนที่ผู้เล่น
        player.move(left,right,up,down);

        shootCooldown--; // ลด cooldown

        // ยิงกระสุน
        if(shoot && shootCooldown<=0){
            bullets.add(new Bullet(player.x+15,player.y));
            shootCooldown=12;
        }

        // สุ่มสร้างศัตรู
        if(rand.nextInt(15)==0){

            int amount = rand.nextInt(2)+1;

            for(int i=0;i<amount;i++){
                int type = rand.nextInt(3); // ประเภทศัตรู
                enemies.add(new Enemy(rand.nextInt(550),0,type));
            }
        }

        // อัปเดตกระสุน
        for(int i=0;i<bullets.size();i++){
            bullets.get(i).body.y -= 12; // ยิงขึ้น

            // ลบถ้าออกจอ
            if(bullets.get(i).body.y<0){
                bullets.remove(i);
                i--;
            }
        }

        // อัปเดตศัตรู
        for(int i=0;i<enemies.size();i++){

            Enemy en = enemies.get(i);

            en.body.y += en.speed; // เคลื่อนลง

            // ลบถ้าออกจอ
            if(en.body.y>600){
                enemies.remove(i);
                i--;
            }
        }

        checkCollision(); // ตรวจการชน

        repaint(); // วาดใหม่
    }

    void checkCollision(){

        Rectangle playerRect = player.getBounds();

        for(int i=0;i<enemies.size();i++){

            Enemy enemy = enemies.get(i);

            // ศัตรูชนผู้เล่น
            if(enemy.body.intersects(playerRect)){

                player.hp -= 20; // ลดเลือด
                enemies.remove(i);

                if(player.hp<=0){
                    gameOver=true;
                }

                i--;
                continue;
            }

            // กระสุนชนศัตรู
            for(int j=0;j<bullets.size();j++){

                if(enemy.body.intersects(bullets.get(j).body)){

                    enemy.hp--; // ลดเลือดศัตรู
                    bullets.remove(j);

                    // ถ้าศัตรูตาย
                    if(enemy.hp<=0){
                        enemies.remove(i);
                        score+=10;

                        // อัปเดต high score
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

        // พื้นหลังอวกาศ
        GradientPaint space =
                new GradientPaint(0,0,new Color(10,10,40),
                        0,600,new Color(0,0,0));

        g2.setPaint(space);
        g2.fillRect(0,0,600,600);

        // วาดดาว
        g.setColor(Color.white);

        for(int i=0;i<starX.length;i++){

            g.fillRect(starX[i],starY[i],2,2);

            starY[i]+=2;

            if(starY[i]>600){
                starY[i]=0;
                starX[i]=rand.nextInt(600);
            }
        }

        // วาดผู้เล่น
        g.setColor(Color.cyan);

        int[] x = {player.x+20,player.x,player.x+40};
        int[] y = {player.y,player.y+40,player.y+40};

        g.fillPolygon(x,y,3);

        // วาดกระสุน
        g.setColor(Color.yellow);

        for(Bullet b:bullets){
            g.fillRect(b.body.x,b.body.y,b.body.width,b.body.height);
        }

        // วาดศัตรู
        for(Enemy en:enemies){

            if(en.type==0) g.setColor(Color.red);
            else if(en.type==1) g.setColor(Color.magenta);
            else g.setColor(Color.orange);

            g.fillRect(en.body.x,en.body.y,en.body.width,en.body.height);
        }

        // แสดงข้อมูลเกม
        g.setColor(Color.white);

        g.drawString("HP : "+player.hp,10,20);
        g.drawString("Score : "+score,10,40);
        g.drawString("HighScore : "+highScore,450,20);

        // pause
        if(paused){
            g.setFont(new Font("Arial",Font.BOLD,40));
            g.setColor(Color.YELLOW);
            g.drawString("PAUSED",210,300);
        }

        // game over
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
