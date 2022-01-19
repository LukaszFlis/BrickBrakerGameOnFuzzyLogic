import javax.swing.*;

public class Main {
	public static void main(String[] args) {
		JFrame gameFrame = new JFrame();
		Gameplay gamePlay = new Gameplay();

		gameFrame.setBounds(10, 10, 700, 600);
		gameFrame.setTitle("Breakout Ball");
		gameFrame.setResizable(false);
		gameFrame.setVisible(true);
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.add(gamePlay);
		gameFrame.setVisible(true);

		FuzzyController fuzzyController = new FuzzyController(gamePlay);
		fuzzyController.run();
	}


}
