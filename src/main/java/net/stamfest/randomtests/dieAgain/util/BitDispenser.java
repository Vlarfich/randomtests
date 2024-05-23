package net.stamfest.randomtests.dieAgain.util;

import net.stamfest.randomtests.bits.Bits;

import java.util.LinkedList;
import java.util.Queue;

public class BitDispenser extends Dispenser{

    private Bits BITS;

    public BitDispenser(Bits bits) {
        super();
        this.BITS = bits;
        bits.forEach((x) -> {integers.add(x);});
    }

    private Queue<Integer> integers = new LinkedList<>();

    @Override
    void shift() {
        generated[0] = generated[1];
        generated[1] = generated[2];
        generated[2] = integers.poll();
        availableBits = 32;
    }

}
