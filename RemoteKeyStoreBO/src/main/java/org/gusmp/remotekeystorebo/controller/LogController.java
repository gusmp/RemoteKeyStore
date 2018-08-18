package org.gusmp.remotekeystorebo.controller;

import java.util.ArrayList;
import java.util.List;

import org.gusmp.remotekeystorebo.bean.response.GetLogsResponse;
import org.gusmp.remotekeystorebo.bean.response.LogResponse;
import org.gusmp.remotekeystorebo.entity.Log;
import org.gusmp.remotekeystorebo.entity.Log.SOURCE;
import org.gusmp.remotekeystorebo.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class LogController {

	private final int PAGE_SIZE = 20;
	
	@Autowired
	private LogService logService;
	
	@RequestMapping("/getLogs")
	@ResponseBody
	public GetLogsResponse getLogs(@RequestParam(required=false,name="start") Integer pageStart,
			@RequestParam(required=false) Integer count,
			@RequestParam(required=false) Boolean continueParam) {
		
		return findLogs(null, pageStart, count, continueParam);
	}

	@RequestMapping("/getLogs/{certificateId}")
	@ResponseBody
	public GetLogsResponse getLogs(@PathVariable int certificateId,
			@RequestParam(required=false, name="start") Integer pageStart,
			@RequestParam(required=false) Integer count,
			@RequestParam(required=false) Boolean continueParam) {
		
		
		return findLogs(certificateId, pageStart, count, continueParam);
	}
	
	private GetLogsResponse findLogs(Integer certificateId, 
			Integer pageStart,
			Integer count,
			Boolean continueParam) {
		
		GetLogsResponse getLogsResponse = new GetLogsResponse();
		List<LogResponse> logResponseList = new ArrayList<LogResponse>();
		List<Log> logList = null;
				
		if (pageStart == null) {
			if (certificateId != null) {
				logList = logService.getLogs(SOURCE.APPLICATION, certificateId, PageRequest.of(0, PAGE_SIZE));
				getLogsResponse.setTotal_count(logService.getLogCount(SOURCE.APPLICATION, certificateId));
			} else {
				logList = logService.getLogs(SOURCE.UNKNOWN, PageRequest.of(0, PAGE_SIZE));
				getLogsResponse.setTotal_count(logService.getLogCount(SOURCE.UNKNOWN));
			}
			getLogsResponse.setPos(0);
			
		}
		else {
			if (certificateId != null) {
				logList = logService.getLogs(SOURCE.APPLICATION, PageRequest.of(pageStart / PAGE_SIZE, PAGE_SIZE));
			} else {
				logList = logService.getLogs(SOURCE.UNKNOWN, PageRequest.of(pageStart / PAGE_SIZE, PAGE_SIZE));
			}
			
			getLogsResponse.setPos(pageStart);
			getLogsResponse.setTotal_count(null);
		}
		
		logList.forEach( e -> logResponseList. add(new LogResponse(e)));
		getLogsResponse.setData(logResponseList);
		
		return getLogsResponse;
	}
}
