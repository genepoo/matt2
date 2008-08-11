package matt.emd;

/*
 * Exceptions.java
 *
 * Created on 10. Mai 2004, 13:54
 */

class InvalidMatrixException extends Exception {};
class InvalidDummyCostException extends Exception {};
class InvalidWeightsVectorException extends Exception {};
class InvalidFeatureException extends Exception {};

/**
 * Thrown if the cound of <b>found</b> basic variables is not matching the required number (of the Part of the Algorithm)<br>
 * It's a exception which normally should <b>not</b> be thrown
 */
class NotEnoughBasicVariablesFoundException extends Exception {};

