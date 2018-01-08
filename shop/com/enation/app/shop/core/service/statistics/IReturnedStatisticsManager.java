package com.enation.app.shop.core.service.statistics;

import java.util.List;
import java.util.Map;

public interface IReturnedStatisticsManager {

	public List<Map> statisticsMonth_Amount(long year,long month);
	
	public List<Map> statisticsYear_Amount(int year);
}
