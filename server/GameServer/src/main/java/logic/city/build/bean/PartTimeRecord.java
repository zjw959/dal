package logic.city.build.bean;

import java.util.ArrayList;
import java.util.List;

/***
 * 兼职记录
 * 
 * @author lihongji
 *
 */
public class PartTimeRecord {

    /** 建筑id **/
    int buildingId;
    /** 兼职等级 **/
    int jobLevel;
    /** 经验值 **/
    long exp;
    /** 打工列表 **/
    List<JobRecord> jobList = new ArrayList<JobRecord>();


    public PartTimeRecord() {

    }


    public PartTimeRecord(int buildingId, int jobLevel, long exp) {
        this.buildingId = buildingId;
        this.jobLevel = jobLevel;
        this.exp = exp;
    }


    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }


    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public List<JobRecord> getJobList() {
        return jobList;
    }

    public void setJobList(List<JobRecord> jobList) {
        this.jobList = jobList;
    }


    public int getJobLevel() {
        return jobLevel;
    }


    public void setJobLevel(int jobLevel) {
        this.jobLevel = jobLevel;
    }



}
