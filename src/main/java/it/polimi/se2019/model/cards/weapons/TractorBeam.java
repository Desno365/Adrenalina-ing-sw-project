package it.polimi.se2019.model.cards.weapons;


import it.polimi.se2019.model.cards.ammo.AmmoType;
import it.polimi.se2019.model.gamemap.Coordinates;
import it.polimi.se2019.model.player.Player;
import it.polimi.se2019.utils.Pair;
import it.polimi.se2019.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public final class TractorBeam extends AlternateFire {

	private List<Coordinates> enemyRelocationCoordinates;

	public TractorBeam(String description, ArrayList<AmmoType> reloadPrice) {
		super(description, reloadPrice);
		this.PRIMARY_DAMAGE = 1;
		this.PRIMARY_MARKS = 0;
		this.SECONDARY_DAMAGE = 3;
		this.SECONDARY_MARKS = 0;
		this.standardDamagesAndMarks = new ArrayList<>();
		this.standardDamagesAndMarks.add(new DamageAndMarks(PRIMARY_DAMAGE, PRIMARY_MARKS));
		this.secondaryDamagesAndMarks = new ArrayList<>();
		this.secondaryDamagesAndMarks.add(new DamageAndMarks(SECONDARY_DAMAGE, SECONDARY_MARKS));
		this.maximumAlternateSteps = 3;
		this.maximumSteps = 3;

	}


	@Override
	Pair handlePrimaryFire(int choice) {
		switch (getCurrentStep()){
			case 2:
				currentTargets = getPrimaryTargets();
				if(currentTargets == null)
					Utils.logError("currentTargets is null! See TractorBeam", new IllegalStateException());
				return getTargetPlayersQuestionAndOptions(currentTargets);
			case 3:
				List<Player> target = new ArrayList<>();
				target.add(currentTargets.get(choice));
				currentTargets = target;
				enemyRelocationCoordinates = getGameMap().getVisibleCoordinates(getOwner());
				return getMoveCoordinatesTargetPlayerQuestionAndOptions(currentTargets.get(0), enemyRelocationCoordinates);
			case 4:
				getGameMap().movePlayerTo(currentTargets.get(0), enemyRelocationCoordinates.get(choice));
				primaryFire();
				break;
		}
		return null;
	}

	@Override
	Pair handleSecondaryFire(int choice) {
		switch (getCurrentStep()){
			case 2:
				currentTargets = getSecondaryTargets();
				return getTargetPlayersQuestionAndOptions(currentTargets);
			case 3:
				List<Player> target = new ArrayList<>();
				target.add(currentTargets.get(choice));
				currentTargets = target;
				getGameMap().movePlayerTo(currentTargets.get(0), getGameMap().getPlayerCoordinates(getOwner()));
				secondaryFire();
				break;
		}
		return null;
	}

	/**
	 * Primary method of firing of the weapon. It interacts with the view and collects targeted players for its mode.
	 */
	@Override
	public void primaryFire() {
		dealDamage(currentTargets, standardDamagesAndMarks);
	}

	/**
	 * Secondary mode of firing.
	 */
	@Override
	public void secondaryFire() {
		dealDamage(currentTargets, secondaryDamagesAndMarks);
	}

	/**
	 * Get the targets of the primary mode of fire for this weapon.
	 *
	 * @return the targettable players.
	 */
	@Override
	public List<Player> getPrimaryTargets() {
		List<Coordinates> visibleCoordinates = getGameMap().getVisibleCoordinates(getOwner());
		List<Player> players = getAllPlayers();
		List<Player> targettablePlayers = new ArrayList<>();
		for (Player player: players) {
			List<Coordinates> intersectionCoordinates = getGameMap().reachableCoordinates(player, 2);
			intersectionCoordinates.retainAll(visibleCoordinates);
			if(!intersectionCoordinates.isEmpty()){
				targettablePlayers.add(player);
			}
		}
		return targettablePlayers.isEmpty() ? null : targettablePlayers;
	}

	/**
	 * Get the targets of the secondary mode of fire for this weapon.
	 *
	 * @return the targettable players.
	 */
	@Override
	public List<Player> getSecondaryTargets() {
		return getGameMap().reachablePlayers(getOwner(), 2);
	}
}