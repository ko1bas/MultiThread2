/**
 * 
 */
package org.kolbas.threads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.kolbas.common.interfaces.StringConvertable;
import org.kolbas.storage.MapStorage;
import org.kolbas.storage.Storageable;

import sun.nio.cs.ext.ISCII91;

/**
 * @author Колбсов П.А.
 *
 */


/**
 * Забирает строки из блокирующейся очереди в пуле потоков.
 * Пул потоков можно выбрать как кэширующий, так и фиксированный.
 * Удаленные слова из строки помещает в хранилище. 
 */

public class QueueReaderThreadPool extends Thread {
	private int countThread;
	private ExecutorService executor;
	private BlockingQueue<String> queue;
	private boolean isClose;
	private FileReaderThreadPool pool;
	private Storageable storage;
	private Class<?> plugin;

	/**
	 * Забирает строки из блокирующейся очереди в пуле потоков.
	 * Удаленные слова из строки помещает в хранилище. 
	 * 
	 * @param countThread
	 *            количество выполняющихся потоков.
	 *            
	 * @param queue
	 *            общая очередь, из которой потоки будут брать строки.          
	 *            
	 * @param map
	 *            хранилище, куда помещаются удаленные слова.
	 *            
	 * @param pool
	 *            пул потоков-"писателей в очередь". Нужен для того чтобы понять считались все файлы или нет.
	 *            
	 * @param plugin
	 *            реализация Storageable.getDeletedString, полученная из jar-файла.            
	 *          
	 */
	
	public QueueReaderThreadPool(int countThread, BlockingQueue<String> queue,
			Storageable map, FileReaderThreadPool pool,
			Class<?> plugin) {
		this.queue = queue;
		this.countThread = countThread;
		this.executor = Executors.newFixedThreadPool(this.countThread);
		this.isClose = false;
		this.pool = pool;
		this.storage = map;
		this.plugin = plugin;
	}

	/**
	 * 
	 * Поток завершается, когда все потоки в пуле завершены.
	 * По реализации мне не совсем нравится. Нормально ли?
	 *
	 */
	@Override
	public void run() {
		isClose = false;
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		for (int i = 0; i < countThread; i++) {
			try {
				tasks.add(Executors.callable(new QueueReaderThread(queue,
						plugin, storage, pool))); 
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

		}
		try {
			executor.invokeAll(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();
			executor.shutdownNow();
			isClose = true;
		}
	}
	
	/**
	 *  
	 *   @return признак, выполнились ли все потоки или нет.
	 */
	public boolean isTerminated()
	{
		return isClose;
	}
}
