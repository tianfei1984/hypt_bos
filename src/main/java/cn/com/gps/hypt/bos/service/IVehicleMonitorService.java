package cn.com.gps.hypt.bos.service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 车辆监控业务接口
 * @author tianfei
 *
 */
public interface IVehicleMonitorService {
	
	/**
	 * 根据条件查询车辆信息
	 * @param params
	 * @return
	 */
	JSONArray queryVehicleLocation(JSONObject params);

}
