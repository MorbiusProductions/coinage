package game.display;

import controller.player.PlayerController;
import game.Game;
import game.Physical;
import world.Area;
import world.World;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class AreaPanel extends JPanel {


  private static final int SQUARE_SIZE = GameDisplay.SQUARE_SIZE;

  public AreaPanel() {
    setBackground(Color.BLACK);
    setFont(new Font("Monospace", Font.BOLD, SQUARE_SIZE));
  }


  @Override
  public void paint(Graphics g) {
    super.paint(g);

    PlayerController pC = Game.getActive().CONTROLLERS.getPlayerController();
    Point playerCoordinate = pC.getGlobalCoordinate();

    World world = Game.getActive().WORLD;
    Area area = world.getAreaByGlobalCoordinate(playerCoordinate.x,playerCoordinate.y);

    for (int y = 0; y < world.getAreaSizeInSquares().getHeight(); y++) {
      for (int x = 0; x < world.getAreaSizeInSquares().getWidth(); x++) {

        Appearance visible = area.getPhysicalsComponent().getPriorityPhysical(x,y).getAppearance();

        int placeX = (x) * SQUARE_SIZE;
        int placeY = (y + 1) * SQUARE_SIZE;

        SquareDrawer.drawSquare(g, visible, SQUARE_SIZE, placeX, placeY);

        Point cursorTarget = Game.getActive().INPUT_SWITCH.getCursorTarget();
        if (cursorTarget.x == x && cursorTarget.y == y) {
          SquareDrawer.drawOval(g, GameDisplay.CURSOR, SQUARE_SIZE, placeX, placeY);
        }

      }
    }
  }
}