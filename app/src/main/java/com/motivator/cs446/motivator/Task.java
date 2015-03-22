package com.motivator.cs446.motivator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jacobsimon on 2/8/15.
 */
public class Task  implements Serializable {
    public String title;
    public Date deadline;

    public List<Integer> repeat;

    public Date completedOn;

    enum State {IN_PROGRESS, COMPLETED, FAILED, DELETED};
    public State state;
    public long id;


    public Task(String title, Date deadline) {
        this(title, deadline, State.IN_PROGRESS, null, new Date());
    }


    public Task(String title, Date deadline, State state, List<Integer> repeat, Date completedOn) {
        this.title = title;
        this.deadline = deadline;
        this.state = state;
        this.repeat = repeat;
        this.completedOn = completedOn;
    }

    public Task(String title, Date deadline, State state, List<Integer> repeat) {
        this(title,deadline,state, repeat, new Date());
    }

    public Task(String title, Date deadline, State state) {
        this(title,deadline,state, null, new Date());
    }
}
