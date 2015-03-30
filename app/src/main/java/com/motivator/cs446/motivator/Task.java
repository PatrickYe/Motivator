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

        if (this.repeat == null) {
            List<Integer> rec = new ArrayList<Integer>();
            for (int i = 0; i < 7; i++) {
                rec.add(i, 0);
            }
            this.repeat = rec;
        }
    }

    public Task(String title, Date deadline, State state, List<Integer> repeat) {
        this(title,deadline,state, repeat, new Date());
    }

    public Task(String title, Date deadline, State state) {
        this(title,deadline,state, null, new Date());
    }

    public boolean isRecurring() {
        return repeat != null && repeat.contains(1);
    }

    public Date getNextDueDate() {
        if (this.isRecurring()) {
            int currDay = deadline.getDay();
            for (int i = 1; i <= 7; i++) {
                if (repeat.get((currDay + i + 6) % 7) == 1) {
                    return new Date(deadline.getTime() + 1000*60*60*24*i);
                }
            }
        }
        return null;
    }
}
