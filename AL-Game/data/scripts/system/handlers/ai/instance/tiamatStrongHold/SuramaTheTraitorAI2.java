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
package ai.instance.tiamatStrongHold;

import ai.GeneralNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.NpcType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;


/**
 * @author Cheatkiller
 *
 */
@AIName("suramathetraitor")
public class SuramaTheTraitorAI2 extends GeneralNpcAI2 {
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		moveToRaksha();
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		NpcShoutsService.getInstance().sendMsg(getOwner(), 390845, getOwner().getObjectId(), 0, 2000);
	}
	
	private void moveToRaksha() {
		setStateIfNot(AIState.WALKING);
  	getOwner().setState(1);
		getMoveController().moveToPoint(651, 1319, 487);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
		ThreadPoolManager.getInstance().schedule(new Runnable() {

		  @Override
		  public void run() {
		  	startDialog();
		  }
	  }, 10000);
	}
	
	
	private void startDialog() {
		final Npc raksha = getPosition().getWorldMapInstance().getNpc(219404);
		NpcShoutsService.getInstance().sendMsg(getOwner(), 390841, getOwner().getObjectId(), 0, 0);
		NpcShoutsService.getInstance().sendMsg(getOwner(), 390842, getOwner().getObjectId(), 0, 3000);
		NpcShoutsService.getInstance().sendMsg(raksha, 390843, raksha.getObjectId(), 0, 6000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

		  @Override
		  public void run() {
		  	WorldMapInstance instance = getPosition().getWorldMapInstance();
		  	raksha.setTarget(getOwner());
		  	SkillEngine.getInstance().getSkill(raksha, 20952, 60, getOwner()).useNoAnimationSkill();
		  	raksha.setNpcType(NpcType.ATTACKABLE);
		  	for (Player player : instance.getPlayersInside()) {
					if (MathUtil.isIn3dRange(player, raksha, 100)) {
						player.clearKnownlist();
						player.updateKnownlist();
					}
				}
		  }
	  }, 8000);
	}
}
