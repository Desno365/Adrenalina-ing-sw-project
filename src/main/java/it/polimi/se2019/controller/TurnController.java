package it.polimi.se2019.controller;

import it.polimi.se2019.model.Model;
import it.polimi.se2019.model.cards.ammo.AmmoType;
import it.polimi.se2019.model.gamemap.Coordinates;
import it.polimi.se2019.network.message.*;
import it.polimi.se2019.utils.ActionType;
import it.polimi.se2019.utils.QuestionContainer;
import it.polimi.se2019.utils.Utils;
import it.polimi.se2019.view.server.Event;
import it.polimi.se2019.view.server.VirtualView;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is in a lower level than GameController. It handles the logic relative
 * @author Marchingegno
 */
public class TurnController{

	private VirtualViewsContainer virtualViewsContainer;
	private Model model;


	public TurnController(Model model, VirtualViewsContainer virtualViewsContainer) {
		this.virtualViewsContainer = virtualViewsContainer;
		this.model = model;
	}


	void processEvent(Event event) {
		//TODO: Control veridicity of the message.

		VirtualView virtualView = event.getVirtualView();
		String playerName = virtualView.getNickname();

		Utils.logInfo("TurnController -> processEvent(): processing an event received from \"" + playerName + "\" with a message of type " + event.getMessage().getMessageType() + " and subtype " + event.getMessage().getMessageSubtype() + ".");

		switch(event.getMessage().getMessageType()){
			case ACTION:
				//TODO check if the player can do the action
				model.setNextMacroAction(playerName, ((IntMessage)event.getMessage()).getContent());
				handleNextAction(virtualView);
				break;
			case GRAB_AMMO:
				model.grabAmmoCard(playerName, ((IntMessage) event.getMessage()).getContent());
				handleNextAction(virtualView);
				break;
			case GRAB_WEAPON:
				if(!model.hasCurrentPlayerPayed())
					handlePayment(virtualView, model.getPriceOfTheChosenWeapon(((IntMessage) event.getMessage()).getContent()), event);
				else{
					model.setPayed(false);
					model.grabWeaponCard(playerName, ((IntMessage) event.getMessage()).getContent());
					handleNextAction(virtualView);
				}
				break;
			case SWAP_WEAPON:
				if(!model.hasCurrentPlayerPayed())
					handlePayment(virtualView, model.getPriceOfTheChosenWeapon(((SwapMessage) event.getMessage()).getIndexToGrab()), event);
				else{
					model.setPayed(false);
					SwapMessage swapMessage = (SwapMessage) event.getMessage();
					model.swapWeapons(swapMessage.getIndexToDiscard(), swapMessage.getIndexToGrab());
					handleNextAction(virtualView);
				}
				break;
			case MOVE:
				Coordinates playerChoice = ((CoordinatesAnswerMessage) event.getMessage()).getSingleCoordinates();
				if (model.getReachableCoordinatesOfTheCurrentPlayer().contains(playerChoice)) {
					model.movePlayerTo(playerName, playerChoice);
					handleNextAction(virtualView);
				} else {
					virtualView.askMove(model.getReachableCoordinatesOfTheCurrentPlayer());
				}
				break;
			case RELOAD:
				if (event.getMessage().getMessageSubtype() == MessageSubtype.REQUEST) {
					List<Integer> loadableWeapons = model.getLoadableWeapons(virtualView.getNickname());
					if (loadableWeapons.isEmpty()) {
						virtualView.askEnd(model.doesPlayerHaveActivableOnTurnPowerups(playerName));
					} else {
						virtualView.askReload(loadableWeapons);
					}
				} else {
					if(!model.hasCurrentPlayerPayed())
						handlePayment(virtualView, model.getPriceOfTheSelectedWeapon(((IntMessage) event.getMessage()).getContent()), event);
					else{
						model.setPayed(false);
						model.reloadWeapon(playerName, ((IntMessage) event.getMessage()).getContent());
						virtualView.askEnd(model.doesPlayerHaveActivableOnTurnPowerups(playerName));}
				}
				break;
			case WEAPON:
				if(model.isShootingWeapon(playerName))
					doWeaponStep(virtualView, ((IntMessage) event.getMessage()).getContent());
				else
					initialWeaponActivation(virtualView, ((IntMessage) event.getMessage()).getContent());
				break;
			case PAYMENT:
				PaymentMessage paymentMessage = (PaymentMessage) event.getMessage();
				resolvePayment(virtualView, paymentMessage.getPowerupsUsed(), paymentMessage.getPriceToPay());
				break;
			case ACTIVATE_POWERUP:
				virtualView.askPowerupActivation(model.getActivableOnTurnPowerups(playerName));
				break;
			case POWERUP:
				if(model.isPowerupInExecution(playerName))
					doPowerupStep(virtualView, ((IntMessage) event.getMessage()).getContent());
				else
					initialPowerupActivation(virtualView, ((IntMessage) event.getMessage()).getContent());
				break;
			default: Utils.logError("Received wrong type of message: " + event.toString(), new IllegalStateException());
		}
	}

	private void handleNextAction(VirtualView playerVirtualView) {
		ActionType actionType = model.getNextActionToExecuteAndAdvance(playerVirtualView.getNickname());
		Utils.logInfo("TurnController -> handleNextAction(): Performing " + actionType + " of " + model.getCurrentAction());
		switch (actionType){
			case MOVE:
				playerVirtualView.askMove(model.getCoordinatesWherePlayerCanMove());
				break;
			case GRAB:
				if (model.getGrabMessageType() == MessageType.GRAB_WEAPON) {
					if (model.currPlayerHasWeaponInventoryFull())
						playerVirtualView.askSwapWeapon(model.getIndexesOfTheGrabbableWeaponCurrentPlayer());
					else
						playerVirtualView.askGrabWeapon(model.getIndexesOfTheGrabbableWeaponCurrentPlayer());
				} else {
					model.grabAmmoCard(playerVirtualView.getNickname(), 0);
					handleNextAction(playerVirtualView);
				}
				break;
			case RELOAD:
				List<Integer> loadableWeapons = model.getLoadableWeapons(playerVirtualView.getNickname());
				if (loadableWeapons.isEmpty())
					handleNextAction(playerVirtualView);
				else
					playerVirtualView.askReload(loadableWeapons);
				break;
			case SHOOT:
				List<Integer> activableWeapons = model.getActivableWeapons(playerVirtualView.getNickname());
				if (activableWeapons.isEmpty())
					handleNextAction(playerVirtualView);
				else
					playerVirtualView.askShoot(activableWeapons);
				break;
			case END:
				//The MacroAction is already refilled.
				handleActionEnd(playerVirtualView);
				break;
			default:
				Utils.logError("This action type cannot be processed.", new IllegalStateException());
				break;
		}
	}

	private void handleActionEnd(VirtualView playerVirtualView) {
		String playerName = playerVirtualView.getNickname();
		if(model.doesThePlayerHaveActionsLeft(playerName)){
			playerVirtualView.askAction(model.doesPlayerHaveActivableOnTurnPowerups(playerName), model.doesPlayerHaveLoadedWeapons(playerName));
		} else {
			playerVirtualView.askEnd(model.doesPlayerHaveActivableOnTurnPowerups(playerName));
		}
	}


	// ####################################
	// WEAPONS METHODS
	// ####################################

	private void initialWeaponActivation(VirtualView virtualView, int indexOfWeapon) {
		if(model.canWeaponBeActivated(virtualView.getNickname(), indexOfWeapon)) {
			QuestionContainer questionContainer = model.initialWeaponActivation(virtualView.getNickname(), indexOfWeapon);
			handleWeaponQuestionContainer(virtualView, questionContainer);
		}
	}

	private void doWeaponStep(VirtualView virtualView, int choice) {
		QuestionContainer questionContainer = model.doWeaponStep(virtualView.getNickname(), choice);
		handleWeaponQuestionContainer(virtualView, questionContainer);
	}

	private void handleWeaponQuestionContainer(VirtualView virtualView, QuestionContainer questionContainer) {
		if (model.isTheWeaponConcluded(virtualView.getNickname())) {
			Utils.logWeapon("Weapon ended.");
			model.handleWeaponEnd(virtualView.getNickname());
			handleNextAction(virtualView);
		} else {
			if (questionContainer == null || questionContainer.isThisQuestionContainerUseless()) {
				Utils.logWarning("This QuestionContainer is either null or useless.");
			}
			Utils.logWeapon("Asking this question container to view:");
			Utils.logWeapon(questionContainer.getQuestion());
			virtualView.askWeaponChoice(questionContainer);
		}
	}


	// ####################################
	// POWERUPS METHODS
	// ####################################

	private void initialPowerupActivation(VirtualView virtualView, int indexOfPowerup) {
		if(model.canOnTurnPowerupBeActivated(virtualView.getNickname(), indexOfPowerup)) {
			QuestionContainer questionContainer = model.initialPowerupActivation(virtualView.getNickname(), indexOfPowerup);
			handlePowerupQuestionContainer(virtualView, questionContainer);
		}
	}

	private void doPowerupStep(VirtualView virtualView, int choice) {
		QuestionContainer questionContainer = model.doPowerupStep(virtualView.getNickname(), choice);
		handlePowerupQuestionContainer(virtualView, questionContainer);
	}

	private void handlePowerupQuestionContainer(VirtualView virtualView, QuestionContainer questionContainer) {
		if (model.isThePowerupConcluded(virtualView.getNickname())) {
			model.handlePowerupEnd(virtualView.getNickname());
			handleActionEnd(virtualView);
		} else {
			virtualView.askPowerupChoice(questionContainer);
		}
	}

	// ####################################
	// PAYMENT METHODS
	// ####################################

	private void resolvePayment(VirtualView virtualView, List<Integer> integers, List<AmmoType> priceToPay){
		model.pay(virtualView.getNickname(), priceToPay, integers);
		processEvent(model.resumeAction());
	}

	private void handlePayment(VirtualView virtualView, List<AmmoType> ammoToPay, Event eventToSave){
		model.saveEvent(eventToSave);
		if(!model.canUsePowerupToPay(virtualView.getNickname(), ammoToPay)){
			resolvePayment(virtualView,  new ArrayList<>(), ammoToPay);
		}else{
			virtualView.askToPay(ammoToPay, model.canAffordWithOnlyAmmo(virtualView.getNickname(), ammoToPay));
		}
	}
}