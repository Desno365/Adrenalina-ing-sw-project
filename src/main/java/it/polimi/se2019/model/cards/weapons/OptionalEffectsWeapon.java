package it.polimi.se2019.model.cards.weapons;

import com.google.gson.JsonObject;
import it.polimi.se2019.model.cards.ammo.AmmoType;
import it.polimi.se2019.utils.QuestionContainer;
import it.polimi.se2019.utils.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * Weapons with at most two optional effects. An optional effect is an enhancement the player can choose to give at the
 * primary fire mode. Some optional effect have an additional ammo cost.
 * @author  Marchingegno
 */
public abstract class OptionalEffectsWeapon extends WeaponCard {
	private boolean[] optionalEffectsActive;
	private boolean[] canAddOptionalEffect; //Index 2 is optional 1 + 2
	private List<AmmoType> optionalPrices;
	int optional1_Damage;
	int optional1_Marks;
	int optional2_Damage;
	int optional2_Marks;
	List<DamageAndMarks> optional1DamagesAndMarks;
	List<DamageAndMarks> optional2DamagesAndMarks;
	List<DamageAndMarks> optionalBothDamagesAndMarks;


	//TODO move in constructor after it is defined.
	private void initializeVariables(){
		optionalEffectsActive = new boolean[2];
		canAddOptionalEffect = new boolean[3];
		optionalPrices = new ArrayList<>();
		optional1DamagesAndMarks = new ArrayList<>();
		optional2DamagesAndMarks = new ArrayList<>();
		optionalBothDamagesAndMarks = new ArrayList<>();
	}

	/**
	 * @deprecated
	 */
	public OptionalEffectsWeapon(String weaponName, String description, List<AmmoType> reloadPrice, final int primaryMarks, final int primaryDamage, final int moveDistance) {
		super(weaponName, description, reloadPrice, primaryMarks, primaryDamage, moveDistance);
		initializeVariables();
		reset();
	}

	/**
	 * @deprecated
	 */
	public OptionalEffectsWeapon(String weaponName, String description, List<AmmoType> reloadPrice, final int primaryMarks, final int primaryDamage) {
		super(weaponName, description, reloadPrice, primaryMarks, primaryDamage);
		initializeVariables();
		reset();
	}

	/**
	 * @deprecated
	 */
	public OptionalEffectsWeapon(String weaponName, String description, List<AmmoType> reloadPrice, final int primaryMarks) {
		super(weaponName, description, reloadPrice, primaryMarks);
		initializeVariables();
		reset();
	}

	public OptionalEffectsWeapon(JsonObject parameters) {
		super(parameters);
		initializeVariables();
		this.optional1_Damage = parameters.get("optional1Damage").getAsInt();
		this.optional1_Marks = parameters.get("optional1Marks").getAsInt();
		this.optional2_Damage = parameters.get("optional2Damage").getAsInt();
		this.optional2_Marks = parameters.get("optional2MArks").getAsInt();
	}

	@Override
	public QuestionContainer initialQuestion() {
		String question = "Which optional effect do you want to activate?";
		checkOptionalEffects();
		List<String> options = new ArrayList<>();
		options.add("No optional effects.");
		for (int i = 0; i < optionalEffectsActive.length; i++) {
			if(canAddOptionalEffect[i]){
				options.add("Optional effect "+i+".");
			}
		}
		//the following is hardcoded.
		if(canAddOptionalEffect[2]){
			options.add("Optional effect 1 + Optional effect 2");
		}
		return QuestionContainer.createStringQuestionContainer(question, options);
	}

	private void checkOptionalEffects() {
		for (int i = 0; i < canAddOptionalEffect.length; i++) {
			canAddOptionalEffect[i] = getOwner().hasEnoughAmmo(optionalPrices.subList(i,i+1));
		}
		//the following is hardcoded.
		canAddOptionalEffect[2] = getOwner().hasEnoughAmmo(optionalPrices);
	}

	protected void registerChoice(int choice) {
		switch (choice){
			case 0:
				//No optional effects.
				break;
			case 1:
				//If the first optional effect can't be added, then choice 1 is the second optional effect
				if(canAddOptionalEffect[0]){
					optionalEffectsActive[0] = true;
				}
				else{
					optionalEffectsActive[1] = true;
				}
				break;
			case 2:
				optionalEffectsActive[1] = true;
				break;
			case 3:
				optionalEffectsActive[0] = true;
				optionalEffectsActive[1] = true;
				break;
			default:
				Utils.logError("There is no options with choice number " +choice, new IllegalArgumentException());
		}
	}

	@Override
	public QuestionContainer handleFire(int choice) {
		incrementCurrentStep();
		if(getCurrentStep() == 1){
			return initialQuestion();
		}
		else if(getCurrentStep() == 2){
			registerChoice(choice);
		}
		return handlePrimaryFire(choice);
	}

	protected QuestionContainer handleOptionalEffect1(int choice){
		return null;
	}

	protected QuestionContainer handleOptionalEffect2(int choice){
		return null;
	}

	@Override
	public void primaryFire() {
		if(isBothOptionalActive()){
			dealDamage(optionalBothDamagesAndMarks, currentTargets);
		}
		else if(isOptionalActive(1)){
			dealDamage(optional1DamagesAndMarks, currentTargets);
		}
		else if(isOptionalActive(2)){
			dealDamage(optional2DamagesAndMarks, currentTargets);
		}
		else{
			dealDamage(standardDamagesAndMarks,currentTargets);
		}
	}




	public void optional1Fire(){
	}

	public void optional2Fire(){

	}

	void optionalReset(){
		for (int i = 0; i < optionalEffectsActive.length; i++) {
			optionalEffectsActive[i] = false;
		}
	}

	@Override
	public void reset() {
		super.reset();
		optionalReset();

	}

	public AmmoType getCostOfOptionalEffect(int numberOfEffect){
		return optionalPrices.get(numberOfEffect - 1);

	}

	protected boolean isOptionalActive(int optionalIndex) {
		return optionalEffectsActive[optionalIndex - 1];
	}

	protected boolean isBothOptionalActive(){
		return optionalEffectsActive[0] && optionalEffectsActive[1];
	}
}