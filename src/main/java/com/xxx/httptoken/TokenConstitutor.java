package com.xxx.httptoken;

import javax.servlet.http.HttpServletRequest;

public class TokenConstitutor {
	
	public TokenConstitutor(){
	}
	
	public HttpToken getToken(HttpServletRequest req){
		String tokenId = tokenStrategy.getTokenId(req);
		HttpToken httpToken = (HttpToken) req.getAttribute(reqAttributePrefix + tokenId);
		if(httpToken == null){
			httpToken = new HttpToken();
			httpToken.setDataRepository(dataRepository);
			httpToken.setTokenId(tokenId);
			httpToken.setExpire(expire);
			req.setAttribute(reqAttributePrefix + tokenId, httpToken);
		}
		return httpToken;
	}
	
	public HttpToken getTokenById(String tokenId){
		HttpToken httpToken = new HttpToken();
		httpToken.setDataRepository(dataRepository);
		httpToken.setTokenId(tokenId);
		httpToken.setExpire(expire);
		return httpToken;
	}

	public TokenStrategy getTokenStrategy() {
		return tokenStrategy;
	}

	public void setTokenStrategy(TokenStrategy tokenStrategy) {
		this.tokenStrategy = tokenStrategy;
	}

	public DataRepository getDataRepository() {
		return dataRepository;
	}

	public void setDataRepository(DataRepository dataRepository) {
		this.dataRepository = dataRepository;
	}

	public long getExpire() {
		return expire;
	}

	public void setExpire(long expire) {
		this.expire = expire;
	}

	public final String reqAttributePrefix = "httptoken:";
	
	TokenStrategy tokenStrategy;
	
	DataRepository dataRepository;
	
	long expire = 1800;
	
}
