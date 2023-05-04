package net.chess.gui.observer;

import java.util.List;
import java.util.ArrayList;

public class Observable {
    private final List<Observer> observers = new ArrayList <>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observable observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Object arg) {
        for (Observer observer : observers) {
            observer.update(this, arg);
        }
    }
}
