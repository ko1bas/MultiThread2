package org.kolbas.threads;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import org.kolbas.files.FileLoader;

/**
 * @author Колбсов П.А.
 *
 */

/**
 * Загружает строки из текстового файла в блокирующуюся очередь в потоке. Через
 * Callable<Boolean> возвращает true, когда чтение файла завершено.
 */
public class FileReaderThread extends Thread implements Callable<Boolean> {

	private FileLoader loader;
	private BlockingQueue<String> queue;
	private boolean isClose;

	/**
	 * Инициализирует внутренние переменные. Что писать непонятно.
	 * Вроде ясно и так. Подскажите.
	 * 
	 */
	public FileReaderThread(FileLoader loader, BlockingQueue<String> queue) {

		this.loader = loader;
		this.queue = queue;
		this.isClose = false;
	}

	/**
	 * Инициализирует внутренние переменные. Что писать непонятно.
	 * Вроде ясно и так. Подскажите.
	 * 
	 */
	FileReaderThread(String fileName, BlockingQueue<String> queue)
			throws FileNotFoundException, IOException {

		this.loader = new FileLoader(fileName);
		this.queue = queue;
		this.isClose = false;
	}

	
	/**
	 * Что писать непонятно.
	 * Вроде ясно и так. Подскажите.
	 * По реализации мне не совсем нравится. Нормально ли?
	 * 
	 */
	@Override
	public void run() {
		String buf = "";
		try {
			while (true) {
				buf = loader.next();
				if (buf == null)
					break;
				try {
					queue.put(buf);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} finally {
			isClose = true;
			try {
				loader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Реализация интерфейса Callable<Boolean>.
	 * @return true, если чтение файла было завершено. Может быть и с ошибкой.
	 * 
	 */
	public Boolean call() throws Exception {

		return isClose;
	}

}
