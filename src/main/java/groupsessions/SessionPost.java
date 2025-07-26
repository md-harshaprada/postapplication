package groupsessions;

public class SessionPost {
    private int questionId;
    private int sessionId;
    private String ownerName;
    private String question;
private String username;
    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getOwnerName() {
        return ownerName;
    }
    public String getUserName() {
        return username;
    }
    
    public void setUserName(String username) {
        this.username = username;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
