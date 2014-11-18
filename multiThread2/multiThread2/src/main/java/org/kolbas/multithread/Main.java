package org.kolbas.multithread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import java.util.concurrent.LinkedBlockingQueue;

import org.kolbas.common.interfaces.StringConvertable;
import org.kolbas.files.FileLoader;
import org.kolbas.files.JarLoader;
import org.kolbas.files.FileSaver;
import org.kolbas.storage.DatabaseStorage;
import org.kolbas.storage.MapStorage;
import org.kolbas.storage.Storageable;
import org.kolbas.threads.FileReaderThread;
import org.kolbas.threads.FileReaderThreadPool;
import org.kolbas.threads.QueueReaderThreadPool;

/**
 * 
 * Реализовать многопоточную обработку файлов предыдущего задания (т.е. в
 * приложение можно передать несколько файлов для обработки). Все удаленные
 * слова сохраняются в выходной файл, при этом в файле должны содержаться только
 * уникальные слова.
 * 
 * Нужно бы было сделать класс, который будет выполнять задание отдельно, но лень. 
 * Сделал все в статике.((
 * 
 */
public class Main {

	private static final int MAX_SIZE_FILE = 1024 * 1024 * 100; // макси
	private static ResourceBundle resource;

	private static int totalFileSize;
	private static BlockingQueue<String> queue;
	private static Storageable storage;
	private static FileReaderThreadPool poolFileReaders;
	private static QueueReaderThreadPool poolQueueReaders;

	/**
	 * 
	 * Используя хитрую эвристику, подбираются оптимальные параметры для решения
	 * задачи. Например учитываем кол-во процессоров, доступную память, совокупный размер файлов и т.д.
	 * 
	 */
	public static void initialize(String[] args, Class<?> plugin) {

		int queueCapasity = 10000; // хитрая эвристика detected.
		queue = new LinkedBlockingQueue<String>(queueCapasity);

		// смотрим на доступную оперативную память и выбираем реализацию
		// хранилища на mapax или БД.
		
		int mapCapasity = 100000;  // хитрая эвристика detected.
		if (totalFileSize < Long.MAX_VALUE)
			storage = new MapStorage(mapCapasity);
		else
			storage = new DatabaseStorage();

		int startArgs = 2; // в командной строке текстовые файлы начинаются с
						   // этого индекса.

		int countFileReaderThread = 4; // хитрая эвристика detected.
		poolFileReaders = new FileReaderThreadPool(args, startArgs,
				countFileReaderThread, queue);
	

		int countQueueReaderThread = Runtime.getRuntime().availableProcessors();  // хитрая эвристика detected.
		poolQueueReaders = new QueueReaderThreadPool(
				countQueueReaderThread, queue, storage, poolFileReaders, plugin);

	}

	/**
	 * 
	 * Выводит строку на экран.
	 * 
	 */
	private static final void p(String s) {
		System.out.println(s);
	}

	/**
	 * 
	 * Выводит подсказку по запуску в командной строке.
	 * 
	 */
	private static void printHelpStr() {

		p(resource.getString("HelpStr"));
		p(resource.getString("HelpStrDescription"));
	}

	/**
	 * 
	 * Проверяет валидность параметров командной строки. 
	 * java -jar MultiThread.jar <jar-file> <output txt-file> <input txt-file1> [<input txt-file2> ...]
	 * 
	 */
	private static final boolean isArgsValid(String[] args) throws IOException {
		boolean res = false;
		int fileSize = 0;
		switch (args.length) {
		case 0:
			printHelpStr();
			break;
		case 1:
			if (args[0].equalsIgnoreCase("-help")
					|| args[0].equalsIgnoreCase("/?"))
				printHelpStr();
			else {
				p(resource.getString("ErrCommandFailure"));
				printHelpStr();
			}
			break;
		case 2:
			p(resource.getString("ErrCommandFailure"));
			printHelpStr();
		default:
			int countError = 0;
			File f = new File(args[0]);
			if (!f.exists()) {
				p(resource.getString("FileIsNotExsist").replace("<file>",
						args[0]));
				countError++;
			} else if (!f.canRead()) {
				p(resource.getString("FileIsNotCanRead").replace("<file>",
						args[0]));
				countError++;
			} else if (f.length() == 0) {
				p(resource.getString("FileIsEmpty").replace("<file>", args[0]));
				countError++;
			} else if (!args[0].endsWith(".jar")) {
				p(resource.getString("FileBadExtension")
						.replace("<file>", args[0])
						.replace("<extension>", ".jar"));
				countError++;
			}

			f = new File(args[1]);
			//Здесь должна быть проверка на то что выходной файл можно создать.
			// if (!f.createNewFile()) {
			// p(resource.getString("FileIsNotCanWrite").replace("<file>",
			// args[1]));
			// countError++;
			// } else
			if (!args[1].endsWith(".txt")) {
				p(resource.getString("FileBadExtension")
						.replace("<file>", args[1])
						.replace("<extension>", ".txt"));
				countError++;
			}

			for (int i = 2; i < args.length; i++) {
				f = new File(args[i]);
				fileSize += f.length();

				if (!f.exists()) {
					p(resource.getString("FileIsNotExsist").replace("<file>",
							args[i]));
					countError++;
				} else if (!f.canRead()) {
					p(resource.getString("FileIsNotCanRead").replace("<file>",
							args[i]));
					countError++;
				} else if (f.length() == 0) {
					p(resource.getString("FileIsEmpty").replace("<file>",
							args[i]));
					countError++;
				} else if (f.length() > MAX_SIZE_FILE) {
					p(resource
							.getString("FileIsMaxSize")
							.replace("<file>", args[i])
							.replace("<maxsize>",
									Integer.toString(MAX_SIZE_FILE)));
					countError++;
				} else if (!args[i].endsWith(".txt")) {
					p(resource.getString("FileBadExtension")
							.replace("<file>", args[i])
							.replace("<extension>", ".txt"));
					countError++;
				}
			}
			if (countError > 0) {
				p(resource.getString("ErrCommandFailure"));
				printHelpStr();
				totalFileSize = 0;
			} else {
				totalFileSize = fileSize;
			}
			res = (countError == 0);
			break;
		}
		return res;
	}

	/**
	 * 
	 * Однопоточная реализация задания. В конце выводит время выполнения.
	 * 
	 */
	public static void OneThread(Class<?> plugin, String outFileName,
			String[] args) throws FileNotFoundException, IOException,
			InstantiationException, IllegalAccessException {

		long time = System.currentTimeMillis();
		int start = 2;
		FileLoader loader = new FileLoader(args, start);

		String buf = "";
		StringConvertable module = (StringConvertable) plugin.newInstance();

		int setCapacity = 10000;
		Set<String> set = new HashSet<String>(setCapacity);

		while (true) {
			buf = loader.next();
			if (buf == null)
				break;
			for (String str : module.getDeletedStrings(buf)) {
				set.add(str);
			}
		}
		loader.close();

		FileSaver saver = new FileSaver(outFileName);
		for (String key : set) {
			saver.write(key);
		}
		saver.close();

		System.out.println("One Thread.  Time: "
				+ (System.currentTimeMillis() - time));
	}


	/**
	 * 
	 * Многопоточная реализация задания. В конце выводит время выполнения.
	 * 
	 */
	public static void MultiThread(Class<?> plugin, String outFileName,
			String[] args) throws FileNotFoundException, IOException,
			InstantiationException, IllegalAccessException,
			InterruptedException {

		long time = System.currentTimeMillis();

		initialize(args, plugin);
		
		poolFileReaders.start();
		poolQueueReaders.start();

		poolFileReaders.join();
		poolQueueReaders.join();

		storage.saveToFile(outFileName);

		System.out.println("MultiThread. Time: "
				+ (System.currentTimeMillis() - time));
	}

	public static void main(String[] args) throws ClassNotFoundException,
			FileNotFoundException, IOException, InstantiationException,
			IllegalAccessException, InterruptedException {

		resource = ResourceBundle.getBundle("data_en_EN");
		final String MODULE_INTERFACE = "org.kolbas.common.interfaces.StringConvertable";

		if (!isArgsValid(args))
			return;

		boolean seeHints = false;
		JarLoader jarClassLoader = new JarLoader(args[0], seeHints);

		List<Class<?>> classes = jarClassLoader
				.getClassesImplementsInterface(MODULE_INTERFACE);
		if (classes.isEmpty()) {
			p(resource.getString("FileNotContainInterface")
					.replace("<file>", args[0])
					.replace("<interface>", MODULE_INTERFACE));
			return;
		}
		Class<?> plugin = classes.get(0);

		OneThread(plugin, args[1], args);

		MultiThread(plugin, args[1], args);

	} // main

}
