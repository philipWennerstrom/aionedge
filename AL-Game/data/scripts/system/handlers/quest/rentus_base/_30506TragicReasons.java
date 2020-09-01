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
package quest.rentus_base;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;


/**
 * @author Ritsu
 *
 */
public class _30506TragicReasons extends QuestHandler
{
	
	private final static int questId=30506;

	public _30506TragicReasons(){
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(799544).addOnQuestStart(questId);
		qe.registerQuestNpc(799544).addOnTalkEvent(questId);
		qe.registerQuestNpc(799549).addOnTalkEvent(questId);

	}
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player=env.getPlayer();
		QuestState qs=player.getQuestStateList().getQuestState(questId);
		int targetId=env.getTargetId();
		QuestDialog dialog=env.getDialog();
		if(qs==null || qs.getStatus() ==QuestStatus.NONE || qs.canRepeat())
		{
			switch(targetId)
			{
				case 799544:
				{
					switch(dialog)
					{
						case START_DIALOG:
							return sendQuestDialog(env,4762);
						default:
							return sendQuestStartDialog(env);
					}
				}
			}
		}
		else if(qs != null && qs.getStatus() ==QuestStatus.START)
		{

			switch(targetId)
			{
				case 799544:
				{
					switch(dialog)
					{
						case START_DIALOG:
							return sendQuestDialog(env,1011);
						case CHECK_COLLECTED_ITEMS_SIMPLE:
							return checkQuestItemsSimple(env, 0, 0, true, 10000, 0, 0);
					}
				}
			}
		}
		else if(qs != null && qs.getStatus() == QuestStatus.REWARD)
		{
			switch(targetId)
			{
				case 799549:
					switch(dialog)
					{
						case USE_OBJECT:
							return sendQuestDialog(env, 10002);
						case SELECT_REWARD:
							return sendQuestDialog(env, 5);
						default :
							return sendQuestEndDialog(env);
					}
			}
		}
		return false;
	}
}
