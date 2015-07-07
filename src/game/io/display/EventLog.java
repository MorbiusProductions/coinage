package game.io.display;

import utils.Dimension;
import utils.Utils;
import world.Area;
import world.AreaCoordinate;
import world.Coordinate;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class EventLog {

  private static final double TOP_OR_BOTTOM_SWAP_LINE = 0.75;

  // VALUES IN MILLISECONDS
  private static final long EVENT_LIFESPAN = 8000;

  // VALUES IN PIXELS
  private static final int LINE_HEIGHT = GameDisplay.SQUARE_SIZE / 9 * 8;
  private static final int LINE_SPACER = LINE_HEIGHT / 3;

  // VALUES IN LINES
  private static final int MINIMUM_HEIGHT = 5;
  private static final int MAXIMUM_HEIGHT = 10;

  private static final Font SMALL_TEXT = new Font("Monospaced", Font.PLAIN, LINE_HEIGHT);
  private static final List<Event> events = new ArrayList<>();
  private static int liveEventsIndex = 0;


  private static int getLinesTall() {
    return Utils.clamp(events.size() - liveEventsIndex, MINIMUM_HEIGHT, MAXIMUM_HEIGHT);
  }


  public static void registerEvent(Color color, String message) {
    events.add(new Event(color, message));
  }


  public static void drawOverlay(Graphics2D g2d) {

    g2d.setFont(SMALL_TEXT);

    // check for expired events and push live events index forward accordingly
    boolean allDead = true;
    for (int i = liveEventsIndex; i < events.size(); i++) {
      if (System.currentTimeMillis() - events.get(i).timePosted < EVENT_LIFESPAN) {
        liveEventsIndex = i;
        allDead = false;
        break;
      }
    }

    if (allDead) {
      return; // draw nothing if there's no messages.
    }

    final AreaCoordinate playerAt = GameDisplay.getRunningGame().getWorld().convertToAreaCoordinate(
        GameDisplay.getRunningGame().getActivePlayerActor().getCoordinate());

    final Dimension areaSizeInSquares =
        GameDisplay.getRunningGame().getWorld().getAreaSizeInSquares();

    final int areaHeight = areaSizeInSquares.getHeight();

    boolean drawingTop = (playerAt.areaY > areaHeight * TOP_OR_BOTTOM_SWAP_LINE);

    int linesTall = getLinesTall();

    int drawWidth = (int) (GameDisplay.SQUARE_SIZE * areaSizeInSquares.getWidth() * 0.70);
    int drawHeight = LINE_HEIGHT * linesTall + LINE_SPACER;
    int drawX = areaSizeInSquares.getWidth() / 2 * GameDisplay.SQUARE_SIZE - drawWidth / 2;
    int drawY;

    if (drawingTop) {
      drawY = LINE_SPACER;
    }
    else {
      drawY = GameDisplay.SQUARE_SIZE * areaHeight - drawHeight;
    }

    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
    g2d.setColor(Color.BLACK);
    g2d.fillRect(drawX, drawY, drawWidth, drawHeight);
    g2d.setColor(Color.DARK_GRAY);
    g2d.drawRect(drawX, drawY, drawWidth, drawHeight);

    // draw live event messages, bottom-up, for as long as there's room

    for (int i = 0; i < events.size() - liveEventsIndex && i < linesTall; i++) {

      Event event = events.get(events.size() - 1 - i);

      g2d.setColor(event.color);
      g2d.drawString(event.message, drawX + 10, drawY + drawHeight - LINE_HEIGHT * i - LINE_SPACER);
    }
  }


  public static void registerEventIfPlayerIsNear(Coordinate nearTo, Color color, String message) {
    final Area playerAt = GameDisplay.getRunningGame().getWorld()
        .getArea(GameDisplay.getRunningGame().getActivePlayerActor().getCoordinate());
    if (playerAt == GameDisplay.getRunningGame().getWorld().getArea(nearTo)) {
      registerEvent(color, message);
    }
  }
}