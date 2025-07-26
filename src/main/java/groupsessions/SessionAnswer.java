package groupsessions;

import java.sql.Timestamp;

public class SessionAnswer {
    private int id;
    private int sessionId;
    private int questionId;
    private String userName;
    private String answer;
    private Timestamp answerTime;
    private int upvoteCount;

    public int getId() { 
        return id; 
    }
    public void setId(int id) { 
        this.id = id; 
    }

    public int getSessionId() { 
        return sessionId; 
    }
    public void setSessionId(int sessionId) { 
        this.sessionId = sessionId; 
    }

    public int getQuestionId() { 
        return questionId; 
    }
    public void setQuestionId(int questionId) { 
        this.questionId = questionId; 
    }

    public String getUserName() { 
        return userName; 
    }
    public void setUserName(String userName) { 
        this.userName = userName; 
    }

    public String getAnswer() { 
        return answer; 
    }
    public void setAnswer(String answer) { 
        this.answer = answer; 
    }

    public Timestamp getAnswerTime() { 
        return answerTime; 
    }
    public void setAnswerTime(Timestamp answerTime) { 
        this.answerTime = answerTime; 
    }

    public int getUpvoteCount() { 
        return upvoteCount;
    }
    public void setUpvoteCount(int upvoteCount) { 
        this.upvoteCount = upvoteCount;
    }
}
