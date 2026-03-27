import java.awt.*;

public class Player {

    int x, y;
    int hp = 100;

    public Player(int x, int y){
        this.x = x;
        this.y = y;
    }
    public void move(){
        // ไม่กดอะไร = ไม่ขยับ
    }
    public void move(boolean left, boolean right, boolean up, boolean down){

        if(left) x -= 5;
        if(right) x += 5;
        if(up) y -= 5;
        if(down) y += 5;

        int width = 40;
        int height = 40;

        // ขอบซ้าย-ขวา
        if(x < 0) x = 0;
        if(x > 600 - width) x = 600 - width;

        // ขอบบน-ล่าง
        if(y < 0) y = 0;
        if(y + height > 600) y = 600 - height;
    }
    public Rectangle getBounds(){
        return new Rectangle(x,y,40,40);
    }
}
