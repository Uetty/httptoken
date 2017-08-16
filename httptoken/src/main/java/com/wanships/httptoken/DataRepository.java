package com.wanships.httptoken;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DataRepository {

	public void setAttribute(String tokenId,String name,Object obj,long expire);
	
	public Object getAttribute(String tokenId,String name);
	
	public Set<String> getAtrributeNames(String tokenId);
	
	public void setAttribute(String tokenId,Map<String,Object> map, long expire);
	
	public void removeAttribute(String tokenId,String... attrs);
	
	public void refreshToken(String tokenId,long expire) throws TokenException ;
	
	public Long getExpire(String tokenId) throws TokenException;
	
	void removeToken(String tokenId);
	
	public void setProjectAttribute(String name,Object obj);
	
	public Object getProjectAttribute(String name);
	
	public void removeProjectAttribute(String... attrs);
	
	public void setProjectAttribute(Map<String,Object> map);
	
	public List<Object> getMultiAttributes(String tokenId,List<String> tokenAttrs,List<String> projectAttrs);
}
