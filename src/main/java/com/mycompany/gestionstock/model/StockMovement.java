package model;

import java.time.LocalDateTime;

public class StockMovement {
    public enum Type { IN, OUT }

    private final String productId;
    private final Type type;
    private final int qty;
    private final LocalDateTime date;
    private final String note;

    public StockMovement(String productId, Type type, int qty, LocalDateTime date, String note) {
        this.productId = productId;
        this.type = type;
        this.qty = qty;
        this.date = date;
        this.note = note;
    }

    public String getProductId() { return productId; }
    public Type getType() { return type; }
    public int getQty() { return qty; }
    public LocalDateTime getDate() { return date; }
    public String getNote() { return note; }
}