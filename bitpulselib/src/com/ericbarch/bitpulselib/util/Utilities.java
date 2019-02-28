package com.ericbarch.bitpulselib.util;

import java.text.DecimalFormat;

import com.ericbarch.bitpulselib.Currencies;

public class Utilities {
	// used for formatting currencies
	public static String roundTwoDecimalString(double d) {
	    DecimalFormat twoDForm = new DecimalFormat("0.00");
	    return twoDForm.format(d);
	}
	
	// useful for comparing values that will be displayed to the user
	public static double roundToTwoDecimals(double val) {
        return Double.valueOf(roundTwoDecimalString(val));
	}
	
	public static String currencyNameForConstant(int currency) {
		switch (currency) {
	    	case Currencies.USD:
	    		return "USD";
	    	case Currencies.AUD:
	    		return "AUD";
	    	case Currencies.CAD:
	    		return "CAD";
	    	case Currencies.CNY:
	    		return "CNY";
	    	case Currencies.EUR:
	    		return "EUR";
	    	case Currencies.GBP:
	    		return "GBP";
	    	case Currencies.JPY:
	    		return "JPY";
	    	case Currencies.NZD:
	    		return "NZD";
	    	case Currencies.PLN:
	    		return "PLN";
	    	case Currencies.RUB:
	    		return "RUB";
	    	case Currencies.SEK:
	    		return "SEK";
	    	case Currencies.SGD:
	    		return "SGD";
	    	case Currencies.NOK:
	    		return "NOK";
	    	case Currencies.CZK:
	    		return "CZK";
	    	default:
	    		return null;
		}
	}
	
	public static int currencyForString(String currency) {
		if (currency.equalsIgnoreCase("USD")) {
			return Currencies.USD;
		} else if (currency.equalsIgnoreCase("AUD")) {
			return Currencies.AUD;
		} else if (currency.equalsIgnoreCase("CAD")) {
			return Currencies.CAD;
		} else if (currency.equalsIgnoreCase("CNY")) {
			return Currencies.CNY;
		} else if (currency.equalsIgnoreCase("EUR")) {
			return Currencies.EUR;
		} else if (currency.equalsIgnoreCase("GBP")) {
			return Currencies.GBP;
		} else if (currency.equalsIgnoreCase("JPY")) {
			return Currencies.JPY;
		} else if (currency.equalsIgnoreCase("NZD")) {
			return Currencies.NZD;
		} else if (currency.equalsIgnoreCase("PLN")) {
			return Currencies.PLN;
		} else if (currency.equalsIgnoreCase("RUB")) {
			return Currencies.RUB;
		} else if (currency.equalsIgnoreCase("SEK")) {
			return Currencies.SEK;
		} else if (currency.equalsIgnoreCase("SGD")) {
			return Currencies.SGD;
		} else if (currency.equalsIgnoreCase("NOK")) {
			return Currencies.NOK;
		} else if (currency.equalsIgnoreCase("CZK")) {
			return Currencies.CZK;
		} else {
			return 0;
		}
	}
}