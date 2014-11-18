
package org.kolbas.storage;

import java.io.IOException;

/**
 * @author Колбсов П.А.
 *
 */

/**
 * 
 * Интерфейс предназначен для создания хранилища. В зависимости от размера
 * файлов, доступной оперативной памяти и т.д., имплементируя данный интерфейс, можно создать
 * несколько реализаций классов-хранилищ. MapStorage - если файлы помещаются в
 * оперативную память, DatabaseStorage - если нет.
 *
 */
public interface Storageable {

	/**
	 * Помещает строку в хранилище.
	 * 
	 * @return true, если получилось (такого значения еще не было в хранилище),
	 *         false иначе.
	 */
	public Boolean put(String value);

	/**
	 * Проверяет, не содержит ли уже хранилище данное значение.
	 * 
	 */
	public Boolean contains(String value);

	/**
	 * Сохраняет содержимое хранилища в текстовый файл в кодировке UTF-8.
	 * 
	 */
	public void saveToFile(String fileName) throws IOException;

}
