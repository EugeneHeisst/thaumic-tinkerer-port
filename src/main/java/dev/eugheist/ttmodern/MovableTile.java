package dev.eugheist.ttmodern;
/** Native equivalent of build164's movable tile contract, independent of optional AE2. */
public interface MovableTile {
 default boolean prepareToMove(){return true;}
 default void doneMoving(){}
}
