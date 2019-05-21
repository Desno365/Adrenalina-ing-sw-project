package it.polimi.se2019.view.client;

import it.polimi.se2019.model.cards.powerups.PowerupCardRep;
import it.polimi.se2019.model.cards.weapons.WeaponRep;
import it.polimi.se2019.model.gamemap.Coordinates;
import it.polimi.se2019.model.gamemap.SpawnSquareRep;
import it.polimi.se2019.model.player.damagestatus.DamageStatusRep;
import it.polimi.se2019.network.client.Client;
import it.polimi.se2019.network.message.*;
import it.polimi.se2019.utils.Color;
import it.polimi.se2019.utils.GameConstants;
import it.polimi.se2019.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static it.polimi.se2019.view.client.CLIPrinter.*;

/**
 * @author MarcerAndrea
 * @author Desno365
 * @author Marchingegno
 */
public class CLIView extends RemoteView {

	private Scanner scanner = new Scanner(System.in);
	private RepPrinter repPrinter = new RepPrinter(getModelRep());

	public static void print(String string) {
		System.out.print(string);
	}

	public static void printLine(String string) {
		System.out.println(string);
	}

	@Override
	public void askForConnectionAndStartIt() {
		printChooseConnection();
		if (Integer.parseInt(waitForChoiceInMenu("1", "2")) == 1)
			startConnectionWithRMI();
		else
			startConnectionWithSocket();
	}

	@Override
	public void failedConnectionToServer() {
		print("Failed to connect to the server. Try again later.");
		Client.terminateClient();
	}

	@Override
	public void askNickname() {
		printChooseNickname();
		String chosenNickname = scanner.nextLine();
		sendMessage(new NicknameMessage(chosenNickname, MessageSubtype.ANSWER));
	}

	@Override
	public void lostConnectionToServer() {
		printLine("Lost connection with the server. Please restart the game.");
		Client.terminateClient();
	}

	@Override
	public void askNicknameError() {
		printLine("The nickname already exists or is not valid, please use a different one.");
		askNickname();
	}

	@Override
	public void displayWaitingPlayers(List<String> waitingPlayers) {
		printWaitingRoom(waitingPlayers);
	}

	@Override
	public void displayTimerStarted(long delayInMs) {
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(1);
		new Thread(() -> {
			printWaitingMatchStart(delayInMs);
		}).start();
	}

	@Override
	public void displayTimerStopped() {
		printLine(Color.getColoredString("Timer for starting the match cancelled.", Color.CharacterColorType.RED));
	}

	@Override
	public void askMapAndSkullsToUse() {
		Utils.logInfo("\n\nMatch ready to start. Select your preferred configuration.");
		int mapIndex = askMapToUse();
		int skulls = askSkullsForGame();
		ArrayList<String> players = new ArrayList<>();
		printWaitingRoom(players);
		GameConfigMessage gameConfigMessage = new GameConfigMessage(MessageSubtype.ANSWER);
		gameConfigMessage.setMapIndex(mapIndex);
		gameConfigMessage.setSkulls(skulls);
		sendMessage(gameConfigMessage);
	}

	@Override
	public void showMapAndSkullsInUse(int skulls, GameConstants.MapType mapType) {
		Utils.logInfo("CLIView -> showMapAndSkullsInUse(): Average of voted skulls: " + skulls + ", most voted map " + mapType.toString() + ".");
	}

	@Override
	public void askAction(List<Integer> activablePowerups) {
		DamageStatusRep damageStatusRep = getModelRep().getClientPlayerRep().getDamageStatusRep();

		printLine("Choose an action!");
		int macroActionsNum = getModelRep().getClientPlayerRep().getDamageStatusRep().getNumberOfMacroActionsPerTurn() - getModelRep().getClientPlayerRep().getDamageStatusRep().getNumberOfMacroActionsToPerform() + 1;
		int macroActionTotal = getModelRep().getClientPlayerRep().getDamageStatusRep().getNumberOfMacroActionsPerTurn();
		printLine("Action " + macroActionsNum + " of " + macroActionTotal  + ".");

		int i;
		int answer;
		for (i = 0; i < damageStatusRep.numOfMacroActions(); i++)
			printLine((i + 1) + ") " + damageStatusRep.getMacroActionName(i) + " " + damageStatusRep.getMacroActionString(i));
		if(!activablePowerups.isEmpty()) {
			printLine((i + 1) + ") Powerup");
			answer = askInteger(1, damageStatusRep.numOfMacroActions() + 1);
		} else {
			answer = askInteger(1, damageStatusRep.numOfMacroActions());
		}

		if(answer == damageStatusRep.numOfMacroActions() + 1) { // If answer is powerup.
			int powerupAnswer = askPowerupToActivate(activablePowerups);
			sendMessage(new IntMessage(powerupAnswer, MessageType.ON_TURN_POWERUP, MessageSubtype.ANSWER));
		} else {
			sendMessage(new DefaultActionMessage(answer - 1, MessageType.ACTION, MessageSubtype.ANSWER));
		}
	}

	@Override
	public void askGrab(Message message) {
		if (message.getMessageType() == MessageType.GRAB_AMMO)
			sendMessage(new DefaultActionMessage(0, MessageType.GRAB_AMMO, MessageSubtype.ANSWER));
		if (message.getMessageType() == MessageType.GRAB_WEAPON)
			askGrabWeapon();
	}

	@Override
	public void askMove(List<Coordinates> reachableCoordinates) {
		repPrinter.displayGame(reachableCoordinates);
		printLine("Enter the coordinates in which you want to move.");
		Coordinates answer = askCoordinates(reachableCoordinates);
		sendMessage(new CoordinatesAnswerMessage(answer, MessageType.MOVE));
	}

	@Override
	public void askShoot() {
		printLine("LOL");
		sendMessage(new Message(MessageType.END_TURN, MessageSubtype.ANSWER)); // TODO: this is a placeholder
	}

	@Override
	public void askReload() {
		printLine("Which weapon do you want to reload?");
		printLine("Select a number between 0 and 2.");
		int answer = askInteger(0, 2);
		// Send a message to the server with the answer for the request. The server will process it in the VirtualView class.
		sendMessage(new DefaultActionMessage(answer, MessageType.RELOAD, MessageSubtype.ANSWER));
	}


	@Override
	public void askEnd(List<Integer> activablePowerups) {
		printLine("Choose an action!");
		printLine("1) End turn");
		printLine("2) Reload");
		int answer;
		if(activablePowerups.isEmpty()) {
			answer = askInteger(1, 2);
		} else {
			printLine("3) Powerup");
			answer = askInteger(1, 3);
		}

		if(answer == 1) {
			// End turn.
			sendMessage(new Message(MessageType.END_TURN, MessageSubtype.ANSWER));
		} else if(answer == 2) {
			// Reload.
			askReload();
		} else if(answer == 3) {
			// Ask powerup.
			int powerupAnswer = askPowerupToActivate(activablePowerups);
			sendMessage(new IntMessage(powerupAnswer, MessageType.ON_TURN_POWERUP, MessageSubtype.ANSWER));
		}
	}

	@Override
	public void askSpawn() {
		List<PowerupCardRep> powerupCards = getModelRep().getClientPlayerRep().getPowerupCards();
		printLine("Select the Powerup card to discard in order to spawn:");
		for (int i = 0; i < powerupCards.size(); i++)
			printLine((i + 1) + ") " + powerupCards.get(i).getCardName() + Color.getColoredString(" ●", powerupCards.get(i).getAssociatedAmmo().getCharacterColorType()));
		int answer = askInteger(1, powerupCards.size());

		// Send a message to the server with the answer for the request. The server will process it in the VirtualView class.
		sendMessage(new DefaultActionMessage(answer - 1, MessageType.SPAWN, MessageSubtype.ANSWER));
	}

	@Override
	public int askPowerupToActivate(List<Integer> activablePowerups) {
		List<PowerupCardRep> powerupCards = getModelRep().getClientPlayerRep().getPowerupCards();
		printLine("Select the Powerup card to activate:");
		for (int i = 0; i < powerupCards.size(); i++) {
			if(activablePowerups.contains(i))
				printLine((i + 1) + ") " + powerupCards.get(i).getCardName() + Color.getColoredString(" ●", powerupCards.get(i).getAssociatedAmmo().getCharacterColorType()));
		}
		return askIntegerFromList(activablePowerups, -1);
	}

	@Override
	public void askChoice(String question, List<String> options) {

	}

	@Override
	public void askPowerupChoice(String question, List<String> options) {
		printLine(question);
		for (int i = 1; i < options.size() + 1; i++) {
			printLine(i + ") " + options.get(i - 1));
		}
		int answer = askInteger(1, options.size());
		sendMessage(new IntMessage(answer - 1, MessageType.POWERUP_INFO_OPTIONS, MessageSubtype.ANSWER));
	}

	@Override
	public void askPowerupCoordinates(String question, List<Coordinates> coordinates) {
		repPrinter.displayGame(coordinates);
		printLine(question);
		Coordinates answer = askCoordinates(coordinates);
		sendMessage(new CoordinatesAnswerMessage(answer, MessageType.POWERUP_INFO_COORDINATES));
	}

	private int askMapToUse() {
		printChooseMap();
		ArrayList<String> possibleChoices = new ArrayList<>();
		for (int i = 1; i <= GameConstants.MapType.values().length; i++) {
			possibleChoices.add(Integer.toString(i));
		}
		return Integer.parseInt(waitForChoiceInMenu(possibleChoices));
	}

	private int askSkullsForGame() {
		printChooseSkulls();
		ArrayList<String> possibleChoices = new ArrayList<>();
		for (int i = GameConstants.MIN_SKULLS; i <= GameConstants.MAX_SKULLS; i++)
			possibleChoices.add(Integer.toString(i));
		return Integer.parseInt(waitForChoiceInMenu(possibleChoices));
	}

	@Override
	public void askGrabWeapon() {
		printLine("Choose a weapon to pickup:");
		List<WeaponRep> weaponReps = ((SpawnSquareRep) getModelRep().getGameMapRep().getPlayerSquare(getNickname())).getWeaponsRep();
		for (int i = 0; i < weaponReps.size(); i++) {
			printLine("[" + (i + 1) + "] " + repPrinter.getWeaponRepString(weaponReps.get(i)));
		}
		sendMessage(new DefaultActionMessage(askInteger(1, weaponReps.size()) - 1, MessageType.GRAB_WEAPON, MessageSubtype.ANSWER));
	}

	/**
	 * Displays the main game board
	 */
	public void updateDisplay() {
		repPrinter.displayGame();
	}

	/**
	 * Ask the user an integer that must be between minInclusive and maxInclusive.
	 * Repeatedly ask the integer if the input is not in the limits.
	 *
	 * @param minInclusive the minimum limit.
	 * @param maxInclusive the maximum limit.
	 * @return the integer chosen by the user.
	 */
	private int askInteger(int minInclusive, int maxInclusive) {
		int input = 0;
		boolean ok;
		do {
			try {
				input = Integer.parseInt(scanner.nextLine());
				ok = true;
			} catch (NumberFormatException e) {
				ok = false;
			}
			if (!ok || input < minInclusive || input > maxInclusive) { // ok must be true and input must be between min and max.
				printLine("The value must be between " + minInclusive + " and " + maxInclusive + ".");
			}
		} while (!ok || input < minInclusive || input > maxInclusive);
		return input;
	}

	/**
	 * Ask the user an integer that must be in the options list.
	 * Repeatedly ask the integer if the input is the list.
	 *
	 * @param options the list containing the possible options.
	 * @param offset number to add to the answer before checking if it is contained in the list.
	 * @return the integer chosen by the user + the offset.
	 */
	private int askIntegerFromList(List<Integer> options, int offset) {
		int input = 0;
		boolean ok;
		do {
			try {
				input = Integer.parseInt(scanner.nextLine());
				input += offset;
				ok = true;
			} catch (NumberFormatException e) {
				ok = false;
			}
			if (!ok || !options.contains(input)) { // ok must be true and input must be in the options list.
				printLine("The value must be in the options.");
			}
		} while (!ok || !options.contains(input));
		return input;
	}

	private Coordinates askCoordinates(List<Coordinates> reachableCoordinates) {
		Coordinates coordinates;
		do {
			printLine("Enter Row coordinate 1-" + getModelRep().getGameMapRep().getNumOfRows());
			int x = askInteger(1, getModelRep().getGameMapRep().getNumOfRows());
			printLine("Enter Column coordinate 1-" + getModelRep().getGameMapRep().getNumOfColumns());
			int y = askInteger(1, getModelRep().getGameMapRep().getNumOfColumns());
			coordinates = new Coordinates(x - 1, y - 1);
			CLIPrinter.moveCursorUP(4);
			CLIPrinter.cleanConsole();
		} while (!reachableCoordinates.contains(coordinates));

		return coordinates;
	}

	/**
	 * Ask the user a boolean.
	 *
	 * @return the boolean chosen by the user.
	 */
	private boolean askBoolean() {
		String input = "";
		while (!(input.equals("n") || input.equals("y") || input.equals("yes") || input.equals("no"))) {
			input = scanner.nextLine().toLowerCase();
			if (!(input.equals("n") || input.equals("y") || input.equals("yes") || input.equals("no")))
				printLine("Please write \"y\" or \"n\".");
		}
		return input.equals("y") || input.equals("yes");
	}
}


