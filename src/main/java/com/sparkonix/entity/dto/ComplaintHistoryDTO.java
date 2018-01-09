package com.sparkonix.entity.dto;

import java.util.List;

import com.sparkonix.entity.ComplaintDetail;
import com.sparkonix.entity.Customer;
import com.sparkonix.entity.IssueNumberDetail;
import com.sparkonix.entity.Machine;
import com.sparkonix.entity.User;

/**
 * @author Pankaj Dhomkar Complaint History DTO object will store the whole
 *         information of complaint where complaint detail will have multiple
 *         result set from the database so it will stored in
 *         List<ComplaintDetail> so all history will be maintain and persist.
 */
public class ComplaintHistoryDTO {
    private List<ComplaintDetail> complaintDetail;
    private IssueNumberDetail issueNumberDetail;
    private Machine machine;
    private Customer customer;
    private User user;
    
    public List<ComplaintDetail> getComplaintDetail() {
        return complaintDetail;
    }
    public void setComplaintDetail(List<ComplaintDetail> complaintDetail) {
        this.complaintDetail = complaintDetail;
    }
    public IssueNumberDetail getIssueNumberDetail() {
        return issueNumberDetail;
    }
    public void setIssueNumberDetail(IssueNumberDetail issueNumberDetail) {
        this.issueNumberDetail = issueNumberDetail;
    }
    public Machine getMachine() {
        return machine;
    }
    public void setMachine(Machine machine) {
        this.machine = machine;
    }
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    
    
}
