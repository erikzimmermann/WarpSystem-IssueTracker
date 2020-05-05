package de.codingair.warpsystem.spigot.features.teleportcommand.utils;

public class Invitation {
    private String sender;
    private String[] receiver; //notify if receiver.length == 1
    private boolean toSender; //only send if receiver.length == 1

    public Invitation(String sender, String[] receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public Invitation(String sender, boolean toSender, String receiver) {
        this.sender = sender;
        this.toSender = toSender;
        this.receiver = new String[] {receiver};
    }

    public void accept() {
        //to sender
    }

    public void deny() {
        //to sender
    }

    public void send() {
        //to receiver
    }

    public void cancel() {
        //destroy on entire network
    }

    public String getSender() {
        return sender;
    }

    public String[] getReceiver() {
        return receiver;
    }

    public boolean isToSender() {
        return toSender;
    }
}
