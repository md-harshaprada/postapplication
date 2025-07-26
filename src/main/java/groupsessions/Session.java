package groupsessions;

import java.sql.Timestamp;

public class Session {
    private int id;
    private String owner;
    private String sessionName;
    private Timestamp startTime;
    private Timestamp endTime;
    private int groupId;
    private String adminType;
    private boolean onlyAdminsCanAnswer;

    public Session(int id, String owner, String sessionName, Timestamp startTime, Timestamp endTime, int groupId, String adminType, boolean onlyAdminsCanAnswer) {
        this.id = id;
        this.owner = owner;
        this.sessionName = sessionName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.groupId = groupId;
        this.adminType = adminType;
        this.onlyAdminsCanAnswer = onlyAdminsCanAnswer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public long getRemainingTime() {
        return endTime.getTime() - System.currentTimeMillis();
    }
    
    public int getGroupId() {
        return groupId; 
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    
    public String getAdminType() {
        return adminType;
    }

    public void setAdminType(String adminType) {
        this.adminType = adminType;
    }

    public boolean isOnlyAdminsCanAnswer() {
        return onlyAdminsCanAnswer;
    }

    public void setOnlyAdminsCanAnswer(boolean onlyAdminsCanAnswer) {
        this.onlyAdminsCanAnswer = onlyAdminsCanAnswer;
    }
    
    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", owner='" + owner + '\'' +
                ", sessionName='" + sessionName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", groupId=" + groupId +
                ", adminType='" + adminType + '\'' +
                ", onlyAdminsCanAnswer=" + onlyAdminsCanAnswer +
                '}';
    }
}

