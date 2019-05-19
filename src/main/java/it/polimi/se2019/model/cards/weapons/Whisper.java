package it.polimi.se2019.model.cards.weapons;

import it.polimi.se2019.model.cards.ammo.AmmoType;
import it.polimi.se2019.model.player.Player;
import it.polimi.se2019.utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class Whisper extends WeaponCard {

	public Whisper(String description, List<AmmoType> reloadPrice) {
		super("Whisper", description, reloadPrice);
		this.PRIMARY_DAMAGE = 3;
		this.PRIMARY_MARKS = 1;
		this.standardDamagesAndMarks = new ArrayList<>();
		standardDamagesAndMarks.add(new DamageAndMarks(PRIMARY_DAMAGE, PRIMARY_MARKS));

	}

	@Override
	public Pair handleFire(int choice) {
		incrementStep();
		return handlePrimaryFire(choice);
	}


	@Override
	public void primaryFire() {
		dealDamage(standardDamagesAndMarks, target);
	}


	@Override
	public List<Player> getPrimaryTargets() {
		List<Player> distantPlayers = getGameMap().getVisiblePlayers(getOwner());
		distantPlayers.removeAll(getGameMap().reachablePlayers(getOwner(), 1));
		return distantPlayers;
	}


	@Override
	Pair handlePrimaryFire(int choice) {
		if(getCurrentStep() == 1){
			currentTargets = getPrimaryTargets();
			return getTargetPlayersQnO(currentTargets);
		}
		else if(getCurrentStep() == 2){
			target = currentTargets.get(choice);
			primaryFire();
		}
		return null;
	}

}