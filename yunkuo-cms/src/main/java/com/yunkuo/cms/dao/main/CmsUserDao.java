package com.yunkuo.cms.dao.main;

import java.util.List;

import com.yunkuo.cms.entity.main.CmsUser;
import com.yunkuo.common.hibernate3.Updater;
import com.yunkuo.common.page.Pagination;

/**
 * 用户DAO接口
 * 
 * 
 */
public interface CmsUserDao{
	public Pagination getPage(String username, String email, Integer siteId,
			Integer groupId, Boolean disabled, Boolean admin, Integer rank,
			int pageNo, int pageSize);
	
	public List getList(String username, String email, Integer siteId,
			Integer groupId, Boolean disabled, Boolean admin, Integer rank);

	public List<CmsUser> getAdminList(Integer siteId, Boolean allChannel,
			Boolean disabled, Integer rank);

	public CmsUser findById(Integer id);

	public CmsUser findByUsername(String username);

	public int countByUsername(String username);
	
	public int countMemberByUsername(String username);

	public int countByEmail(String email);

	public CmsUser save(CmsUser bean);

	public CmsUser updateByUpdater(Updater<CmsUser> updater);

	public CmsUser deleteById(Integer id);
}