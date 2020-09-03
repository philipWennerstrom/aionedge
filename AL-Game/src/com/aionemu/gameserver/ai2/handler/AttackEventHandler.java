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
package com.aionemu.gameserver.ai2.handler;



import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.joda.time.Duration;

import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.manager.AttackManager;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.controllers.CreatureController;
import com.aionemu.gameserver.controllers.PlayerController;
import com.aionemu.gameserver.controllers.movement.NpcMoveController;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.gametime.DateTimeUtil;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * @author ATracer
 */
public class AttackEventHandler {

	/**
	 * @param npcAI
	 * @param creature
	 */
	public static void onAttack(NpcAI2 npcAI, Creature creature) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onAttack");
		}
		if (creature == null || creature.getLifeStats().isAlreadyDead()) {
			return;
		}
		// TODO lock or better switch
		if (npcAI.isInState(AIState.RETURNING)) {
			npcAI.getOwner().getMoveController().abortMove();
			npcAI.setStateIfNot(AIState.IDLE);
			npcAI.onGeneralEvent(AIEventType.NOT_AT_HOME);
			return;
		}
		if (!npcAI.canThink()) {
			return;
		}
		if (npcAI.isInState(AIState.WALKING)) {
			WalkManager.stopWalking(npcAI);
		}
		npcAI.getOwner().getGameStats().renewLastAttackedTime();
		
		if (!npcAI.isInState(AIState.FIGHT)) {
			npcAI.setStateIfNot(AIState.FIGHT);
			if (npcAI.isLogging()) {
				AI2Logger.info(npcAI, "onAttack() -> startAttacking");
			}
			npcAI.setSubStateIfNot(AISubState.NONE);
		
			npcAI.getOwner().setTarget(creature);
			
			AttackManager.startAttacking(npcAI);
			if (npcAI.poll(AIQuestion.CAN_SHOUT))
				ShoutEventHandler.onAttackBegin(npcAI, (Creature) npcAI.getOwner().getTarget());
		}else {
			if (npcAI.getOwner().getTarget() != null && npcAI.getOwner().getTarget() instanceof Player) {
				Player player = (Player) npcAI.getOwner().getTarget();
				
				PlayerController targetController = (PlayerController) npcAI.getOwner().getTarget().getController();
				Date playerAttackedAt = new Date(targetController.getLastAttackTime());
				Duration attackedFrom = new Duration(targetController.getLastAttackedTime(), System.currentTimeMillis());
				long attackedAtFromNow = attackedFrom.getStandardSeconds();

				if (npcAI.getOwner() instanceof Npc) {
					NpcMoveController npcMc = npcAI.getOwner().getMoveController();
					Duration du = new Duration(npcMc.getLastMoveUpdate(), System.currentTimeMillis());
					long secFromLastNpcMove = du.getStandardSeconds();
					
					if (secFromLastNpcMove > 4 && attackedAtFromNow > 5) {
						npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
						
						Util.printSection( "Fixing Geo bug exploit - Player: "+ player.getAcountName()+ "-- > " + " Npc: "+npcAI.getOwner().getName());
					}
				}
			}
		}
	}

	/**
	 * @param npcAI
	 */
	public static void onForcedAttack(NpcAI2 npcAI) {
		onAttack(npcAI, (Creature) npcAI.getOwner().getTarget());
	}

	/**
	 * @param npcAI
	 */
	public static void onAttackComplete(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onAttackComplete: " + npcAI.getOwner().getGameStats().getLastAttackTimeDelta());
		}
		npcAI.getOwner().getGameStats().renewLastAttackTime();
		AttackManager.scheduleNextAttack(npcAI);
	}

	/**
	 * @param npcAI
	 */
	public static void onFinishAttack(NpcAI2 npcAI) {
		if (!npcAI.canThink()) {
			return;
		}
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onFinishAttack");
		}
		Npc npc = npcAI.getOwner();
		EmoteManager.emoteStopAttacking(npc);
		npc.getLifeStats().startResting();
		npc.getAggroList().clear();
		if (npcAI.poll(AIQuestion.CAN_SHOUT))
			ShoutEventHandler.onAttackEnd(npcAI);
		npc.setTarget(null);
		npc.setSkillNumber(0);
	}
}
