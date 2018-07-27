package de.codingair.warpsystem.transfer.packets.utils;

public class AnswerPacket<E> extends AssignedPacket {
    private E value;

    public AnswerPacket() {
    }

    public AnswerPacket(E value) {
        this.value = value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public E getValue() {
        return value;
    }
}
