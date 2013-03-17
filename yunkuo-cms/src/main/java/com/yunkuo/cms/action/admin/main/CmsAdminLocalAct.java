package com.yunkuo.cms.action.admin.main;

import static com.yunkuo.common.page.SimplePage.cpn;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yunkuo.cms.entity.main.CmsGroup;
import com.yunkuo.cms.entity.main.CmsRole;
import com.yunkuo.cms.entity.main.CmsSite;
import com.yunkuo.cms.entity.main.CmsUser;
import com.yunkuo.cms.entity.main.CmsUserExt;
import com.yunkuo.cms.entity.main.CmsUserSite;
import com.yunkuo.cms.web.CmsUtils;
import com.yunkuo.cms.web.WebErrors;
import com.yunkuo.common.page.Pagination;
import com.yunkuo.common.web.CookieUtils;
import com.yunkuo.common.web.RequestUtils;

/**
 * 本站管理员ACTION
 * 
 * 
 */
@Controller
public class CmsAdminLocalAct extends CmsAdminAbstract {
	private static final Logger log = LoggerFactory
			.getLogger(CmsAdminLocalAct.class);

	@RequestMapping("/admin_local/v_list")
	public String list(String queryUsername, String queryEmail,
			Integer queryGroupId, Boolean queryDisabled, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser currUser = CmsUtils.getUser(request);
		Pagination pagination = manager.getPage(queryUsername, queryEmail, site
				.getId(), queryGroupId, queryDisabled, true,
				currUser.getRank(), cpn(pageNo), CookieUtils
						.getPageSize(request));
		model.addAttribute("pagination", pagination);

		model.addAttribute("queryUsername", queryUsername);
		model.addAttribute("queryEmail", queryEmail);
		model.addAttribute("queryGroupId", queryGroupId);
		model.addAttribute("queryDisabled", queryDisabled);

		return "admin/local/list";
	}

	@RequestMapping("/admin_local/v_add")
	public String add(HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser currUser = CmsUtils.getUser(request);
		List<CmsGroup> groupList = cmsGroupMng.getList();
		List<CmsRole> roleList = cmsRoleMng.getList();
		model.addAttribute("site", site);
		model.addAttribute("groupList", groupList);
		model.addAttribute("roleList", roleList);
		model.addAttribute("currRank", currUser.getRank());
		return "admin/local/add";
	}

	@RequestMapping("/admin_local/v_edit")
	public String edit(Integer id, Integer queryGroupId, Boolean queryDisabled,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		String queryUsername = RequestUtils.getQueryParam(request,
				"queryUsername");
		String queryEmail = RequestUtils.getQueryParam(request, "queryEmail");
		CmsUser currUser = CmsUtils.getUser(request);
		WebErrors errors = validateEdit(id, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsUser admin = manager.findById(id);
		CmsUserSite userSite = admin.getUserSite(site.getId());

		List<CmsGroup> groupList = cmsGroupMng.getList();
		List<CmsRole> roleList = cmsRoleMng.getList();

		model.addAttribute("cmsAdmin", admin);
		model.addAttribute("site", site);
		model.addAttribute("userSite", userSite);
		model.addAttribute("roleIds", admin.getRoleIds());
		model.addAttribute("groupList", groupList);
		model.addAttribute("roleList", roleList);
		model.addAttribute("currRank", currUser.getRank());

		model.addAttribute("queryUsername", queryUsername);
		model.addAttribute("queryEmail", queryEmail);
		model.addAttribute("queryGroupId", queryGroupId);
		model.addAttribute("queryDisabled", queryDisabled);
		return "admin/local/edit";
	}

	@RequestMapping("/admin_local/o_save")
	public String save(CmsUser bean, CmsUserExt ext, String username,
			String email, String password, Boolean viewonlyAdmin,
			Boolean selfAdmin, Integer rank, Integer groupId,
			Integer[] roleIds, Integer[] channelIds, Byte[] steps,
			Boolean[] allChannels, HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		WebErrors errors = validateSave(bean, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		Integer[] siteIds = new Integer[] { site.getId() };
		String ip = RequestUtils.getIpAddr(request);
		bean = manager.saveAdmin(username, email, password, ip, viewonlyAdmin,
				selfAdmin, rank, groupId, roleIds, channelIds, siteIds, steps,
				allChannels, ext);
		log.info("save CmsAdmin id={}", bean.getId());
		cmsLogMng.operating(request, "cmsUser.log.save", "id=" + bean.getId()
				+ ";username=" + bean.getUsername());
		return "redirect:v_list";
	}

	@RequestMapping("/admin_local/o_update")
	public String update(CmsUser bean, CmsUserExt ext, String password,
			Integer groupId, Integer[] roleIds, Integer[] channelIds,
			Byte step, Boolean allChannel, String queryUsername,
			String queryEmail, Integer queryGroupId, Boolean queryDisabled,
			Integer pageNo, HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		WebErrors errors = validateUpdate(bean.getId(), request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		bean = manager.updateAdmin(bean, ext, password, groupId, roleIds,
				channelIds, site.getId(), step, allChannel);
		log.info("update CmsAdmin id={}.", bean.getId());
		cmsLogMng.operating(request, "cmsUser.log.update", "id=" + bean.getId()
				+ ";username=" + bean.getUsername());
		return list(queryUsername, queryEmail, queryGroupId, queryDisabled,
				pageNo, request, model);
	}

	@RequestMapping("/admin_local/o_delete")
	public String delete(Integer[] ids, Integer queryGroupId,
			Boolean queryDisabled, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		String queryUsername = RequestUtils.getQueryParam(request,
				"queryUsername");
		String queryEmail = RequestUtils.getQueryParam(request, "queryEmail");
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsUser[] beans = manager.deleteByIds(ids);
		for (CmsUser bean : beans) {
			log.info("delete CmsAdmin id={}", bean.getId());
			cmsLogMng.operating(request, "cmsUser.log.delete", "id="
					+ bean.getId() + ";username=" + bean.getUsername());
		}
		return list(queryUsername, queryEmail, queryGroupId, queryDisabled,
				pageNo, request, model);
	}

	@RequestMapping(value = "/admin_local/v_channels_add")
	public String channelsAdd(Integer siteId, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		return channelsAddJson(siteId, request, response, model);
	}

	@RequestMapping(value = "/admin_local/v_channels_edit")
	public String channelsEdit(Integer userId, Integer siteId,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		return channelsEditJson(userId, siteId, request, response, model);
	}

	@RequestMapping(value = "/admin_local/v_check_username")
	public void checkUsername(String username, HttpServletResponse response) {
		checkUserJson(username, response);
	}

	@RequestMapping(value = "/admin_local/v_check_email")
	public void checkEmail(String email, HttpServletResponse response) {
		checkEmailJson(email, response);
	}

	private WebErrors validateSave(CmsUser bean, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		return errors;
	}

	private WebErrors validateEdit(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (vldExist(id, errors)) {
			return errors;
		}
		return errors;
	}

	private WebErrors validateUpdate(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (vldExist(id, errors)) {
			return errors;
		}
		if (vldParams(id, request, errors)) {
			return errors;
		}
		return errors;
	}

	private WebErrors validateDelete(Integer[] ids, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		errors.ifEmpty(ids, "ids");
		for (Integer id : ids) {
			vldExist(id, errors);
		}
		return errors;
	}

	private boolean vldExist(Integer id, WebErrors errors) {
		if (errors.ifNull(id, "id")) {
			return true;
		}
		CmsUser entity = manager.findById(id);
		if (errors.ifNotExist(entity, CmsUser.class, id)) {
			return true;
		}
		return false;
	}
	
	private boolean vldParams(Integer id, HttpServletRequest request,
			WebErrors errors) {
		CmsUser entity = manager.findById(id);
		CmsUser user = CmsUtils.getUser(request);
		if (entity.getRank() > user.getRank()) {
			errors.addErrorCode("error.noPermission", CmsUser.class, id);
			return true;
		}
		return false;
	}

}