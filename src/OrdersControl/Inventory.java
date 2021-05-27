package OrdersControl;
import java.io.Serializable;
import java.util.ArrayList;

public class Inventory implements Serializable {
    private ArrayList<Order> ordersList = new ArrayList<>();

    public Inventory(){
    }


    public void addOrder(Order order){
        ordersList.add(order);
    }

    public void deleteOrder(Order order){
        ordersList.remove(order);
    }

    public ArrayList<Order> getOrdersList(){
        return ordersList;
    }
}
