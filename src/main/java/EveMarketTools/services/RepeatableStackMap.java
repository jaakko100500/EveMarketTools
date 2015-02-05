package EveMarketTools.services;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@Component
public class RepeatableStackMap<KeyType, ValueType> {
    private Map<KeyType, List<QueueEntry<ValueType>>> stackHashMap = new HashMap<>();

    public List<QueueEntry<ValueType>> take(KeyType key, Long amount) {
        List<QueueEntry<ValueType>> objectStack = stackHashMap.get(key);
        List<QueueEntry<ValueType>> output = new Stack<>();

        Long amountLeft = amount;
        if (objectStack == null || objectStack.isEmpty()) {
            return output;
        } else {
            while (amountLeft > 0 && !objectStack.isEmpty()) {
                QueueEntry storedEntry = objectStack.get(0);
                objectStack.remove(0);

                QueueEntry returnedEntry = new QueueEntry(storedEntry.amount, storedEntry.object);

                output.add(returnedEntry);

                if (amountLeft > storedEntry.amount) {
                    amountLeft -= storedEntry.amount;
                } else {
                    storedEntry.amount -= amountLeft;
                    returnedEntry.amount = amountLeft;
                    amountLeft = 0L;

                    if (storedEntry.amount != 0L) {
                        objectStack.add(0, storedEntry);
                    }
                }
            }
        }

        return output;
    }

    public void put(KeyType key, Long amount, ValueType object) {
        List<QueueEntry<ValueType>> objectStack = stackHashMap.get(key);

        if (objectStack == null) {
            objectStack = new Stack<>();
        }

        QueueEntry queueEntry = new QueueEntry(amount, object);
        objectStack.add(queueEntry);
        stackHashMap.put(key, objectStack);
    }

    //For Jackson
    @org.codehaus.jackson.annotate.JsonIgnore
    public boolean isEmpty() {
        return stackHashMap.isEmpty();
    }

    //For Jackson
    public Map<KeyType, List<QueueEntry<ValueType>>> getStackHashMap() {
        return stackHashMap;
    }
}
