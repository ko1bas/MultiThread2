
package org.kolbas.threads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Колбсов П.А.
 *
 */

/**
 * Загружает строки из текстового файла в блокирующуюся очередь в пуле потоков.
 * Пул потоков можно выбрать как кэширующий, так и фиксированный.
 */

public class FileReaderThreadPool extends Thread {
	private int countThread;
	private ExecutorService executor;
	private String[] files;
	private int startIndex;
	private BlockingQueue<String> queue;
	private boolean isClose;

	
	/**
	 * 
	 * Загружает строки из текстового файла в блокирующуюся очередь в пуле потоков 
	 * с фиксированным количеством одновременно выполняющихся потоков.
	 * @param args
	 *            массив путей к текстовым файлам.
	 *            
	 * @param start
	 *            стартовый индекс в массиве путей к текстовым файлам.
	 *            
	 * @param countThread
	 *            количество одновременно выполняющихся потоков.
	 *            
	 * @param queue
	 *            общая очередь, в которую потоки будут помещать строки, считанные из файла.           
	 *          
	 */
	public FileReaderThreadPool(String[] args, int start, int countThread,
			BlockingQueue<String> queue) {
		this.files = args;
		this.queue = queue;
		this.startIndex = start;
		this.countThread = countThread;
		this.executor = Executors.newFixedThreadPool(this.countThread);
		this.isClose =false;
	}

	/**
	 *       
	 *   @return признак, выполнились ли все потоки или нет.
	 */
	public boolean isTerminated()
	{
		return isClose;
	}

	/**
	 * 
	 * Поток завершается, когда все потоки в пуле завершены.
	 * По реализации мне не совсем нравится. Нормально ли?
	 *
	 */
	@Override
	public void run() {
		isClose =false;
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		for (int i = startIndex; i < files.length; i++) {
			try {
				tasks.add(Executors.callable(new FileReaderThread(files[i], queue)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			executor.invokeAll(tasks);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			executor.shutdown();
			executor.shutdownNow();
			isClose=true;	
		}		
	}
		
}
