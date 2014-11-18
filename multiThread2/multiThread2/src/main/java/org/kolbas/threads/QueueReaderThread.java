package org.kolbas.threads;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import org.kolbas.common.interfaces.StringConvertable;
import org.kolbas.storage.Storageable;



/**
 * @author Колбсов П.А.
 *
 */

/**
 * 
 * Забирает строки из блокирующейся очереди, получает из строки  удаленные слова с помощью загруженного из jar-файла плагина
 * и помещает их в хранилище.
 * Через Callable<Boolean> возвращает true, когда чтение всех файлов в пуле  завершено и очередь пуста.
 * Если чтение всех файлов в пуле  НЕ завершено, НО очередь пуста - засыпает на SLEEP_TIMES.
 * 
 */
public class QueueReaderThread extends Thread implements Callable<Boolean>{

	private BlockingQueue<String> queue;

	private StringConvertable plugin;

	private Storageable storage;

	private FileReaderThreadPool pool;

	private final int SLEEP_TIMES = 50; // выбрал наобум :). 
	
	private boolean isClose;

	
	/**
	 * 
	 * Инициализирует внутренние переменные.
	 * 
	 */
	public QueueReaderThread(BlockingQueue<String> queue, Class<?> plugin,
			Storageable map, FileReaderThreadPool pool)
			throws InstantiationException, IllegalAccessException {

		this.queue = queue;
		this.plugin = (StringConvertable) plugin.newInstance();
		this.storage = map;
		this.pool = pool;
		this.isClose = false;
	}

	/**
	 * 
	 * Читает строки из очереди и помещает их в хранилище.
	 * NoSuchElementException, может возникнуть, если очередь пуста.
	 * Если все файлы прочитаны -то завершаем поток, иначе засыпаем на SLEEP_TIMES.
	 * По реализации мне не совсем нравится. Нормально ли?
	 * 
	 * 
	 */
	public void run() {
		isClose = false;
		while (true) {
			ArrayList<String> array;
			try {
				String res = queue.remove();
				array = plugin.getDeletedStrings(res);
				for (String str : array) {
					storage.put(str);
				}
			} catch (NoSuchElementException IOE) {
				if (!pool.isTerminated())
					try {
						this.sleep(SLEEP_TIMES);
						//System.out.println("sleep");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				else
					break;
			}
		}
		this.isClose = true;
	}

	/**  
	 *   @return признак, завершен поток или нет.
	 */
	public Boolean call() throws Exception {
		return isClose;
	}
}
