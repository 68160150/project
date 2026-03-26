import javax.swing.*;

public class SpaceShooter {

    public static void main(String[] args){

        JFrame frame=new JFrame("Space Shooter");

        frame.add(new GamePanel());

        frame.setSize(600,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
