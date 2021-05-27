package Printing;

import OrdersControl.Box;
import OrdersControl.Order;
import OrdersControl.Unit;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class LabelPrinter {
    private final Robot robot;
    private final Keyboard keyboard;
    private final Order order;
    private final String orderNumber;

    public LabelPrinter(Order order) throws AWTException {
        robot = new Robot();
        keyboard = new Keyboard(robot);
        this.order = order;
        orderNumber = order.getOrderNumber();
    }

    public LabelPrinter() throws AWTException{
        robot = new Robot();
        keyboard = new Keyboard(robot);
        order = new Order();
        orderNumber = "";
    }

    public void printLabels(ArrayList<Box> boxesList, boolean wait) {
        Runnable task = () -> StartPrintLabels(boxesList, wait);
        Thread backgroundThread = new Thread(task);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    public void  printBoxID(ArrayList<Box> boxesList){
        Runnable task = () -> StartPrintBoxID(boxesList);
        Thread backgroundThread = new Thread(task);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    public void deleteLabels(ArrayList<Box> boxesList){
        Runnable task = () -> StartDeleteLabels(boxesList);
        Thread backgroundThread = new Thread(task);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    public void reprintLabels(ArrayList<Box> boxesList){
        Runnable task = () -> StartReprintLabels(boxesList);
        Thread backgroundThread = new Thread(task);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    private void StartReprintLabels(ArrayList<Box> boxesList){
        try {
            Thread.sleep(300);
            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_TAB);
            Thread.sleep(200);
            robot.keyRelease(KeyEvent.VK_ALT);
            robot.keyRelease(KeyEvent.VK_TAB);
            Thread.sleep(400);

            robot.keyPress(KeyEvent.VK_F1);
            robot.keyRelease(KeyEvent.VK_F1);
            Thread.sleep(300);
            keyboard.type('4');
            Thread.sleep(200);
            keyboard.type("\n");
            Thread.sleep(500);

            for (Box box : boxesList) {
                for (Unit unit : box.getUnitsList()) {
                    Thread.sleep(300);
                    keyboard.type(unit.getIntID());
                    Thread.sleep(200);
                    keyboard.type("\n");
                    Thread.sleep(1000);
                    keyboard.type("\n");
                    Thread.sleep(800);
                }
            }

            robot.keyPress(KeyEvent.VK_F1);
            robot.keyRelease(KeyEvent.VK_F1);
            Thread.sleep(1000);

            keyboard.type('1');
            Thread.sleep(200);
            keyboard.type("\n");
            Thread.sleep(300);
            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_TAB);
            Thread.sleep(200);
            robot.keyRelease(KeyEvent.VK_ALT);
            robot.keyRelease(KeyEvent.VK_TAB);
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void StartDeleteLabels(ArrayList<Box> boxesList){
        try {
            Thread.sleep(300);
            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_TAB);
            Thread.sleep(200);
            robot.keyRelease(KeyEvent.VK_ALT);
            robot.keyRelease(KeyEvent.VK_TAB);
            Thread.sleep(400);

            robot.keyPress(KeyEvent.VK_F1);
            robot.keyRelease(KeyEvent.VK_F1);
            Thread.sleep(300);
            keyboard.type('5');
            Thread.sleep(200);
            keyboard.type("\n");
            Thread.sleep(500);

            for (Box box : boxesList) {
                for (Unit unit : box.getUnitsList()) {
                    Thread.sleep(300);
                    keyboard.type(unit.getSerialNumber());
                    Thread.sleep(200);
                    keyboard.type("\n");
                    Thread.sleep(1000);
                    keyboard.type("\n");
                    Thread.sleep(800);
                }
            }

            robot.keyPress(KeyEvent.VK_F1);
            robot.keyRelease(KeyEvent.VK_F1);
            Thread.sleep(1000);

            keyboard.type('1');
            Thread.sleep(200);
            keyboard.type("\n");
            Thread.sleep(300);
            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_TAB);
            Thread.sleep(200);
            robot.keyRelease(KeyEvent.VK_ALT);
            robot.keyRelease(KeyEvent.VK_TAB);
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void StartPrintLabels(ArrayList<Box> boxesList, boolean wait) {
        try {
            if(wait){
                Thread.sleep(3000);
            } else {
                Thread.sleep(1000);
            }
            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_TAB);
            Thread.sleep(200);
            robot.keyRelease(KeyEvent.VK_ALT);
            robot.keyRelease(KeyEvent.VK_TAB);
            Thread.sleep(100);

            keyboard.type(orderNumber);
            Thread.sleep(400);
            keyboard.type("\n");
            Thread.sleep(400);
            if (orderNumber.contains("S")) {
                int index = orderNumber.indexOf("S");
                String salesOrder = orderNumber.substring(index);
                robot.keyPress(KeyEvent.VK_F3);
                robot.keyRelease(KeyEvent.VK_F3);
                Thread.sleep(400);
                keyboard.type(salesOrder);
                Thread.sleep(500);
            }
            keyboard.type("\n");
            Thread.sleep(500);

            for (Box box:boxesList) {
                for (Unit unit : box.getUnitsList()) {
                    String serialNumber = unit.getSerialNumber();

                    keyboard.type(serialNumber);
                    Thread.sleep(300);
                    keyboard.type("\n");
                    Thread.sleep(300);
                    keyboard.type(serialNumber.charAt(serialNumber.length() - 1));
                    Thread.sleep(300);
                    keyboard.type("\n");
                    Thread.sleep(350);

                    String macAddress = unit.getMacAddress();
                    if (macAddress != null) {
                        keyboard.type(macAddress);
                        Thread.sleep(350);
                        keyboard.type("\n");
                        Thread.sleep(300);
                    }
                    keyboard.type("\n");
                    Thread.sleep(300);
                    keyboard.type(order.getOptionNumber());
                    Thread.sleep(300);
                    keyboard.type("\n");
                    Thread.sleep(300);
                    keyboard.type("\n");
                    Thread.sleep(350);
                }
            }

            if (order.getPrintCount() > 0) {
                robot.keyPress(KeyEvent.VK_F1);
                robot.keyRelease(KeyEvent.VK_F1);
                Thread.sleep(300);
            }

            Thread.sleep(100);
            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_TAB);
            Thread.sleep(200);
            robot.keyRelease(KeyEvent.VK_ALT);
            robot.keyRelease(KeyEvent.VK_TAB);
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void StartPrintBoxID(ArrayList<Box> boxesList) {
        try {
            Thread.sleep(100);
            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_TAB);
            Thread.sleep(200);
            robot.keyRelease(KeyEvent.VK_ALT);
            robot.keyRelease(KeyEvent.VK_TAB);
            Thread.sleep(400);

            robot.keyPress(KeyEvent.VK_F5);
            robot.keyRelease(KeyEvent.VK_F5);
            Thread.sleep(300);

            for (Box box : boxesList) {
                for (Unit unit : box.getUnitsList()) {
                    keyboard.type(unit.getIntID());
                    Thread.sleep(200);
                    keyboard.type("\n");
                    Thread.sleep(1500);
                }

                robot.keyPress(KeyEvent.VK_F3);
                robot.keyRelease(KeyEvent.VK_F3);
                Thread.sleep(200);
                keyboard.type("\n");
                Thread.sleep(2500);
            }

            robot.keyPress(KeyEvent.VK_F1);
            robot.keyRelease(KeyEvent.VK_F1);
            Thread.sleep(1000);

            Thread.sleep(100);
            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_TAB);
            Thread.sleep(200);
            robot.keyRelease(KeyEvent.VK_ALT);
            robot.keyRelease(KeyEvent.VK_TAB);
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
