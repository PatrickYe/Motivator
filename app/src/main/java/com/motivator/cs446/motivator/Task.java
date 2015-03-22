package com.motivator.cs446.motivator;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jacobsimon on 2/8/15.
 */
public class Task  implements Serializable {
    public String title;
    public Date deadline;
    public Date completedOn;
    enum State {IN_PROGRESS, COMPLETED, FAILED, DELETED};
    public State state;
    public long id;


    public Task(String title, Date deadline) {
        this(title, deadline, State.IN_PROGRESS);
    }

    public Task(String title, Date deadline, State state) {
        this(title,deadline,state, new Date());
    }

    public Task(String title, Date deadline, State state, Date completedOn) {
        this.title = title;
        this.deadline = deadline;
        this.state = state;
        this.completedOn = completedOn;
    }
}
