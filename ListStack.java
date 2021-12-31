import java.util.LinkedList;

public class ListStack<T> {
    private LinkedList<T> linkedList = new LinkedList<T>();

    public T set(int index,T t){
        return linkedList.set(index,t);
    }

    public T get(int index){
        return linkedList.get(index);
    }

    public int size(){
        return linkedList.size();
    }
    public void push(T t){
        linkedList.addFirst(t);
    }

    public T pop(){
        return linkedList.removeFirst();
    }

    public T peek(){
        T t = null;
        if (!linkedList.isEmpty()){
            t = linkedList.getFirst();
        }
        return t;
    }

    public boolean isEmpty(){
        return linkedList.isEmpty();
    }
}
