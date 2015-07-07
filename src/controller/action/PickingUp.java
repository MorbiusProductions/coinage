package controller.action;

import actor.Actor;
import game.io.display.Event;
import game.io.display.EventLog;
import game.physical.Physical;
import game.physical.PhysicalFlag;
import world.Coordinate;
import world.World;

/**
 * Actors perform pickups to move items from the world to their inventory.
 */
public class PickingUp extends Action {

  private final Physical pickingUpWhat;

  public PickingUp(Actor actor, Coordinate pickingUpWhere, Physical pickingUpWhat) {
    super(actor, pickingUpWhere);
    this.pickingUpWhat = pickingUpWhat;
  }


  @Override
  public int calcDelayToPerform() {
    return 1;
  }

  @Override
  public int calcDelayToRecover() {
    return 1;
  }


  /**
   * Picking up will fail if the item is immovable, or if the item is not found at the target
   * location.
   */
  @Override
  protected boolean validate(World world) {

    if (pickingUpWhat.hasFlag(PhysicalFlag.IMMOVABLE)) {
      EventLog.registerEvent(Event.INVALID_ACTION, "That can't be picked up.");
      return false;
    }

    boolean itemIsAtTarget = world.getSquare(getTarget()).getAll().contains(pickingUpWhat);

    if (!itemIsAtTarget) {
      EventLog.registerEvent(Event.INVALID_ACTION,
          "The thing you were reaching for is no longer there.");
    }

    return itemIsAtTarget;

  }

  /**
   * Upon success, the item is added to the actor's inventory.
   */
  @Override
  protected void apply(World world) {
    world.getSquare(getTarget()).pull(pickingUpWhat);
    getActor().getInventory().addItem(pickingUpWhat);
  }

}