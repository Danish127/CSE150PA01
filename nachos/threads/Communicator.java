package nachos.threads;

import nachos.machine.*;


public class Communicator {
    /**
     * Allocate a new communicator.
     */
    public Communicator() {
    }

     
    public void speak(int word) {
//one thread has something to say.  
comLock.acquire();
//if nobody is listening or if something has already been said, speaker goes to sleep
while (listening == 0 || message != null) {
    speaker.sleep();
}
//make a new message and wake up someone to listen
message = new Integer(word);
listener.wake();
comLock.release();
    }

 
    public int listen() {
//there's another thread listening
comLock.acquire();
listening++;
//if there's not a message for the listener to hear, wake up a speaker and then immediately goes to sleep
while (message == null) {
    speaker.wake();
    listener.sleep();
}
//this thread hears the message and there is one fewer listener
int receivedMessage = message.intValue();
message = null;
listening--;
comLock.release();
return receivedMessage;
    }

    private static class Speaker implements Runnable {
Speaker(Communicator com, String name) {
    this.com = com;
    this.name = name;
}

public void run() {
    //two things to say
    for (int i = 0; i < 2; i++) {
com.speak(i);
System.out.println(name + " says " + i);
    }
    System.out.println(name + " is done");
}

private Communicator com;
private String name;
    }

    private static class Listener implements Runnable {
Listener(Communicator com, String name) {
    this.com = com;
    this.name = name;
}

public void run() {
    //two things to hear
    for (int i = 0; i < 2; i++) {
int heard = com.listen();
System.out.println(name + " hears " + heard);
    }
    System.out.println(name + " is done");
}

private Communicator com;
private String name;
    }

    public static void selfTest() {
Communicator com1 = new Communicator();
KThread thread1 = new KThread(new Speaker(com1, "Test 1"));
KThread thread2 = new KThread(new Listener(com1, "Test 2"));
KThread thread3 = new KThread(new Speaker(com1, "Test 3"));
thread1.fork();
thread2.fork();
thread3.fork();
//once main test is done then the other test get cut off because it is the main thread which is done
new Listener(com1, "main test").run();
    }

    private Integer message = null;
    private int listening = 0;
    private Lock comLock = new Lock();
    private Condition2 listener = new Condition2(comLock);
    private Condition2 speaker = new Condition2(comLock);
}
