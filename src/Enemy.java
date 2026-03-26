import java.awt.*;

public class Enemy {

    Rectangle body;
    int type;
    int speed;
    int hp;

    public Enemy(int x,int y,int type){

        this.type = type;

        if(type==0){
            body = new Rectangle(x,y,30,30);
            speed=5;
            hp=1;
        }
        else if(type==1){
            body = new Rectangle(x,y,25,25);
            speed=8;
            hp=1;
        }
        else{
            body = new Rectangle(x,y,50,50);
            speed=3;
            hp=3;
        }
    }
}
