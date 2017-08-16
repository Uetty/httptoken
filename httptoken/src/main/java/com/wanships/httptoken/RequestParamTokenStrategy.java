package com.wanships.httptoken;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

public class RequestParamTokenStrategy implements TokenStrategy {

	@Override
	public String getTokenId(HttpServletRequest req) {
		String tokenId = req.getParameter("access_token");
		if(tokenId == null || "".equals(tokenId.trim())){
			tokenId = req.getSession().getId();
		}
		if(tokenId == null || "".equals(tokenId.trim())){
			tokenId = UUID.randomUUID().toString();
		}
		return tokenId;
	}

}
