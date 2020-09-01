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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package ai.worlds.tiamaranta.ativasCristalline;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;

/**
 * @author Ritsu
 */


@AIName("topazKomad")
public class TopazKomadAI2 extends AggressiveNpcAI2
{
	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		int lifetime = (getNpcId() == 282709 ? 20000 : 10000);
		toDespawn(lifetime);
	}

	private void toDespawn(int delay)
	{
		ThreadPoolManager.getInstance().schedule(new Runnable() 
		{
			@Override
			public void run()
			{
				AI2Actions.deleteOwner(TopazKomadAI2.this);
			}
		}, delay);
	}
}