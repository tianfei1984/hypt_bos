package cn.com.gps.hypt.bos.service.impl;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.gps.hypt.bos.service.IVehicleMonitorService;
import cn.com.gps.hypt.common.cache.IDataAcquireCacheManager;
import cn.com.gps.hypt.common.cache.IVehicleCacheManager;
import cn.com.hypt.db.dao.VehicleMapper;
import cn.com.hypt.db.model.Vehicle;
import cn.com.hypt.db.model.VehicleExample;

/**
 *  车辆监控业务实现类
 * @author tianfei
 *
 */
@Service
public class VehicleMonitorServiceImpl implements IVehicleMonitorService {
	
	//车辆缓存
	@Autowired
	private IVehicleCacheManager vehicleCacheManager;
	
	@Autowired
	private IDataAcquireCacheManager dataAcquireCacheManager;

	@Override
	public JSONArray queryVehicleLocation(JSONObject params) {
		//所有车辆ID
		List<Integer> list = vehicleCacheManager. findAllVehicleIds();
		JSONArray array = new JSONArray();
		JSONObject gps = null;
		Vehicle vehicle = null;
		for(Integer vId : list){
			// 车辆信息
			vehicle = vehicleCacheManager.findVehicleById(vId);
			//车辆位置
			gps = dataAcquireCacheManager.getGps(vId);
			gps.put("licensePlate", vehicle.getLicensePlate());
			array.add(gps);
		}
		
		return array;
	}
	
	

}
