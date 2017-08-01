package com.sparkonix.entity.dto;

import java.util.Date;

import com.sparkonix.entity.Issue;
import com.sparkonix.entity.Reseller;



public class IssueWithResellerDTO {
	
	private Issue issue;
	private Reseller reseller;
	public Issue getIssue() {
		return issue;
	}
	public void setIssue(Issue issue) {
		this.issue = issue;
	}
	public Reseller getReseller() {
		return reseller;
	}
	public void setReseller(Reseller reseller) {
		this.reseller = reseller;
	}
}
