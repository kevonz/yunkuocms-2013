package com.yunkuo.cms.entity.assist;

import com.yunkuo.cms.entity.assist.base.BaseCmsCommentExt;

import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name="cms_comment_ext")
public class CmsCommentExt extends BaseCmsCommentExt {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public CmsCommentExt () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CmsCommentExt (java.lang.Integer id) {
		super(id);
	}

/*[CONSTRUCTOR MARKER END]*/


}