package it.polimi.se2019.model.cards.powerups;

import it.polimi.se2019.model.cards.ammo.AmmoType;
import it.polimi.se2019.model.player.Player;

/**
 * This class implements the Tagback grenade powerup
 *
 * @author MarcerAndrea
 */
public class TagbackGrenade extends PowerupCard {

	private static final String DESCRIPTION =
			"You may play this card\n" +
					"when you receive damage\n" +
					"from a player you can see.\n" +
					"Give that player 1 mark.";
	private static final int GIVEN_MARKS = 1;

	public TagbackGrenade(AmmoType associatedAmmo) {
		super("Tagback grenade", associatedAmmo, DESCRIPTION);
	}


	/**
	 * Activates the powerup.
	 *
	 * @param activatingPlayer player who as activated the powerup.
	 */
	@Override
	public void activatePowerup(Player activatingPlayer) {
		// TODO can be activated when the client receive damage from a player he can see (should this check be performed by the controller or here?).
		Player targetPlayer = activatingPlayer; // TODO placeholder, must be targetPlayer.
		targetPlayer.getPlayerBoard().addMarks(activatingPlayer, GIVEN_MARKS); // add marks to the target player.
	}

	@Override
	public String toString() {
		return "Tagback Grenade";
	}

}