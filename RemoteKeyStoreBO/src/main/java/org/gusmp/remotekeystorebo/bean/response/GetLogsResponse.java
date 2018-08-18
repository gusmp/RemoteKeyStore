package org.gusmp.remotekeystorebo.bean.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetLogsResponse extends BaseResponse {
	
	private List<LogResponse> data = new ArrayList<LogResponse>();
	private Integer pos;
	private Integer total_count;

}
