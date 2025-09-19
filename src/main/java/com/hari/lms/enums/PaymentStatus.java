package com.hari.lms.enums;

/**
 * Enumeration representing the different states of a payment transaction.
 * 
 * @author Hari Parthu
 */
public enum PaymentStatus {

    /**
     * Payment has been created but not yet processed
     */
    PENDING,

    /**
     * Payment is currently being processed by the payment gateway
     */
    PROCESSING,

    /**
     * Payment has been successfully completed
     */
    COMPLETED,

    /**
     * Payment has failed due to various reasons (insufficient funds, card declined,
     * etc.)
     */
    FAILED,

    /**
     * Payment has been cancelled by the user or system
     */
    CANCELLED,

    /**
     * Payment has been refunded to the customer
     */
    REFUNDED
}