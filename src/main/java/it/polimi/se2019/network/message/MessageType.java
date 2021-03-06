package it.polimi.se2019.network.message;

/**
 * Enum containing all the possible message types.
 * @author Desno365
 * @author Marchingegno
 * @author MarcerAndrea
 */
public enum MessageType {

	// MessageTypes for Match initialization.
	NICKNAME,
	WAITING_PLAYERS,
	TIMER_FOR_START,
	GAME_CONFIG,
	CONNECTION,

	// MessageTypes between VirtualView <=> RemoteView
	SPAWN,
	UPDATE_REPS,
	ACTION,
	MOVE,
	GRAB_WEAPON,
	SWAP_WEAPON,
	GRAB_AMMO,
	RELOAD,
	ACTIVATE_WEAPON,
	WEAPON,
	ACTIVATE_ON_TURN_POWERUP,
	ACTIVATE_ON_DAMAGE_POWERUP,
	POWERUP,
	PAYMENT,
	END_TURN,
	END_GAME,
}