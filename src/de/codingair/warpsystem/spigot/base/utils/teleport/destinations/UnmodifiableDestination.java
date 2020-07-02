package de.codingair.warpsystem.spigot.base.utils.teleport.destinations;

public class UnmodifiableDestination extends Destination {
    public UnmodifiableDestination(Destination d) {
        super();
        super.apply(d);
    }

    @Override
    public Destination apply(Destination destination) {
        throw new IllegalStateException("Field cannot be modified!");
    }

    @Override
    public void setId(String id) {
        throw new IllegalStateException("Field cannot be modified!");
    }

    @Override
    public void setType(DestinationType type) {
        throw new IllegalStateException("Field cannot be modified!");
    }

    @Override
    public void setAdapter(DestinationAdapter adapter) {
        throw new IllegalStateException("Field cannot be modified!");
    }

    @Override
    public void setOffsetX(double offsetX) {
        throw new IllegalStateException("Field cannot be modified!");
    }

    @Override
    public void setOffsetY(double offsetY) {
        throw new IllegalStateException("Field cannot be modified!");
    }

    @Override
    public void setOffsetZ(double offsetZ) {
        throw new IllegalStateException("Field cannot be modified!");
    }
}
