package fj;

/**
 * Functions that convert between Java primitive types.
 *
 * @version %build.number%
 */
public final class Primitive {
  private Primitive() {
    throw new UnsupportedOperationException();
  }

  // BEGIN Boolean ->

  /**
   * A function that converts booleans to bytes.
   */
  public static final F<Boolean, Byte> Boolean_Byte = b -> (byte) (b ? 1 : 0);

  /**
   * A function that converts booleans to characters.
   */
  public static final F<Boolean, Character> Boolean_Character = b -> (char) (b ? 1 : 0);

  /**
   * A function that converts booleans to doubles.
   */
  public static final F<Boolean, Double> Boolean_Double = b -> b ? 1D : 0D;

  /**
   * A function that converts booleans to floats.
   */
  public static final F<Boolean, Float> Boolean_Float = b -> b ? 1F : 0F;

  /**
   * A function that converts booleans to integers.
   */
  public static final F<Boolean, Integer> Boolean_Integer = b -> b ? 1 : 0;

  /**
   * A function that converts booleans to longs.
   */
  public static final F<Boolean, Long> Boolean_Long = b -> b ? 1L : 0L;

  /**
   * A function that converts booleans to shorts.
   */
  public static final F<Boolean, Short> Boolean_Short = b -> (short) (b ? 1 : 0);

  // END Boolean ->

  // BEGIN Byte ->

  /**
   * A function that converts bytes to booleans.
   */
  public static final F<Byte, Boolean> Byte_Boolean = b -> b != 0;

  /**
   * A function that converts bytes to characters.
   */
  public static final F<Byte, Character> Byte_Character = b -> (char) (byte) b;

  /**
   * A function that converts bytes to doubles.
   */
  public static final F<Byte, Double> Byte_Double = b -> (double) b;

  /**
   * A function that converts bytes to floats.
   */
  public static final F<Byte, Float> Byte_Float = b -> (float) b;

  /**
   * A function that converts bytes to integers.
   */
  public static final F<Byte, Integer> Byte_Integer = b -> (int) b;

  /**
   * A function that converts bytes to longs.
   */
  public static final F<Byte, Long> Byte_Long = b -> (long) b;

  /**
   * A function that converts bytes to shorts.
   */
  public static final F<Byte, Short> Byte_Short = b -> (short) b;

  // END Byte ->

  // BEGIN Character ->

  /**
   * A function that converts characters to booleans.
   */
  public static final F<Character, Boolean> Character_Boolean = c -> c != 0;

  /**
   * A function that converts characters to bytes.
   */
  public static final F<Character, Byte> Character_Byte = c -> (byte) (char) c;

  /**
   * A function that converts characters to doubles.
   */
  public static final F<Character, Double> Character_Double = c -> (double) (char) c;

  /**
   * A function that converts characters to floats.
   */
  public static final F<Character, Float> Character_Float = c -> (float) (char) c;

  /**
   * A function that converts characters to integers.
   */
  public static final F<Character, Integer> Character_Integer = c -> (int) (char) c;

  /**
   * A function that converts characters to longs.
   */
  public static final F<Character, Long> Character_Long = c -> (long) (char) c;

  /**
   * A function that converts characters to shorts.
   */
  public static final F<Character, Short> Character_Short = c -> (short) (char) c;

  // END Character ->

  // BEGIN Double ->

  /**
   * A function that converts doubles to booleans.
   */
  public static final F<Double, Boolean> Double_Boolean = d -> d != 0D;

  /**
   * A function that converts doubles to bytes.
   */
  public static final F<Double, Byte> Double_Byte = d -> (byte) (double) d;

  /**
   * A function that converts doubles to characters.
   */
  public static final F<Double, Character> Double_Character = d -> (char) (double) d;

  /**
   * A function that converts doubles to floats.
   */
  public static final F<Double, Float> Double_Float = d -> (float) (double) d;

  /**
   * A function that converts doubles to integers.
   */
  public static final F<Double, Integer> Double_Integer = d -> (int) (double) d;

  /**
   * A function that converts doubles to longs.
   */
  public static final F<Double, Long> Double_Long = d -> (long) (double) d;

  /**
   * A function that converts doubles to shorts.
   */
  public static final F<Double, Short> Double_Short = d -> (short) (double) d;

  // END Double ->

  // BEGIN Float ->

  /**
   * A function that converts floats to booleans.
   */
  public static final F<Float, Boolean> Float_Boolean = f -> f != 0F;

  /**
   * A function that converts floats to bytes.
   */
  public static final F<Float, Byte> Float_Byte = f -> (byte) (float) f;

  /**
   * A function that converts floats to characters.
   */
  public static final F<Float, Character> Float_Character = f -> (char) (float) f;

  /**
   * A function that converts floats to doubles.
   */
  public static final F<Float, Double> Float_Double = f -> (double) (float) f;

  /**
   * A function that converts floats to integers.
   */
  public static final F<Float, Integer> Float_Integer = f -> (int) (float) f;

  /**
   * A function that converts floats to longs.
   */
  public static final F<Float, Long> Float_Long = f -> (long) (float) f;

  /**
   * A function that converts floats to shorts.
   */
  public static final F<Float, Short> Float_Short = f -> (short) (float) f;

  // END Float ->

  // BEGIN Integer ->

  /**
   * A function that converts integers to booleans.
   */
  public static final F<Integer, Boolean> Integer_Boolean = i -> i != 0;

  /**
   * A function that converts integers to bytes.
   */
  public static final F<Integer, Byte> Integer_Byte = i -> (byte) (int) i;

  /**
   * A function that converts integers to characters.
   */
  public static final F<Integer, Character> Integer_Character = i -> (char) (int) i;

  /**
   * A function that converts integers to doubles.
   */
  public static final F<Integer, Double> Integer_Double = i -> (double) i;

  /**
   * A function that converts integers to floats.
   */
  public static final F<Integer, Float> Integer_Float = i -> (float) i;

  /**
   * A function that converts integers to longs.
   */
  public static final F<Integer, Long> Integer_Long = i -> (long) i;

  /**
   * A function that converts integers to shorts.
   */
  public static final F<Integer, Short> Integer_Short = i -> (short) (int) i;

  // END Integer ->

  // BEGIN Long ->

  /**
   * A function that converts longs to booleans.
   */
  public static final F<Long, Boolean> Long_Boolean = l -> l != 0L;

  /**
   * A function that converts longs to bytes.
   */
  public static final F<Long, Byte> Long_Byte = l -> (byte) (long) l;

  /**
   * A function that converts longs to characters.
   */
  public static final F<Long, Character> Long_Character = l -> (char) (long) l;

  /**
   * A function that converts longs to doubles.
   */
  public static final F<Long, Double> Long_Double = l -> (double) (long) l;

  /**
   * A function that converts longs to floats.
   */
  public static final F<Long, Float> Long_Float = l -> (float) (long) l;

  /**
   * A function that converts longs to integers.
   */
  public static final F<Long, Integer> Long_Integer = l -> (int) (long) l;

  /**
   * A function that converts longs to shorts.
   */
  public static final F<Long, Short> Long_Short = l -> (short) (long) l;

  // END Long ->

  // BEGIN Short ->

  /**
   * A function that converts shorts to booleans.
   */
  public static final F<Short, Boolean> Short_Boolean = s -> s != 0;

  /**
   * A function that converts shorts to bytes.
   */
  public static final F<Short, Byte> Short_Byte = s -> (byte) (short) s;

  /**
   * A function that converts shorts to characters.
   */
  public static final F<Short, Character> Short_Character = s -> (char) (short) s;

  /**
   * A function that converts shorts to doubles.
   */
  public static final F<Short, Double> Short_Double = s -> (double) (short) s;

  /**
   * A function that converts shorts to floats.
   */
  public static final F<Short, Float> Short_Float = s -> (float) (short) s;

  /**
   * A function that converts shorts to integers.
   */
  public static final F<Short, Integer> Short_Integer = s -> (int) (short) s;

  /**
   * A function that converts shorts to longs.
   */
  public static final F<Short, Long> Short_Long = s -> (long) (short) s;

  // END Short
}
