import java.awt.*;

public class Player {

    int x, y;
    int hp = 100;

    public Player(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void move(boolean left, boolean right, boolean up, boolean down){

        if(left) x -= 6;
        if(right) x += 6;
        if(up) y -= 6;
        if(down) y += 6;

        if(x < 0) x = 0;
        if(x > 560) x = 560;

        if(y < 0) y = 0;
        if(y > 520) y = 520;
    }

    public Rectangle getBounds(){
        return new Rectangle(x,y,40,40);
    }
}
