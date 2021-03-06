package it.polimi.se2019.model.cards.weapons;

import com.google.gson.JsonObject;
import it.polimi.se2019.model.gamemap.Coordinates;
import it.polimi.se2019.model.player.Player;
import it.polimi.se2019.utils.QuestionContainer;
import it.polimi.se2019.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Class of the weapon Plasma gun.
 * @author Marchingeno
 */
public class PlasmaGun extends OptionalChoiceWeapon {
	private List<Coordinates> possibleMoveCoordinates;

	public PlasmaGun(JsonObject parameters) {
		super(parameters);
		getStandardDamagesAndMarks().add(new DamageAndMarks(getPrimaryDamage(), getPrimaryMarks()));
		getOptional2DamagesAndMarks().add(new DamageAndMarks(getPrimaryDamage() + optional2Damage, optional2Marks));
		possibleMoveCoordinates = new ArrayList<>();

		setBaseName("Shoot somebody!");
		setMoveName("Move!");
	}

	@Override
	public List<Player> getPrimaryTargets() {
		return getGameMap().getVisiblePlayers(getOwner());
	}

	@Override
	protected QuestionContainer handleMoveRequest(int choice) {
		possibleMoveCoordinates = baseCompleted ? getMoveCoordinateAfterBase() : getMoveCoordinateBeforeBase();
		return getMoveCoordinatesQnO(possibleMoveCoordinates);
	}

	@Override
	protected void handleMoveAnswer(int choice) {
		relocateOwner(possibleMoveCoordinates.get(choice));
	}

	@Override
	protected QuestionContainer handleExtraRequest(int choice) {
		Utils.logError("PlasmaGun: Extra  called", new IllegalStateException());
		return null;
	}

	@Override
	protected void handleExtraAnswer(int choice) {
		Utils.logError("PlasmaGun: Extra  called", new IllegalStateException());
	}

	private List<Coordinates> getMoveCoordinateAfterBase() {
		return getGameMap().reachableCoordinates(getOwner(), getMoveDistance());
	}

	private List<Coordinates> getMoveCoordinateBeforeBase() {
		List<Coordinates> reachable = getGameMap().reachableCoordinates(getOwner(), getMoveDistance());
		List<Coordinates> reachableCoordinatesWithSeenPlayer = new ArrayList<>();
		for (Coordinates coordinates : reachable) {
			List<Player> seenPlayerFromThisCoordinate = getGameMap().getVisiblePlayers(coordinates);
			seenPlayerFromThisCoordinate.remove(getOwner());
			if (!seenPlayerFromThisCoordinate.isEmpty()) {
				reachableCoordinatesWithSeenPlayer.add(coordinates);
			}
		}
		return reachableCoordinatesWithSeenPlayer;
	}

	@Override
	public void primaryFire() {
		if (isOptionalActive(2)) {
			dealDamageAndConclude(getOptional2DamagesAndMarks(), getTarget());
		} else {
			dealDamageAndConclude(getStandardDamagesAndMarks(), getTarget());
		}
	}

	@Override
	protected boolean canPrimaryBeActivated() {
		return !getPrimaryTargets().isEmpty() || !getMoveCoordinateBeforeBase().isEmpty();
	}

	@Override
	protected boolean canFireOptionalEffect1() {
		return true;
	}

	@Override
	protected boolean canFireOptionalEffect2() {
		return canAddBaseWithoutEffects();
	}

	@Override
	protected void updateBooleans() {
		canAddBase = !baseCompleted && !getPrimaryTargets().isEmpty();
		canAddMove = !moveCompleted && (baseCompleted || !getMoveCoordinateBeforeBase().isEmpty());
		canAddExtra = false;

		canAddMove = canAddMove && isOptionalActive(1);
	}
}