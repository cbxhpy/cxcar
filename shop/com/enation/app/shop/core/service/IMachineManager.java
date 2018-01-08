package com.enation.app.shop.core.service;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.model.CarMachine;
import com.enation.framework.database.Page;

/**
 * 洗车机管理
 */
public interface IMachineManager {


	/**
	 * 删除洗车机
	 * @param ids
	 */
	public void delCarMachines(Integer[] ids);

	/**
	 * 分页读取洗车机信息
	 * @param order
	 * @param sort 
	 * @param page
	 * @param pageSize
	 * @param machine_number 
	 * @param uname 
	 * @param region_id 
	 * @param city_id 
	 * @param province_id 
	 * @param machine_name 
	 * @return
	 */
	public Page pageMachine(String order, String sort, int page, int pageSize, String uname, String machine_number, 
			Integer province_id, Integer city_id, Integer region_id, String machine_name);
	
	/**
	 * 根据距离查出附近的洗车机
	 * @author yexf
	 * 2017-4-2
	 * @param addr_lng 经度
	 * @param addr_lat 纬度
	 * @param distance 距离
	 * @param search_address 
	 * @return
	 */
	public List<CarMachine> getMachineList(String addr_lng, String addr_lat,
			Double distance, String search_address); 
	
	/**
	 * 根据编码获取洗车机器
	 * @author yexf
	 * 2017-4-8
	 * @param machine_number
	 * @return
	 */
	public CarMachine getMachineByNumber(String machine_number);

	/**
	 * 根据id获取洗车机器
	 * 2017-6-20
	 * @param  
	 * @param car_machine_id
	 * @return 
	 */
	public CarMachine getMachineById(String car_machine_id);
	
	/**
	 * update洗车机器
	 * 2017-5-27
	 * @param  
	 * @param CarMachine 
	 */
	public void updateMachineIsUse(String is_use, String car_machine_id);

	/**
	 * 添加洗车机器
	 * 2017-6-20
	 * @param  
	 * @param carMachine 
	 */
	public void addCarMachine(CarMachine carMachine);

	/**
	 * 修改洗车机
	 * 2017-6-20
	 * @param  
	 * @param carMachine 
	 */
	public void updateMachine(CarMachine carMachine);

	/**
	 * 新硬件-开启机器
	 * 2017-9-1
	 * @param member_id 
	 * @param machine_number 
	 * @param   
	 */
	public int startMachine(String machine_number, String member_id);

	/**
	 * 获取洗车机的状态
	 * 2017-9-5
	 * @param  
	 * @param new_number 
	 */
	public Map<String, Object> getMachineStatus(String new_number);

	/**
	 * 判断机器是否离线 0：离线 1：在线
	 * 2017-10-14
	 * @param  
	 * @param machine_number
	 * @return 
	 */
	public Integer checkMachineIsUse(String machine_number);

	/**
	 * 使用中的洗车机
	 * 2017-11-10
	 * @param  
	 * @param string
	 * @return 
	 */
	public List<CarMachine> getMachineByUse(String string);

	/**
	 * 获取es_machine的id
	 * 2017-11-10
	 * @param  
	 * @param machine_number
	 * @return 
	 */
	public Map<String, Object> getYJMachineByNumber(String machine_number);

	/**
	 * 10分钟没有通讯，更改硬件使用状态为结算
	 * @param id
	 * @param status
	 */
	public void updateHardwareStatus(String id, String status);

	/**
	 * 15分钟状态status还是0的，修改为1
	 * 2018-1-1
	 * @param   
	 */
	public void updateUseMachineToOver();

	/**
	 * 判断用户是否有开始使用中的机器
	 * @param member_id
	 * @return
	 */
	public Boolean checkMemberIsOpenMachine(String member_id);
	
}
