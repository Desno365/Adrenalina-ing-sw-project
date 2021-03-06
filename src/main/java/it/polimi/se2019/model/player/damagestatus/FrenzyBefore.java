package it.polimi.se2019.model.player.damagestatus;

import it.polimi.se2019.utils.MacroActionBuilder;

import java.util.ArrayList;

import static it.polimi.se2019.utils.GameConstants.FRENZY_BEFORE_NUMBER_OF_ACTION_PER_TURN;

/**
 * @author Marchingegno
 */
public class FrenzyBefore extends DamageStatus {

	public FrenzyBefore() {
		super();
		MacroActionBuilder shootPeopleBuilder = new MacroActionBuilder();
		MacroActionBuilder runAroundBuilder = new MacroActionBuilder();
		MacroActionBuilder grabStuffBuilder = new MacroActionBuilder();
		availableActions = new ArrayList<>();

		shootPeopleBuilder.setMovementDistance(1);
		shootPeopleBuilder.setReloadAction(true);
		shootPeopleBuilder.setShootAction(true);
		shootPeopleBuilder.setName("Shoot");
		availableActions.add(shootPeopleBuilder.build());

		runAroundBuilder.setMovementDistance(4);
		runAroundBuilder.setName("Move");
		availableActions.add(runAroundBuilder.build());

		grabStuffBuilder.setMovementDistance(2);
		grabStuffBuilder.setGrabAction(true);
		grabStuffBuilder.setName("Grab");
		availableActions.add(grabStuffBuilder.build());

		setNumberOfMacroActionsPerTurn(FRENZY_BEFORE_NUMBER_OF_ACTION_PER_TURN);
		setNumberOfMacroActionsToPerform(getNumberOfMacroActionsPerTurn());
	}

	@Override
	public String toString() {
		return "FRENZY BEFORE";
	}

	@Override
	public boolean isFrenzy() {
		return true;
	}
}