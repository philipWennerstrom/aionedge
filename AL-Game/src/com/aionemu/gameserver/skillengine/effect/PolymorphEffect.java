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
package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUSTOM_SETTINGS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_IMMOBILIZE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @author ATracer
 * @modified Cheatkiller
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolymorphEffect")
public class PolymorphEffect extends TransformEffect {
	
	private int tribeType = 0;
	
	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
		if(effect.getEffected() instanceof Player) {
			final Player player = (Player) effect.getEffected();
			/**
			 * Only for defensive_cannon & aircraft gun
			 * Temporarily until find why (Polymorph reserved_6?)
			 */
			if(effect.getSkillTemplate().getName().equals("Board Artillery") || effect.getSkillTemplate().getName().equals("Get on Antiaircraft Gun")) {
				player.getMoveController().abortMove();
				player.getEffectController().setAbnormal(AbnormalState.ROOT.getId());
				effect.setAbnormal(AbnormalState.ROOT.getId());
				//PacketSendUtility.broadcastPacketAndReceive(player, new SM_TARGET_IMMOBILIZE(player));
			}
		player.getKnownList().doOnAllNpcs(new Visitor<Npc>() {
			@Override
			 public void visit(Npc npc) {
				if(npc.isFriendTo(player))
					tribeType = 38;
				else if(npc.isAggressiveTo(player))
					tribeType = 8;
				else if(npc.isNoneRelation(player))
					tribeType = 2;
				else
					tribeType = npc.getNpcType().getId();
					PacketSendUtility.sendPacket(player, new SM_CUSTOM_SETTINGS(npc.getObjectId(), 0, tribeType, 0));
			 }
		 });
	  }
	}
		  
	

	@Override
	public void startEffect(Effect effect) {
		if (model > 0) {
			Creature effected = effect.getEffected();
			NpcTemplate template = DataManager.NPC_DATA.getNpcTemplate(model);
			if (template != null)
				effected.getTransformModel().setTribe(template.getTribe(), false);
		}
		super.startEffect(effect, AbnormalState.NOFLY);
	}

	@Override
	public void endEffect(Effect effect) {
		effect.getEffected().getTransformModel().setActive(false);
		if(effect.getEffected() instanceof Player) {
			final Player player = (Player) effect.getEffected();
			if(effect.getSkillTemplate().getName().equals("Board Artillery"))
				player.getEffectController().unsetAbnormal(AbnormalState.ROOT.getId());
			player.getKnownList().doOnAllNpcs(new Visitor<Npc>() {
				@Override
				 public void visit(Npc npc) {
					if(npc.isFriendTo(player))
						tribeType = 38;
					else if(npc.isAggressiveTo(player))
						tribeType = 8;
					else if(npc.isNoneRelation(player))
						tribeType = 2;
					else
						tribeType = npc.getNpcType().getId();
						PacketSendUtility.sendPacket(player, new SM_CUSTOM_SETTINGS(npc.getObjectId(), 0, tribeType, 0));
						player.getTransformModel().setTribe(null, false);
				  }
			  });
		  }
		 super.endEffect(effect, AbnormalState.NOFLY);
		}
  }