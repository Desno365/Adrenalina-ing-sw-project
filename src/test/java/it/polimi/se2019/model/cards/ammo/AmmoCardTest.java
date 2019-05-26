package it.polimi.se2019.model.cards.ammo;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AmmoCardTest {

	List<AmmoType> ammoInTheCard;

	@Before
	public void setUp() {
		ammoInTheCard = new ArrayList<>();
		ammoInTheCard.add(AmmoType.RED_AMMO);
		ammoInTheCard.add(AmmoType.BLUE_AMMO);
	}


	@Test
	public void hasPowerup_standardInitialization_correctOutput() {
		AmmoCard ammoCardWithPowerup = new AmmoCard(ammoInTheCard, true, "r_b_p");
		AmmoCard ammoCardWithoutPowerup = new AmmoCard(ammoInTheCard, false, "r_b_p");
		assertTrue(ammoCardWithPowerup.hasPowerup());
		assertFalse(ammoCardWithoutPowerup.hasPowerup());
	}

	@Test
	public void getAmmo_standardInitialization_correctOutput() {
		AmmoCard ammoCard = new AmmoCard(ammoInTheCard, true, "r_b_p");
		assertEquals(ammoInTheCard, ammoCard.getAmmo());
	}

	@Test
	public void getRep_standardInitialization_correctRepresentation() {
		AmmoCard ammoCard = new AmmoCard(ammoInTheCard, true, "r_b_p");
		AmmoCardRep ammoCardRep = (AmmoCardRep) ammoCard.getRep();

		assertEquals(ammoCard.hasPowerup(), ammoCardRep.hasPowerup());
		assertEquals(ammoCard.getAmmo(), ammoCardRep.getAmmo());
	}
}