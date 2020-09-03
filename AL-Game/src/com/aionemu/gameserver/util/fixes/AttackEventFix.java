/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.util.fixes;

import org.joda.time.Duration;

import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.Util;

/**
 * @author wennerstrom
 *
 */
public class AttackEventFix {

	public static void checkGeoDataBug(NpcAI2 npcAI, Creature attacker) {
		Creature attacked = npcAI.getOwner();
		VisibleObject target = attacked.getTarget();

		if (target instanceof Player) {
			playerTarget(npcAI, target);
		}

	}

	/**
	 * @param npcAI
	 * @param attacked
	 * @param target
	 */
	public static void playerTarget(NpcAI2 npcAI, VisibleObject target) {
		Creature attacked = npcAI.getOwner();
		Player player = (Player) target;
		if (attacked instanceof Npc) {
			Npc nAttacked = ((Npc) attacked);
			if (isFreezing(player, nAttacked)) {
				giveUpOnTarget(npcAI, attacked, player);
			}
		}
	}

	/**
	 * Verifica quanto tempo o npc esta parado e o target nao recebe ataque
	 * @param attackedAtFromNow
	 * @param secFromLastNpcMove
	 * @return
	 */
	private static boolean isFreezing(Player target, Npc npc) {
	  long attackedAtFromNow =  new Duration(target.getController().getLastAttackedTime(), System.currentTimeMillis()).getStandardSeconds();
		long secFromLastNpcMove =  new Duration(npc.getMoveController().getLastMoveUpdate(), System.currentTimeMillis()).getStandardSeconds();
		return secFromLastNpcMove > 2 && attackedAtFromNow > 6;
	}

	/**
	 * Envia o comando para AI desistir do Target
	 * 
	 * @param npcAI
	 * @param attacked
	 * @param player
	 */
	private static void giveUpOnTarget(NpcAI2 npcAI, Creature attacked, Player player) {
		npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
		Util.printSection("Fixing Geo bug exploit - Player: " + player.getAcountName() + " < -- > " + " Npc: " + attacked.getName());
	}
}