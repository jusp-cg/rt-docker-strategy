package com.capgroup.dcip.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

/**
 * Centralized mechanism for converting between types (wrapper for Springs ConversionService) 
 */
@Service
public class ConverterUtils {

	private static ConversionService conversionService;

	@Autowired
	public ConverterUtils(ConversionService conversionService) {
		ConverterUtils.conversionService = conversionService;
	}

	public static <T> T convertTo(Object obj, Class<T> type) {
		return conversionService.convert(obj, type);
	}
}
