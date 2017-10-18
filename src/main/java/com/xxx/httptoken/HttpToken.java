package com.xxx.httptoken;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpToken {

	protected HttpToken(){
	}

	public void setAttribute(String name,Object obj){
		dataRepository.setAttribute(tokenId, name, obj, expire);
	}
	
	public void setAttributes(Map<String,Object> attrs){
		dataRepository.setAttribute(tokenId, attrs, expire);
	}
	
	public Object getAttribute(String name){
		return dataRepository.getAttribute(tokenId, name);
	}
	
	public Set<String> keys(){
		return dataRepository.getAtrributeNames(tokenId);
	}
	
	public void removeAttribute(String... name){
		dataRepository.removeAttribute(tokenId, name);
	}
	
	public void removeToken(){
		dataRepository.removeToken(tokenId);
	}
	
	public Long getResidualExpire() throws TokenException{
		
		return dataRepository.getExpire(tokenId);
	}
	
	public void refreshExpire() throws TokenException{
		dataRepository.refreshToken(tokenId, expire);
	}
	
	public void setProjectAttribute(String name,Object obj) {
		dataRepository.setProjectAttribute(name, obj);
	}
	
	public Object getProjectAttribute(String name) {
		return dataRepository.getProjectAttribute(name);
	}
	
	public void removeProjectAttribute(String... attrs) {
		dataRepository.removeProjectAttribute(attrs);
	}
	
	public void setProjectAttribute(Map<String,Object> map) {
		dataRepository.setProjectAttribute(map);
	}
	
	public List<Object> getMultiAttributes(List<String> tokenAttrs,List<String> projectAttrs) {
		return dataRepository.getMultiAttributes(tokenId, tokenAttrs, projectAttrs);
	}
	
	
	public long getExpire() {
		return expire;
	}

	public void setExpire(long expire) {
		this.expire = expire;
	}
	
	public String getId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public DataRepository getDataRepository() {
		return dataRepository;
	}

	public void setDataRepository(DataRepository dataRepository) {
		this.dataRepository = dataRepository;
	}
	
	String tokenId;
	
	DataRepository dataRepository;
	
	long expire = 1800;
}
