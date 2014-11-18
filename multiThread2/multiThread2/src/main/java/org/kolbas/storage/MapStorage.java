
package org.kolbas.storage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.kolbas.files.FileSaver;

/**
 * @author Колбсов П.А.
 *
 */

/**
 * 
 * Класс-хранилище для случая, когда содержимое файлов помещается в оперативную
 * память.
 *
 */
public class MapStorage implements Storageable {

	private ConcurrentMap<String, Boolean> map;

	/**
	 * 
	 * Инициализирует хранилище.
	 *
	 */
	public MapStorage() {
		map = new ConcurrentHashMap<String, Boolean>();
	}

	/**
	 * 
	 * Инициализирует хранилище.
	 * 
	 * @param capasity
	 *            начальный размер хранилища.
	 *
	 */
	public MapStorage(int capasity) {
		map = new ConcurrentHashMap<String, Boolean>(capasity);
	}

	/**
	 * Помещает строку в хранилище.
	 * 
	 * @return true, если получилось (такого значения еще не было в хранилище),
	 *         false иначе.
	 */
	public synchronized Boolean put(String value) {

		return map.putIfAbsent(value, true);
	}

	/**
	 * Проверяет, не содержит ли уже хранилище данное значение.
	 * 
	 */
	public synchronized Boolean contains(String value) {
		return map.containsKey(value);

	}

	/**
	 * Сохраняет содержимое хранилища в текстовый файл в кодировке UTF-8.
	 * 
	 */
	public synchronized void saveToFile(String fileName) throws IOException {

		FileSaver saver = new FileSaver(fileName);
		for (String str : map.keySet()) {
			saver.write(str);
		}
		saver.close();
	}

}
