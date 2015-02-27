package com.motivator.cs446.motivator;

/**
 * Created by jacobsimon on 2/8/15.
 */
public class Task {
    public String title;
    public String deadline;
    enum State {IN_PROGRESS, COMPLETED, FAILED, DELETED};
    public State state;


    public Task(String title, String deadline) {
        this.title = title;
        this.deadline = deadline;
        this.state = State.IN_PROGRESS;
    }
}
