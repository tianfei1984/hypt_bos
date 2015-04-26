package cn.com.gps.hypt.bos.resource;

import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.gps.hypt.bos.service.IVehicleMonitorService;
import cn.com.gps.hypt.common.cache.IDataAcquireCacheManager;
import cn.com.gps.hypt.common.cache.IRunningStatusCacheManager;
import cn.com.gps.hypt.common.cache.ITerminalCacheManager;
import cn.com.gps.hypt.common.cache.ITmnlVehiCacheManager;
import cn.com.gps.hypt.common.cache.ITripCacheManager;
import cn.com.gps.hypt.common.cache.IVehicleCacheManager;
import cn.com.gps.hypt.common.model.RunningState;
import cn.com.gps.hypt.common.tool.DateUtil;
import cn.com.hypt.db.dao.TripMapper;
import cn.com.hypt.db.model.Trip;
import cn.com.hypt.db.model.TripExample;

/**
 * 车辆状态查询接口
 * @author tianfei
 *
 */
@Controller
@RequestMapping("monitor")
public class VehicleMonitorResource {
	
	@Autowired
	private IDataAcquireCacheManager dataAcquireCacheManager;
	
	@Autowired
	private IRunningStatusCacheManager runningStatusCacheManager;
	
	@Autowired
	private ITripCacheManager tripCacheManager;
	
	@Autowired
	private ITerminalCacheManager terminalCacheManager;
	
	@Autowired
	private IVehicleCacheManager vehicleCacheManager;
	
	@Autowired
	private ITmnlVehiCacheManager tmnlVehCacheManager;
	
	@Autowired
	private TripMapper tripMapper;
	
	@Autowired
	private IVehicleMonitorService vehicleMonitorService;
	
	@RequestMapping
	public @ResponseBody String desc(){
		JSONObject json = new JSONObject();
		json.put("location?vid", "车辆当前位置信息");
		json.put("rstatus?vid", "查询车辆当前运行状态");
		json.put("trip?vid", "查询车辆当前轨迹点");
		json.put("dailyJob", "生成日统计");
		json.put("tripJob", "生成轨迹");
		
		return json.toString();
	}
	
	/**
	 * 查询所有车辆位置信息
	 * @return
	 */
	@RequestMapping(value="allVehicles" ,method=RequestMethod.POST,consumes="application/json")
	public @ResponseBody String getAllGps(@RequestBody JSONObject params){
	    JSONArray array = vehicleMonitorService.queryVehicleLocation(params);
	    
	    return array.toString();
	}
	

	/**
	 * 查询车辆当前位置信息
	 * @param vehicleId
	 * @return
	 */
	@RequestMapping(value = "location",method=RequestMethod.GET)
	public @ResponseBody String getVehicleGps(@RequestParam("vid")int vehicleId,@RequestParam("updated") long updated){
		JSONObject json = dataAcquireCacheManager.getGps(vehicleId);
		System.out.println(".");
		if(json != null && json.optLong("updated") > updated){
		    JSONObject j = new JSONObject();
		    j.put("longitude", json.get("longitude"));
		    j.put("latitude", json.get("latitude"));
		    j.put("speed", json.get("speed"));
		    j.put("updated", json.get("updated"));
		    json.put("position", j);
			return json.toString();
		}
		return "{}";
	}
	
	/**
	 * 查询车辆当前运行状态
	 * @param vehicleId
	 * @return
	 */
	@RequestMapping(value="rstatus",method=RequestMethod.GET)
	public @ResponseBody String getRunningSatus(@RequestParam("vid")int vehicleId) { 
		Date occurTime = DateUtil.addDate(DateUtil.formatDate(new Date()), 0);
		RunningState rs = runningStatusCacheManager.findLatestRunningState(vehicleId, occurTime);
		if(rs != null){
			return JSONObject.fromObject(rs).toString();
		}
		return "没有车辆运行状态";
	}
	
	/**
	 * 查询车辆当前轨迹点
	 * @param vehicleId
	 * @return
	 */
	@RequestMapping(value="trip",method=RequestMethod.GET)
	public @ResponseBody String getTrip(@RequestParam("vid")int vehicleId){
	    TripExample example = new TripExample();
	    example.or().andVehicleIdEqualTo(vehicleId);
	    List<Trip> list = tripMapper.selectByExampleWithBLOBs(example);
	    
		if(list != null && !list.isEmpty()){
			return list.get(0).getGps();
		}
		return "没有轨迹点";
	}
	
	/**
	 * 刷新缓存信息
	 * @return
	 */
	@RequestMapping(value="refreshCache",method=RequestMethod.GET)
	public @ResponseBody String refreshCache(){
		terminalCacheManager.initCache();
		vehicleCacheManager.initCache();
		tmnlVehCacheManager.initCache();
		
		return "success";
	}
	
	
}
