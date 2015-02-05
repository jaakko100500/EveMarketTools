package EveMarketTools.services;

import java.util.Date;

public class QueueEntry<ValueType> {
    public long amount;
    public ValueType object;
    public Date created = new Date();

    public QueueEntry(long amount, ValueType object) {
        this.amount = amount;
        this.object = object;
    }

    public QueueEntry() {};

    @Override
    public String toString() {
        return String.format("[%d,%s]", amount, object);
    }
}
