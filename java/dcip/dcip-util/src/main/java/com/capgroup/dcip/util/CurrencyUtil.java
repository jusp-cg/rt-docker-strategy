package com.capgroup.dcip.util;

import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Currency;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.NoArgsConstructor;


public final class CurrencyUtil {

	private CurrencyUtil() {}
	
	/*
	 * 	Create  a Map of locale and currency symbol
	 */
	
    protected static SortedMap<Currency, Locale> currencyLocaleMap;
    
    static {
        currencyLocaleMap = new TreeMap<Currency, Locale>(new Comparator<Currency>() {
          public int compare(Currency c1, Currency c2){
              return c1.getCurrencyCode().compareTo(c2.getCurrencyCode());
          }
      });
      for (Locale locale : Locale.getAvailableLocales()) {
           try {
               Currency currency = Currency.getInstance(locale);
               currencyLocaleMap.put(currency, locale);
           	}catch (Exception e){
           }
      }
    }
	
    /*
     * 	Return the currency symbol
     */
    public static String getCurrencySymbol(String currencyCode) {
        Currency currency = Currency.getInstance(currencyCode);
        
        String cs = currency.getSymbol(currencyLocaleMap.get(currency));
        Pattern pt = Pattern.compile("\\p{Sc}");
        Matcher match = pt.matcher(cs);
        String s = "";
        while(match.find()) {
        	s = match.group();
        	cs=cs.replace("\\"+s,  "");
        }
        
        /*
         * This returns US$, since $ is for many currencies
         * We only want the Symbol. It may break for some currencies like ChF
         */
        
        // CS is the currency, S is the Symbol match
        
        if(s.length() == 0) {return cs;}
        else {return s;}
        
    }
    
    
	
}
