/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.picture.impl;

import java.util.concurrent.atomic.AtomicInteger;

public class Deadlock {

    static class Friend {

        private final String name;

        public Friend(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public synchronized void bow(Friend bower) {
            System.out.format(Thread.currentThread() + "-" + this + " %s: %s"
                    + "  has bowed to me!%n",
                    this.name, bower.getName());
            bower.bowBack();
        }

        public synchronized void bowBack() {
            System.out.format(Thread.currentThread() + "-" + this + " %s: %s"
                    + " has bowed back to me!%n",
                    this.name/*, bower.getName()*/);
            AtomicInteger c = new AtomicInteger(0);
            c.addAndGet(1);
        }
    }

    public static void main(String[] args) {
        final Friend alphonse
                = new Friend("Alphonse");
        final Friend gaston
                = new Friend("Gaston");
        new Thread(new Runnable() {
            @Override
            public void run() {
                alphonse.bow(gaston);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                gaston.bow(alphonse);
            }
        }).start();
    }
}
