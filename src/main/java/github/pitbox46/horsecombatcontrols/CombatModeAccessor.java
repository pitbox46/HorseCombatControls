package github.pitbox46.horsecombatcontrols;

public interface CombatModeAccessor {
    boolean inCombatMode();
    boolean toggleCombatMode();
    void setCombatMode(boolean bool);
}
