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
package com.aionemu.gameserver.utils;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.concurrent.PriorityThreadFactory;

/**
 * @author Rolandas
 */
public class WorkStealThreadFactory extends PriorityThreadFactory implements ForkJoinWorkerThreadFactory {

	public WorkStealThreadFactory(String namePrefix) {
		super(namePrefix, Thread.NORM_PRIORITY);
	}

	public void setDefaultPool(ForkJoinPool pool) {
		// In Java 8 and jsr166 common pool exists, not in Java 7
		/*
		if (pool == null)
			pool = ForkJoinPool.commonPool();
		*/
		super.setDefaultPool(pool);
	}

	public ForkJoinPool getDefaultPool() {
		return (ForkJoinPool) super.getDefaultPool();
	}

	@Override
	public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
		return new WorkStealThread(pool);
	}

	private static class WorkStealThread extends ForkJoinWorkerThread {

		private static final Logger log = LoggerFactory.getLogger(WorkStealThread.class);

		public WorkStealThread(ForkJoinPool pool) {
			super(pool);
		}

		@Override
		protected void onStart() {
			super.onStart();
		}

		protected void onTermination(Throwable exception) {
			if (exception != null)
				log.error("Error - Thread: " + this.getName() + " terminated abnormaly: " + exception);
			super.onTermination(exception);
		}
	}

}
