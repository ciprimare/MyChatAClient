package com.mychataclient.enums;

/**
 * Created by ciprian.mare on 3/20/2015.
 */
public enum MessageType {
    LOGOUT(0),
    REGISTER_MESSAGE(1),
    LOGIN_MESSAGE(2),
    BROADCAST_MESSAGE(3);

    private int cod;

    MessageType(int cod) {
        this.cod = cod;
    }

    /**
     *
     * @return
     */
    public int getCod() {
        return cod;
    }
}
