package org.kolbas.modules.second;

import java.util.ArrayList;

import org.kolbas.common.classes.StringSplitter;
import org.kolbas.common.interfaces.StringConvertable;

public class SecondModule implements StringConvertable {

	public String convert(String input) {
		StringSplitter splitter = new StringSplitter(input);
		StringBuffer buf = new StringBuffer(input.length());
		boolean flag = false;
		while (splitter.hasMoreStrings()) {
			String str = splitter.getNextString();
			if (splitter.isDelemiters(str)) {
				if (" ".equals(str)) {
					if (!flag)
						buf.append(str);
					flag = true;
				} else {
					buf.append(str);
					flag = false;
				}
			} else {
				buf.append(str);
				flag = false;
			}
		}
		return buf.toString();
	}

	public ArrayList<String> getDeletedStrings(String input) {
		ArrayList<String> emptyList = new ArrayList<String>();
		return emptyList;
	}

}
