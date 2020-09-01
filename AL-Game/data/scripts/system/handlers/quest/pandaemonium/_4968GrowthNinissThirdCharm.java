/*
 * This file is part of aion-lightning <aion-lightning.org>.
 *
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package quest.pandaemonium;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * Talk with Maochinicherk (798068). Bring the Glowing Aether Paper (186000092) and Kinah (70000) to Ninis (798385).
 * 
 * @author undertrey
 * @modified vlog
 */
public class _4968GrowthNinissThirdCharm extends QuestHandler {

	private final static int questId = 4968;

	public _4968GrowthNinissThirdCharm() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(798385).addOnQuestStart(questId);
		qe.registerQuestNpc(798385).addOnTalkEvent(questId);
		qe.registerQuestNpc(798068).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798385) { // Ninis
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 798068: { // Maochinicherk
					switch (env.getDialog()) {
						case START_DIALOG:
							if (var == 0)
								return sendQuestDialog(env, 1352);
						case STEP_TO_1:
							return defaultCloseDialog(env, 0, 1, 182207138, 1, 0, 0); // 1
					}
					break;
				}
				case 798385: // Ninis
					switch (env.getDialog()) {
						case START_DIALOG:
							if (var == 1) {
								removeQuestItem(env, 182207138, 1);
								return sendQuestDialog(env, 2375);
							}
						case CHECK_COLLECTED_ITEMS:
							long itemAmount = player.getInventory().getItemCountByItemId(186000092);
							if (var == 1 && player.getInventory().tryDecreaseKinah(70000) && itemAmount >= 1) {
								removeQuestItem(env, 186000092, 1);
								changeQuestStep(env, 1, 1, true); // reward
								return sendQuestDialog(env, 5);
							}
							else
								return sendQuestDialog(env, 2716);
						case FINISH_DIALOG:
							return defaultCloseDialog(env, 1, 1);
					}
					break;
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798385) { // Ninis
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
