package it.polimi.se2019.model.player.damagestatus;

import it.polimi.se2019.utils.MacroActionBuilder;

import java.util.ArrayList;

import static it.polimi.se2019.utils.GameConstants.NUMBER_OF_ACTIONS_PER_TURN;

/**
 * @author Marchingegno
 */
public class LowDamage extends DamageStatus {

	public LowDamage() {
		super();
		MacroActionBuilder runAroundBuilder = new MacroActionBuilder();
		MacroActionBuilder grabStuffBuilder = new MacroActionBuilder();
		MacroActionBuilder shootPeopleBuilder = new MacroActionBuilder();
		availableActions = new ArrayList<>();

		runAroundBuilder.setMovementDistance(3);
		runAroundBuilder.setName("Move");
		availableActions.add(runAroundBuilder.build());

		grabStuffBuilder.setMovementDistance(1);
		grabStuffBuilder.setGrabAction(true);
		grabStuffBuilder.setName("Grab");
		availableActions.add(grabStuffBuilder.build());

		shootPeopleBuilder.setShootAction(true);
		shootPeopleBuilder.setName("Shoot");
		availableActions.add(shootPeopleBuilder.build());

		setNumberOfMacroActionsPerTurn(NUMBER_OF_ACTIONS_PER_TURN);
		setNumberOfMacroActionsToPerform(getNumberOfMacroActionsPerTurn());
	}

	@Override
	public String toString() {
		return "LOW DAMAGE";
	}


	@Override
	public boolean isFrenzy() {
		return false;
	}
}