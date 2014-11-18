package org.kolbas.files;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * @author Колбсов П.А.
 *
 */


/**
 * Сохраняет строки в текстовый файл в кодировке UTF-8.
 *
 */

public class FileSaver {
	private  String fname;
	private  BufferedWriter writer;

	
	/**
	 * Сохраняет строки в текстовый файл в кодировке UTF-8.
	 */
	public FileSaver(String fname) throws UnsupportedEncodingException,
			FileNotFoundException {
		this.fname = fname;
		writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(this.fname), "utf-8"));
	}
	
	
	/**
	 * Сохраняет строку  в файл.
	 * 
	 */
	public void write(String value) throws IOException
	{
		writer.write(value+"\r\n");
	}
	
	/**
	 * Закрывает файл.
	 * 
	 */
	public void close() throws IOException {
		writer.close();
	}

}
