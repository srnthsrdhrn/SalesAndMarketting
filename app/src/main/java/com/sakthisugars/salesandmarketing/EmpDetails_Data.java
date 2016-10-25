package com.sakthisugars.salesandmarketing;

/**
 * Created by user on 8/6/2016.
 */
public class EmpDetails_Data {
    String Empid = null;
    String Headid = null;
    String Deptid = null;
    String Designationid = null;
    String Designationname = null;
    String Deptname= null;
    String Empname,Headname;
    public String getEmpname(){return Empname;}
    public void setEmpname(String empname){ this.Empname=empname;}
    public String getHeadname(){return Headname;}
    public void setHeadname(String headname){ this.Headname=headname;}
    public String getEmpid() {
        return Empid;
    }
    public void setEmpid(String empid) {
        this.Empid = empid;
    }
    public String getHeadid() {
        return Headid;
    }
    public void setHeadid(String headid) {
        this.Headid = headid;
    }
    public String getDeptid() {return Deptid;}
    public void setDeptid(String deptid) {this.Deptid = deptid;}
    public String getDesignationid() {return Designationid;}
    public void setDesignationid(String desinid) {this.Designationid = desinid;}
    public String getDepartmentname(){return Deptname;}
    public void setDepartmentname(String deptname){this.Deptname=deptname;}
    public String getDesignationname(){return Designationname;}
    public void  setDesignationname(String designame){this.Designationname=designame;}

}

