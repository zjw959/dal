package kafka.team.action;

public interface TeamActionHandler<T> {

    public void process(T json);

}
