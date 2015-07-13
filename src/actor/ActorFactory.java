package actor;

import actor.attribute.Attribute;
import actor.attribute.AttributeRange;
import actor.attribute.Rank;
import game.Game;
import game.io.GameResources;

import java.util.HashMap;
import java.util.Map;

public class ActorFactory {

  public static Actor makeActor(String actorTemplateID) {
    ActorTemplate actorTemplate =
        GameResources.getActorLibrary().get(actorTemplateID.toUpperCase());
    if (actorTemplate != null) {
      return new Actor(actorTemplate);
    }
    return null;
  }


  static Map<Attribute, Rank> makeAttributeMap(ActorTemplate actorTemplate) {

    Map<Attribute, Rank> attributes = new HashMap<>();

    for (Attribute attribute : Attribute.values()) {
      AttributeRange attributeRange = actorTemplate.baseAttributeRanges.get(attribute.ordinal());
      attributes.put(attribute, attributeRange.getRandomWithin(Game.RANDOM));
    }

    return attributes;
  }
}