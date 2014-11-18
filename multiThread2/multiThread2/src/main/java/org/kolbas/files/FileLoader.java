
package org.kolbas.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Vector;

/**
 * @author Колбсов П.А.
 *
 */


/**
 * Загружает текстовый(е) файл(ы). Пока только в кодировке UTF-8.
 * 
 */

public class FileLoader{
	
	private  BufferedReader reader;

	
	/**
	 * Загружает текстовый файл.
	 * @param fname
	 *            путь к txt-файлу.
	 * 
	 */
	public FileLoader(String fname) throws IOException, FileNotFoundException {
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(fname), Charset.forName("UTF-8")));
	}

	
	/**
	 * Загружает текстовые файлы  в одном потоке.
	 * @param args
	 *            массив, хранящий пути к файлам.
	 *            
	 * @param start
	 *            индекс массива, с которого будут браться пути к файлам.
	 * 
	 */
	public FileLoader(String[] args, int start) throws IOException,
			FileNotFoundException {

		if (start < 0)
			start = 0;
		FileInputStream tmp;
		Vector<InputStream> inputStreams = new Vector<InputStream>();
		for (int i = start; i < args.length; i++) {
			tmp = new FileInputStream(args[i]);
			inputStreams.add(tmp);
		}
		String currCharSetName ="UTF-8";
		Enumeration<InputStream> enu = inputStreams.elements();
		SequenceInputStream sis = new SequenceInputStream(enu);
		reader = new BufferedReader(new InputStreamReader(sis,Charset.forName("UTF-8")));
		
	}

	/**
	 * Получает новую строку из файла. 
	 * 
	 *  @return null, если достигнут конец файла.
	 */
	public String next() {
		try {
			return reader.readLine();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Закрывает файл.
	 * 
	 */
	public void close() throws IOException {
		reader.close();
	}

}
