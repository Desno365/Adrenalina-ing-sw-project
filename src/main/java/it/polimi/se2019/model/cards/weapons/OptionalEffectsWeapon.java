package it.polimi.se2019.model.cards.weapons;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.se2019.model.cards.ammo.AmmoType;
import it.polimi.se2019.utils.QuestionContainer;
import it.polimi.se2019.utils.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * Weapons with at most two optional effects. An optional effect is an enhancement the player can choose to give at the
 * primary fire mode. Some optional effect have an additional ammo cost.
 *
 * @author Marchingegno
 */
public abstract class OptionalEffectsWeapon extends WeaponCard {
	int optional1Damage;
	int optional1Marks;
	int optional2Damage;
	int optional2Marks;
	private boolean[] hasOptionalEffects;
	private List<DamageAndMarks> optional1DamagesAndMarks;
	private List<DamageAndMarks> optional2DamagesAndMarks;
	private List<DamageAndMarks> optionalBothDamagesAndMarks;
	private boolean[] optionalEffectsActive;
	private AmmoType optional1Price;
	private AmmoType optional2Price;
	private List<Integer> choices;


	public OptionalEffectsWeapon(JsonObject parameters) {
		super(parameters);
		hasOptionalEffects = new boolean[2];
		hasOptionalEffects[0] = true;
		hasOptionalEffects[1] = true;
		optionalEffectsActive = new boolean[2];
		choices = new ArrayList<>();
		optional1DamagesAndMarks = new ArrayList<>();
		optional2DamagesAndMarks = new ArrayList<>();
		optionalBothDamagesAndMarks = new ArrayList<>();
		this.optional1Damage = parameters.get("optional1Damage").getAsInt();
		this.optional1Marks = parameters.get("optional1Marks").getAsInt();
		this.optional2Damage = parameters.get("optional2Damage").getAsInt();
		this.optional2Marks = parameters.get("optional2Marks").getAsInt();
		optional1Price = null;
		optional2Price = null;
		int i = 1;
		for (JsonElement price : parameters.getAsJsonArray("optionalPrices")) {
			if (!price.getAsString().equals("NONE")) {
				if (i == 1) {
					optional1Price = AmmoType.valueOf(price.getAsString());
				} else {
					optional2Price = AmmoType.valueOf(price.getAsString());
				}
			}
			i++;
		}
	}

	@Override
	public QuestionContainer initialQuestion() {
		String question = "Which optional effect do you want to activate?";
		List<String> options = new ArrayList<>();

		if (canAddBaseWithoutEffects()) {
			options.add("No optional effects.");
			choices.add(0);
			Utils.logWeapon("Added 0 to choices.");
		}
		for (int i = 0; i < optionalEffectsActive.length; i++) {
			if (canAddThisOptionalEffect(i + 1) && hasOptionalEffects[i]) {
				int j = i + 1;
				options.add("Optional effect " + j + ".");
				choices.add(j);
				Utils.logWeapon("Added " + j + " to choices.");
			}
		}
		//the following is hardcoded.
		if (canAddBothOptionalEffects() && hasOptionalEffects[1]) {
			options.add("Optional effect 1 + Optional effect 2.");
			choices.add(12);
			Utils.logWeapon("Added 12 to choices.");
		}
		return QuestionContainer.createStringQuestionContainer(question, options);
	}

	/**
	 * Registers which optional effects the player has activated.
	 *
	 * @param choice the choice of the player.
	 */
	private void registerChoice(int choice) {
		choices.forEach(item -> Utils.logWeapon(item.toString()));
		switch (choices.get(choice)) {
			case 0:
				Utils.logWeapon("Only base effect selected.");
				break;
			case 1:
				optionalEffectsActive[0] = true;
				Utils.logWeapon("Selected optional effect 1.");
				break;
			case 2:
				optionalEffectsActive[1] = true;
				Utils.logWeapon("Selected optional effect 2.");
				break;
			case 12:
				optionalEffectsActive[0] = true;
				optionalEffectsActive[1] = true;
				Utils.logWeapon("Selected optional effect 1.");
				Utils.logWeapon("Selected optional effect 2.");
				break;
			default:
				Utils.logError("Unexpected value: " + choices.get(choice), new IllegalStateException("Unexpected value: " + choices.get(choice)));
		}
	}

	@Override
	public QuestionContainer doActivationStep(int choice) {
		incrementCurrentStep();
		Utils.logWeapon(this.getCardName() + ": executing main method with currentStep " + getCurrentStep() + " and choice " + choice);
		if (getCurrentStep() == 1) {
			return initialQuestion();
		} else if (getCurrentStep() == 2) {
			registerChoice(choice);
		}
		return handlePrimaryFire(choice);
	}

	/**
	 * Handles the first optional effect. A weapon can override this method to make easier handling the firing process.
	 * @param choice the choice of the player.
	 * @return the Question Container to ask to the player.
	 */
	protected QuestionContainer handleOptionalEffect1(int choice) {
		return null;
	}

	/**
	 * Handles the second optional effect. A weapon can override this method to make easier handling the firing process.
	 * @param choice the choice of the player.
	 * @return the Question Container to ask to the player.
	 */
	protected QuestionContainer handleOptionalEffect2(int choice) {
		return null;
	}

	/**
	 * Handles both optional effects. A weapon can override this method to make easier handling the firing process.
	 * @param choice the choice of the player.
	 * @return the Question Container to ask to the player.
	 */
	protected QuestionContainer handleBothOptionalEffects(int choice) {
		return null;
	}

	@Override
	public void primaryFire() {
		if (isBothOptionalActive()) {
			Utils.logWeapon("Firing with optionalBothDamagesAndMarks");
			dealDamageAndConclude(optionalBothDamagesAndMarks, getCurrentTargets());
		} else if (isOptionalActive(1)) {
			Utils.logWeapon("Firing with optional1DamagesAndMarks");
			dealDamageAndConclude(optional1DamagesAndMarks, getCurrentTargets());
		} else if (isOptionalActive(2)) {
			Utils.logWeapon("Firing with optional2DamagesAndMarks");
			dealDamageAndConclude(optional2DamagesAndMarks, getCurrentTargets());
		} else {
			Utils.logWeapon("Firing with standardDamagesAndMarks");
			dealDamageAndConclude(getStandardDamagesAndMarks(), getCurrentTargets());
		}
	}

	@Override
	protected boolean canPrimaryBeActivated() {
		return canAddBaseWithoutEffects();
	}

	/**
	 * Checks if the player can add the base effect without any optional effects.
	 * @return true if the player can add the base effect.
	 */
	protected boolean canAddBaseWithoutEffects() {
		return !getPrimaryTargets().isEmpty();
	}

	/**
	 * Reset this class.
	 */
	private void optionalReset() {
		for (int i = 0; i < optionalEffectsActive.length; i++) {
			optionalEffectsActive[i] = false;
		}
		choices = new ArrayList<>();
	}

	@Override
	public void reset() {
		super.reset();
		optionalReset();
	}

	/**
	 * Returns whether or not the optional effect is active.
	 * @param optionalIndex the index of the optional effect.
	 * @return if the optional effect is active.
	 */
	boolean isOptionalActive(int optionalIndex) {
		return optionalEffectsActive[optionalIndex - 1];
	}

	/**
	 * Checks whether both optional effects are active.
	 * @return true if both optional effects are active.
	 */
	boolean isBothOptionalActive() {
		return optionalEffectsActive[0] && optionalEffectsActive[1];
	}

	/**
	 * Checks if the player can afford and can fire an optional effect.
	 * @param numberOfEffect the index of the effect.
	 * @return true if the player can afford and fire the optional effect.
	 */
	private boolean canAddThisOptionalEffect(int numberOfEffect) {
		if (numberOfEffect == 1)
			return canAddOptionalEffect1();
		else
			return canAddOptionalEffect2();
	}

	/**
	 * Checks if the player can afford and can fire the first optional effect.
	 * @return true if the player can afford and fire the optional effect.
	 */
	private boolean canAddOptionalEffect1() {
		return canAffordOptionalEffect1() && canFireOptionalEffect1();
	}

	/**
	 * Checks if the player can afford and can fire the second optional effect.
	 * @return true if the player can afford and fire the optional effect.
	 */
	private boolean canAddOptionalEffect2() {
		if (!hasOptionalEffects[1]) return false;
		return canAffordOptionalEffect2() && canFireOptionalEffect2();
	}

	/**
	 * Checks if the player can afford and can fire both optional effects.
	 * @return true if the player can afford and fire the optional effects.
	 */
	private boolean canAddBothOptionalEffects() {
		if (!hasOptionalEffects[1]) return false;
		return canAffordBothOptionalEffects() && canFireBothOptionalEffects();
	}

	/**
	 * Checks if the player can afford the first optional effect.
	 * @return true if the player can afford the optional effect.
	 */
	boolean canAffordOptionalEffect1() {
		if (optional1Price == null)
			return true;
		List<AmmoType> optional1Cost = new ArrayList<>();
		optional1Cost.add(optional1Price);
		return getOwner().hasEnoughAmmo(optional1Cost);
	}

	/**
	 * Checks if the player can afford the second optional effect.
	 * @return true if the player can afford the optional effect.
	 */
	private boolean canAffordOptionalEffect2() {
		if (optional2Price == null)
			return true;
		List<AmmoType> optional2Cost = new ArrayList<>();
		optional2Cost.add(optional2Price);
		return getOwner().hasEnoughAmmo(optional2Cost);
	}

	/**
	 * Checks if the player can afford both optional effects simultaneously.
	 * @return true if the player can afford the optional effects.
	 */
	private boolean canAffordBothOptionalEffects() {
		List<AmmoType> prices = new ArrayList<>();
		if (optional1Price != null) {
			prices.add(optional1Price);
		}
		if (optional2Price != null) {
			prices.add(optional2Price);
		}
		return getOwner().hasEnoughAmmo(prices);
	}

	/**
	 * Checks if the player can fire the first optional effect.
	 *
	 * @return true if the player can fire the optional effect.
	 */
	protected abstract boolean canFireOptionalEffect1();

	/**
	 * Checks if the player can fire the second optional effect.
	 * This method can be overrided, but it is not abstract because some weapons don't have the second optional effect.
	 * @return true if the player can fire the optional effect.
	 */
	protected boolean canFireOptionalEffect2() {
		return false;
	}

	/**
	 * Checks if the player can fire both optional effects simultaneously.
	 * @return true if the player can fire the optional effects.
	 */
	protected boolean canFireBothOptionalEffects() {
		return canFireOptionalEffect1() && canFireOptionalEffect2();
	}

	@Override
	public List<AmmoType> getFiringCost(int choice) {
		List<AmmoType> firingCost = new ArrayList<>();
		switch (choices.get(choice)) {
			case 0:
				break;
			case 1:
				if (optional1Price != null)
					firingCost.add(optional1Price);
				break;
			case 2:
				if (optional2Price != null)
					firingCost.add(optional2Price);
				break;
			case 12:
				if (optional1Price != null)
					firingCost.add(optional1Price);
				if (optional2Price != null)
					firingCost.add(optional2Price);
				break;
			default:
				Utils.logError("Unexpected value: " + choices.get(choice), new IllegalStateException("Unexpected value: " + choices.get(choice)));
		}
		return firingCost;
	}

	//**********
	//GETTERS
	//**********

	boolean[] getHasOptionalEffects() {
		return hasOptionalEffects;
	}


	List<DamageAndMarks> getOptional1DamagesAndMarks() {
		return optional1DamagesAndMarks;
	}

	List<DamageAndMarks> getOptional2DamagesAndMarks() {
		return optional2DamagesAndMarks;
	}


	List<DamageAndMarks> getOptionalBothDamagesAndMarks() {
		return optionalBothDamagesAndMarks;
	}
}