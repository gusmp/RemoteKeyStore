package org.gusmp.remotekeystorebo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	public static String parseDate(Date date) {
		return sdf.format(date);
	}

}
