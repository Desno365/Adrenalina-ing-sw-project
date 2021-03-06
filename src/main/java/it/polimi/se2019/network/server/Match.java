package it.polimi.se2019.network.server;

import it.polimi.se2019.controller.Controller;
import it.polimi.se2019.network.message.GameConfigMessage;
import it.polimi.se2019.network.message.Message;
import it.polimi.se2019.network.message.MessageSubtype;
import it.polimi.se2019.network.message.MessageType;
import it.polimi.se2019.utils.GameConstants;
import it.polimi.se2019.utils.SingleTimer;
import it.polimi.se2019.utils.Utils;
import it.polimi.se2019.view.server.VirtualView;

import java.util.*;

/**
 * Represents a single Match with all his participants.
 * @author Desno365
 */
class Match {

	private final int numberOfParticipants;
	private ArrayList<AbstractConnectionToClient> participants;
	private ArrayList<AbstractConnectionToClient> disconnectedParticipants = new ArrayList<>();
	private HashMap<AbstractConnectionToClient, VirtualView> virtualViews = new HashMap<>();
	private boolean matchStarted = false;
	private SingleTimer singleTimer = new SingleTimer();
	private Controller controller;

	// Game config attributes.
	private HashMap<AbstractConnectionToClient, Integer> skullsChosen = new HashMap<>();
	private HashMap<AbstractConnectionToClient, Integer> mapChosen = new HashMap<>();
	private int numberOfAnswers = 0;

	/**
	 * Create a new match with the specified clients.
	 *
	 * @param participants a map that contains all the clients for this match and their nicknames.
	 */
	Match(List<AbstractConnectionToClient> participants) {
		numberOfParticipants = participants.size();
		if (numberOfParticipants < GameConstants.MIN_PLAYERS || numberOfParticipants > GameConstants.MAX_PLAYERS)
			throw new IllegalArgumentException("The number of participants for this match (" + numberOfParticipants + ") is not valid.");
		this.participants = new ArrayList<>(participants);
	}


	// ####################################
	// PUBLIC METHODS
	// ####################################

	/**
	 * Send game config request messages to the clients, asking skulls and map type.
	 */
	void requestMatchConfig() {
		if (isMatchStarted())
			return;

		for (AbstractConnectionToClient client : participants)
			client.sendMessage(new Message(MessageType.GAME_CONFIG, MessageSubtype.REQUEST));

		Utils.logInfo("Starting timer for Match answer.");
		singleTimer.start(this::initializeGame, (Utils.getServerConfig()).getTurnTimeLimitMs());
	}

	/**
	 * Add the vote of a client for game configurations.
	 *
	 * @param client   the client tha made the vote.
	 * @param skulls   skulls voted.
	 * @param mapIndex map voted.
	 */
	void addConfigVote(AbstractConnectionToClient client, int skulls, int mapIndex) {
		if (isMatchStarted()) {
			Utils.logInfo("\tMatch already started, GameConfigMessage ignored.");
			return;
		}

		if (participants.contains(client) && !skullsChosen.containsKey(client) && !mapChosen.containsKey(client)) { // Check if the client is in the Match and if he didn't already vote.
			Utils.logInfo("\tAdding game config vote with skulls " + skulls + ", map index " + mapIndex + ".");

			skullsChosen.put(client, skulls);
			mapChosen.put(client, mapIndex);

			numberOfAnswers++;
			if (numberOfAnswers >= numberOfParticipants) {
				Utils.logInfo("\t\tAll participants sent their votes. Initializing the game.");
				initializeGame();
			}
		}
	}

	/**
	 * Returns a list with all the participants of this match.
	 *
	 * @return a list with all the participants of this match.
	 */
	List<AbstractConnectionToClient> getParticipants() {
		return new ArrayList<>(participants);
	}

	/**
	 * Returns the VirtualView associated to the client, or null if this match doesn't have the VirtualView of the client.
	 *
	 * @param client the client.
	 * @return the VirtualView associated to the client.
	 */
	VirtualView getVirtualViewOfClient(AbstractConnectionToClient client) {
		return virtualViews.get(client);
	}

	/**
	 * Returns true if the match started.
	 *
	 * @return true if the match started.
	 */
	boolean isMatchStarted() {
		return matchStarted;
	}

	/**
	 * Returns true if this match finished and is ready to be dismantled.
	 *
	 * @return true if this match finished and is ready to be dismantled.
	 */
	boolean isMatchFinished() {
		return matchStarted && controller.isGameEnded();
	}


	// ####################################
	// PUBLIC METHODS TO HANDLE DISCONNECTION
	// ####################################

	/**
	 * Returns a list with all the disconnected participants of this match.
	 *
	 * @return a list with all the disconnected participants of this match.
	 */
	List<AbstractConnectionToClient> getDisconnectedParticipants() {
		return new ArrayList<>(disconnectedParticipants);
	}

	/**
	 * Sets a participant of this match as disconnected while also forwarding this information to the VirtualView.
	 * Note: should be called only if match already started.
	 *
	 * @param client the disconnected client.
	 */
	void setParticipantAsDisconnected(AbstractConnectionToClient client) {
		if (!participants.contains(client)) {
			Utils.logError("Participant can't be set as disconnected since it doesn't exist in this Match.", new IllegalStateException());
			return;
		}

		// Add client to the disconnected participants list.
		disconnectedParticipants.add(client);

		Utils.logInfo("Match -> setParticipantAsDisconnected(): reported disconnection of player \"" + client.getNickname() + "\" to the Match. In this match there are " + disconnectedParticipants.size() + " players disconnected.");

		// Forward disconnection information to the VirtualView.
		VirtualView virtualView = getVirtualViewOfClient(client);
		if (virtualView == null)
			Utils.logError("The VirtualView should always be set if the match is started.", new IllegalStateException());
		else
			virtualView.onClientDisconnected();
	}

	/**
	 * Sets a participant of this match as reconnected while also forwarding this information to the VirtualView.
	 *
	 * @param client the reconnected client.
	 */
	void setParticipantAsReconnected(AbstractConnectionToClient client) {
		Optional<AbstractConnectionToClient> oldClient = disconnectedParticipants.stream().filter(p -> p.getNickname().equals(client.getNickname())).findFirst();
		if (oldClient.isPresent()) {
			// Remove client from the disconnected participants list.
			disconnectedParticipants.remove(oldClient.get());

			Utils.logInfo("Match -> setParticipantAsReconnected(): reported reconnection of player \"" + client.getNickname() + "\" to the Match. In this match there are " + disconnectedParticipants.size() + " players disconnected.");

			// Update client in the participants list.
			participants.removeIf(participant -> participant.getNickname().equals(client.getNickname()));
			participants.add(client);

			// Update client in the virtualviews hashmap.
			VirtualView virtualView = getVirtualViewOfClient(oldClient.get());
			virtualViews.remove(oldClient.get());
			virtualViews.put(client, virtualView);

			// Forward reconnection information to the VirtualView.
			virtualView.onClientReconnected(client);
		}
	}


	// ####################################
	// PRIVATE METHODS
	// ####################################

	/**
	 * Start the match.
	 */
	private void initializeGame() {
		Utils.logInfo("Cancelling timer for Match answer.");
		singleTimer.cancel();

		// Find votes.
		int skulls = findVotedNumberOfSkulls();
		GameConstants.MapType mapType = findVotedMap();
		Utils.logInfo("Match -> initializeGame(): initializing a new game with skulls: " + skulls + ", mapName: \"" + mapType.getMapName() + "\".");

		// Send messages with votes.
		sendVotesResultMessages(skulls, mapType);

		// Create virtualViews.
		for (AbstractConnectionToClient client : participants) {
			Utils.logInfo("Match => initializeGame(): Added Virtual View to " + client.getNickname());
			VirtualView virtualView = new VirtualView(client);
			virtualViews.put(client, virtualView);
		}

		// Create Controller.
		controller = new Controller(mapType, virtualViews.values(), skulls);
		controller.startGame();

		matchStarted = true;
	}

	/**
	 * Calculates the average of voted skulls.
	 *
	 * @return the average of voted skulls.
	 */
	private int findVotedNumberOfSkulls() {
		// If there are no votes gives a default value.
		if (skullsChosen.size() == 0)
			return GameConstants.MIN_SKULLS;

		// Calculates the average.
		float average = 0f;
		for (Integer votedSkulls : skullsChosen.values())
			average += votedSkulls;
		return Math.round(average / skullsChosen.values().size());
	}

	/**
	 * Find the most voted map, if votes are tied it chooses the map with the smaller ordinal.
	 *
	 * @return the map type of the most voted map.
	 */
	private GameConstants.MapType findVotedMap() {
		// Create array of votes.
		int[] votes = new int[GameConstants.MapType.values().length];

		// Add votes.
		for (Integer votedMap : mapChosen.values())
			votes[votedMap]++;

		// search for max.
		int indexOfMax = 0;
		for (int i = 1; i < votes.length; i++) {
			if (votes[i] > votes[indexOfMax])
				indexOfMax = i;
		}

		// Return corresponding map.
		return GameConstants.MapType.values()[indexOfMax];
	}

	/**
	 * Send a message with the result of the poll.
	 *
	 * @param skulls  average number of skulls voted.
	 * @param mapType most voted map type.
	 */
	private void sendVotesResultMessages(int skulls, GameConstants.MapType mapType) {
		for (AbstractConnectionToClient client : participants) {
			GameConfigMessage gameConfigMessage = new GameConfigMessage(MessageSubtype.INFO);
			gameConfigMessage.setSkulls(skulls);
			gameConfigMessage.setMapIndex(mapType.ordinal());
			client.sendMessage(gameConfigMessage);
		}
	}

}
