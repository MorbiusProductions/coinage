package controller;

import actor.Actor;
import game.Direction;
import game.Game;
import game.Physical;
import game.display.Event;
import game.display.EventLog;
import world.Coordinate;
import world.World;

/**
 *
 */
public abstract class ActorController implements Controller {

  private final Actor actor;
  private Coordinate coordinate;


  public ActorController(Actor actor, Coordinate coordinate) {

    if (actor == null || coordinate == null) {
      throw new IllegalArgumentException("Cannot instantiate ActorController without an actor and" +
          " a coordinate.");
    }

    this.actor = actor;
    this.coordinate = coordinate;

  }



  private Action action;
  private Direction facing;

  private Physical grabbing;
  private Coordinate grabbingAt;

  private Physical dropping;
  private Coordinate droppingAt;

  private int beatsToRecover = 0;

  public final void startGrabbing(Physical grabbing, Coordinate grabbingAt) {
    if (actor.getInventory() == null) {
      System.out.println("Tried to grab for an actor without an inventory.");
      return;
    }
    if (grabbing.isImmovable()) {
      EventLog.registerEvent(Event.INVALID_ACTION,"That can't be picked up.");
      return;
    }

    action = Action.GRABBING;

    this.grabbing = grabbing;
    this.grabbingAt = grabbingAt;

  }

  public final void startDropping(Physical dropping, Coordinate droppingAt) {
    if (droppingAt.getSquare().isBlocked()) {
      EventLog.registerEvent(Event.INVALID_ACTION, "There's no room there.");
      return;
    }

    action = Action.DROPPING;

    this.dropping = dropping;
    this.droppingAt = droppingAt;

  }

  public final void startMoving(Direction movingIn) {
    action = Action.MOVING;
    facing = movingIn;
  }

  public final void stopMoving() {
    action = null;
  }

  protected final Action getCurrentAction() {
    return action;
  }


  /**
   * @return A new Point containing this ActorController's coordinate. Changes to this Point
   * are not reflected by this ActorController.
   */
  public final Coordinate getCoordinate() {
    return coordinate;
  }





  @Override
  public final void onUpdate() {

    World world = Game.getActiveWorld();

    if (beatsToRecover > 0) {

      beatsToRecover--; // until this is zero no action can be taken.

    } else if (action == Action.MOVING) {

      Coordinate newCoordinate =
          world.offsetCoordinateBySquares(coordinate, facing.relativeX, facing.relativeY);

      if (newCoordinate != null && world.move(actor, coordinate, newCoordinate)) {

        coordinate = newCoordinate;
        beatsToRecover = action.beatsToPerform;

        onMoveSucceeded();

      } else {

        onActionFailed(Action.MOVING, "Can't move there.");

      }

    } else if (action == Action.GRABBING) {

      if (grabbingAt.getSquare().pull(grabbing)) {
        actor.getInventory().addItem(grabbing);
      } else {
        onActionFailed(Action.GRABBING, "The thing you were reaching for is no longer there.");
      }

      grabbing = null;
      grabbingAt = null;
      action = null;

    } else if (action == Action.DROPPING) {

      if (actor.getInventory().removeItem(dropping)){
        droppingAt.getSquare().put(dropping);
      } else {
        onActionFailed(Action.DROPPING,"You can't seem to find the thing you were trying to drop.");
      }

      grabbing = null;
      grabbingAt = null;
      action = null;

    }



    onUpdateProcessed();

  }

  /**
   * Subclasses of ActorController should override this method to hook into successful movement.
   */
  protected void onMoveSucceeded() { }

  /**
   * Subclasses of ActorController should override this method to hook into game updates. This
   * function is run at the end of this ActorController's onUpdate() call, and should be used to
   * setup the actor for FUTURE updates, rather than the current one.
   */
  protected void onActionFailed(Action action, String message) { }

  /**
   * Subclasses of ActorController should override this method to hook into game updates. This
   * function is run at the end of this ActorController's onUpdate() call, and should be used to
   * setup the actor for FUTURE updates, rather than the current one.
   */
  protected void onUpdateProcessed() { }

  public Actor getActor() {
    return actor;
  }

}