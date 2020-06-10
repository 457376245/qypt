package com.web.bmo;

import java.util.Map;

public interface SeckillBmo {
     Map qrySeckillList(Map param) throws Exception;
     Map qrySeckillPro(Map param)throws Exception;
     Map saveActivity(Map param)throws Exception;
     Map qrySeckillAct(Map param)throws Exception;
     Map saveSeckill(Map param) throws Exception;
     Map saveSeckillStock(Map param) throws Exception;
     Map deleteAct(Map param)throws Exception;
     Map editPro(Map param)throws Exception;
     public Map reflashRedis();
}
