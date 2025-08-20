package com.easemybooking.booking.exception;
public class CapacityExceededException extends RuntimeException {
    public CapacityExceededException(String msg) { super(msg); }
}
