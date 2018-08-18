package org.gusmp.remotekeystorebo.formater;

import java.text.ParseException;
import java.util.Locale;

import org.gusmp.remotekeystorebo.controller.KeyStoreController.THEME;
import org.springframework.format.Formatter;

public class ThemeFormater implements Formatter<THEME> {

	@Override
	public String print(THEME theme, Locale arg1) {
		
		if (theme == null) {
			return THEME.WEBIX.name();
		}
		
		return theme.name();

	}

	@Override
	public THEME parse(String theme, Locale arg1) throws ParseException {

		if (theme == null) return THEME.WEBIX;
		else return THEME.valueOf(theme);
	}
	
	

}
