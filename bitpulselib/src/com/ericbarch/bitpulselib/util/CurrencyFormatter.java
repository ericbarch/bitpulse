package com.ericbarch.bitpulselib.util;

import com.ericbarch.bitpulselib.Currencies;


public class CurrencyFormatter {
	public static String localizePrice(int currency, double amount) {
		switch (currency) {
        	case Currencies.USD:
        		return "$" + Utilities.roundTwoDecimalString(amount);
        	case Currencies.AUD:
        		return "AU$" + Utilities.roundTwoDecimalString(amount);
        	case Currencies.CAD:
        		return "CA$" + Utilities.roundTwoDecimalString(amount);
        	case Currencies.CNY:
        		return Utilities.roundTwoDecimalString(amount) + "\u5143";
        	case Currencies.EUR:
        		return Utilities.roundTwoDecimalString(amount) + "\u20ac";
        	case Currencies.GBP:
        		return "\u00a3" + Utilities.roundTwoDecimalString(amount);
        	case Currencies.JPY:
        		return "\u00a5" + Utilities.roundTwoDecimalString(amount);
        	case Currencies.NZD:
        		return "NZ$" + Utilities.roundTwoDecimalString(amount);
        	case Currencies.PLN:
        		return Utilities.roundTwoDecimalString(amount) + "z\u0142";
        	case Currencies.RUB:
        		return Utilities.roundTwoDecimalString(amount) + "RUB";
        	case Currencies.SEK:
        		return Utilities.roundTwoDecimalString(amount) + "Kr";
        	case Currencies.SGD:
        		return "SG$" + Utilities.roundTwoDecimalString(amount);
        	case Currencies.NOK:
        		return Utilities.roundTwoDecimalString(amount) + "Kr";
        	case Currencies.CZK:
        		return Utilities.roundTwoDecimalString(amount) + "CZK";
        	default:
        		return "N/A";
		}
	}
}
