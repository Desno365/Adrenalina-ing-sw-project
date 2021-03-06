package it.polimi.se2019.model.cards.weapons;

import com.google.gson.JsonObject;
import it.polimi.se2019.model.gamemap.Coordinates;
import it.polimi.se2019.model.player.Player;
import it.polimi.se2019.utils.QuestionContainer;
import it.polimi.se2019.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Class of the weapon Cyberblade.
 *
 * @author Marchingegno
 */
public class Cyberblade extends OptionalChoiceWeapon {
    //BASE: Hit one target.
    //EXTRA: Hit a different target.
    //MOVE: Move before, after or in between effects.
    private List<Coordinates> possibleMoveCoordinates;
    private Player secondTarget;

    public Cyberblade(JsonObject parameters) {
        super(parameters);
        secondTarget = null;
        getStandardDamagesAndMarks().add(new DamageAndMarks(getPrimaryDamage(), getPrimaryMarks()));
        getStandardDamagesAndMarks().add(new DamageAndMarks(optional2Damage, optional2Marks));

        setBaseName("Whack a player");
        setMoveName("Move");
        setExtraName("Whack a player");
    }

    @Override
    public List<Player> getPrimaryTargets() {
        List<Player> players = getGameMap().getPlayersFromCoordinates(getGameMap().getPlayerCoordinates(getOwner()));
        players.remove(getOwner());
        if (getTarget() != null)
            players.remove(getTarget());
        if (secondTarget != null)
            players.remove(secondTarget);
        return players;
    }

    @Override
    protected QuestionContainer handleMoveRequest(int choice) {
        possibleMoveCoordinates = getCoordinateWithEnemies(getNumberOfHitRemaining());
        return getMoveCoordinatesQnO(possibleMoveCoordinates);
    }

    @Override
	protected void handleMoveAnswer(int choice) {
        relocateOwner(possibleMoveCoordinates.get(choice));
    }

    @Override
    protected QuestionContainer handleBaseRequest(int choice) {
        setCurrentTargets(getPrimaryTargets());
        if (secondTarget != null) {
            getCurrentTargets().remove(secondTarget);
        }
        return getTargetPlayersQnO(getCurrentTargets());
    }

    @Override
    protected QuestionContainer handleExtraRequest(int choice) {
        setCurrentTargets(getPrimaryTargets());
        if (getTarget() != null) {
            getCurrentTargets().remove(getTarget());
        }
        return getTargetPlayersQnO(getCurrentTargets());
    }

    @Override
	protected void handleExtraAnswer(int choice) {
        secondTarget = getCurrentTargets().remove(choice);
    }


    /**
     * Returns coordinates with at least numberOfEnemies in it.
     *
     * @param numberOfEnemies the minimum number of the enemies in the coordinates.
     * @return an array of coordinates with at least two players.
     */
    private List<Coordinates> getCoordinateWithEnemies(int numberOfEnemies) {
        List<Coordinates> reachable = getGameMap().reachableCoordinates(getOwner(), getMoveDistance());
        List<Coordinates> reachableWithParamPlayers = new ArrayList<>();
        for (Coordinates coordinate : reachable) {
            List<Player> playersInThisCoordinate = getGameMap().getPlayersFromCoordinates(coordinate);
            playersInThisCoordinate.remove(getOwner());
            //It's okay if i try to remove null because ArrayList is OP
            playersInThisCoordinate.remove(getTarget());
            playersInThisCoordinate.remove(secondTarget);
            if (playersInThisCoordinate.size() >= numberOfEnemies) {
                reachableWithParamPlayers.add(coordinate);
            }
        }

        Utils.logWeapon("Coordinates found in getCoordinateWithEnemies: ");
        reachableWithParamPlayers.forEach(coordinates -> Utils.logWeapon(coordinates.toString()));
        return reachableWithParamPlayers;
    }

    /**
     * Returns the number of times you can hit a player.
     *
     * @return the number of times you can hit a player.
     */
    private int getNumberOfHitRemaining() {
        int numberOfHitRemaining = 0;
        if (isExtraActive()) {
            if (!baseCompleted)
                numberOfHitRemaining++;
            if (!extraCompleted)
                numberOfHitRemaining++;
        } else {
            if (!baseCompleted)
                numberOfHitRemaining++;
        }

        Utils.logWeapon("Number of hit remaining: " + numberOfHitRemaining);
        return numberOfHitRemaining;
    }

    @Override
    public void primaryFire() {
        dealDamageAndConclude(getStandardDamagesAndMarks(), getTarget(), secondTarget);
    }

    @Override
    protected void updateBooleans() {
        canAddBase = !baseCompleted && !getPrimaryTargets().isEmpty();
        canAddExtra = !extraCompleted && !getPrimaryTargets().isEmpty();
        canAddMove = !moveCompleted && !getCoordinateWithEnemies(getNumberOfHitRemaining()).isEmpty();

        canAddMove = canAddMove && isOptionalActive(1);
        canAddExtra = canAddExtra && isOptionalActive(2);

        Utils.logWeapon("is the first optional active: " + isOptionalActive(1));
        Utils.logWeapon("is the second optional active: " + isOptionalActive(2));

        Utils.logWeapon("canAddBase - canAddExtra - canAddMove");
        Utils.logWeapon(canAddBase + " - " + canAddExtra + " - " + canAddBase);
    }

    private boolean isExtraActive() {
        return isOptionalActive(2);
    }

    @Override
    protected boolean canPrimaryBeActivated() {
        return !getPrimaryTargets().isEmpty() || !getCoordinateWithEnemies(1).isEmpty();
    }

    @Override
    protected boolean canFireOptionalEffect1() {
        return true;
    }

    @Override
    protected boolean canFireOptionalEffect2() {
        //If there are two people on your square
        List<Player> playersOnMySquare = getGameMap().getPlayersFromCoordinates(getGameMap().getPlayerCoordinates(getOwner()));
        playersOnMySquare.remove(getOwner());
        return playersOnMySquare.size() >= 2;

    }

    @Override
    protected boolean canFireBothOptionalEffects() {
        //There are at least 2 people nearby.
        //Here i'm going to use the fact that reachableCoordinates in GameMap returns also the coordinates that you're in.
        List<Coordinates> nearbyCoordinatesExceptOwn = getCoordinateWithEnemies(1);
        nearbyCoordinatesExceptOwn.remove(getGameMap().getPlayerCoordinates(getOwner()));
        //Here i check if the coordinate of the owner has 2 or more players.
        List<Player> playersInOwnCoordinate = getGameMap().getPlayersFromCoordinates(getGameMap().getPlayerCoordinates(getOwner()));
        playersInOwnCoordinate.remove(getOwner());
        if (playersInOwnCoordinate.size() >= 2)
            return true;

        //In the following loop i check whether a nearby coordinate has 2 or more players.
        if (nearbyCoordinatesExceptOwn.stream()
                .map(coordinates -> getGameMap().getPlayersFromCoordinates(coordinates))
                .anyMatch(players -> players.size() >= 2)) {
            return true;
        }

        //In the following loop i check whether the sum a nearby coordinate and own coordinate's players is equal or greather than 2.
        return nearbyCoordinatesExceptOwn.stream()
                .map(coordinates -> getGameMap().getPlayersFromCoordinates(coordinates))
                .map(players -> players.size() + playersInOwnCoordinate.size())
                .anyMatch(integer -> integer >= 2);
    }

    @Override
    public void reset() {
        super.reset();
        secondTarget = null;
    }
}
