package org.gusmp.remotekeystorebo.bean.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCertificatesResponse extends BaseResponse {
	
	private List<SaveOrUpdateCertificateResponse> data = new ArrayList<SaveOrUpdateCertificateResponse>();
	private Integer pos;
	private Integer total_count;

}
