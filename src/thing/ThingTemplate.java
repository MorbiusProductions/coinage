package thing;

import actor.stats.DamageType;
import game.Game;
import game.physical.Appearance;
import game.physical.PhysicalFlag;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * A stored Prototype from which Things can be fabricated.
 */
public class ThingTemplate {

  final String name;
  final EnumSet<PhysicalFlag> flags;
  final WeaponComponent weaponComponent;
  private final List<Appearance> appearances;


  /**
   * Attempts to produce a ThingTemplate from the given map.
   *
   * @throws IOException If any invalid input is encountered.
   */
  public ThingTemplate(Map<String, String> templateMap) throws IOException {
    this.name = templateMap.get("name");
    this.appearances = parseAppearances(templateMap);
    this.flags = parseFlags(templateMap);
    this.weaponComponent = parseWeaponComponent(templateMap);
  }


  Appearance getRandomAppearance() {
    return appearances.get(Game.RANDOM.nextInt(appearances.size()));
  }


  /**
   * Attempts to extract a list of appearances from the given template map. Multiple symbol and
   * color entries must be space-separated to be recognized.
   *
   * @throws IOException If any invalid input is encountered.
   */
  private List<Appearance> parseAppearances(Map<String, String> templateMap) throws IOException {

    final List<Appearance> appearances = new ArrayList<>();

    final String[] symbolStrings = templateMap.get("symbols").split(" ");
    final List<Character> symbols = parseSymbols(symbolStrings);

    final String[] colorStrings = templateMap.get("colors").split(" ");
    final List<Color> colors = parseColors(colorStrings);

    for (char character : symbols) {
      for (Color color : colors) {
        appearances.add(new Appearance(character, color, null, Appearance.VISUAL_PRIORITY__THINGS));
      }
    }

    return appearances;
  }


  /**
   * Attempts to extract a list of symbols (ASCII representations) from the given template map.
   *
   * @throws IOException If any invalid input is encountered.
   */
  private List<Character> parseSymbols(String[] symbolStrings) throws IOException {

    final List<Character> symbols = new ArrayList<>();

    for (final String s : symbolStrings) {

      // First try to parse the input as an ASCII character code.
      try {
        symbols.add((char) Integer.parseInt(s));
      }
      catch (NumberFormatException e) {

        // If we can't, try to parse it as a literal character.
        try {
          symbols.add(s.charAt(0));
        }
        catch (IndexOutOfBoundsException iob) {
          throw new IOException("Could not parse symbol \"" + s + "\" due to invalid formatting.");
        }
      }
    }

    return symbols;
  }


  /**
   * Attempts to extract a list of colors from the given template map. Colors must come in the
   * format R:G:B, where each letter is a value between 0-255.
   *
   * @throws IOException If any invalid input is encountered.
   */
  private List<Color> parseColors(String[] colorStrings) throws IOException {

    final List<Color> colors = new ArrayList<>();

    for (final String s : colorStrings) {

      final String[] rgbStrings = s.split(":");
      final int[] rgb = new int[3];

      try {
        for (int i = 0; i < rgbStrings.length; i++) {
          final String value = rgbStrings[i];
          rgb[i] = Integer.parseInt(value);
        }
        colors.add(new Color(rgb[0], rgb[1], rgb[2]));
      }
      catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
        ex.printStackTrace();
        throw new IOException("Could not parse color \"" + s + "\" due to invalid formatting.");
      }
    }

    return colors;
  }


  /**
   * Attempts to extract a list of flags from the given template map. Multiple flags must be
   * space-separated to be recognized.
   *
   * @throws IOException If any invalid input is encountered.
   */
  private EnumSet<PhysicalFlag> parseFlags(Map<String, String> templateMap) throws IOException {

    final EnumSet<PhysicalFlag> flags = EnumSet.noneOf(PhysicalFlag.class);

    final String flagsString = templateMap.get("flags");

    if (flagsString != null) {
      final String[] splitFlagStrings = flagsString.split(" ");

      for (final String s : splitFlagStrings) {
        try {
          flags.add(PhysicalFlag.valueOf(s));
        }
        catch (IllegalArgumentException e) {
          e.printStackTrace();
          throw new IOException("Could not parse flag \"" + s + "\" because it is not recognized.");
        }
      }
    }

    return flags;
  }


  /**
   * Attempts to build a weapon component from the given template map, returning null if it fails.
   */
  private WeaponComponent parseWeaponComponent(Map<String, String> templateMap) {

    try {

      final DamageType damageType = DamageType.valueOf(templateMap.get("damage_type"));

      final int damage = Integer.parseInt(templateMap.get("damage"));
      final double damageBonusMultFromMuscle = Double.parseDouble(templateMap.get("muscle_bonus"));
      final double damageConsistency = Double.parseDouble(templateMap.get("dmg_consistency"));

      final int attackSpeed = Integer.parseInt(templateMap.get("attack_speed"));
      final double reflexAttackBonus = Double.parseDouble(templateMap.get("reflex_atk_bonus"));

      final int recoverySpeed = Integer.parseInt(templateMap.get("recovery_speed"));
      final double reflexRecoveryBonus = Double.parseDouble(templateMap.get("reflex_rec_bonus"));

      return new WeaponComponent(damageType, damage, damageBonusMultFromMuscle, damageConsistency,
          attackSpeed, reflexAttackBonus, recoverySpeed, reflexRecoveryBonus);
    }
    catch (Exception e) {
      return null; // No valid weapon data found.
    }
  }
}