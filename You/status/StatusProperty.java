package status;

public class StatusProperty {
    private double current;
    private double max;

    public StatusProperty(double current, double max) {
        this.current = current;
        this.max = max;
    }

    // 隨時間回復或增加，但不能超過最大值
    public void add(double amount) {
        this.current = Math.min(this.current + amount, this.max);
    }

    // 執行行動時消耗數值，如果夠扣回傳 true，不夠回傳 false
    public boolean consume(double amount) {
        if (this.current >= amount) {
            this.current -= amount;
            return true;
        }
        return false;
    }

    public double getCurrent() { return current; }
    public double getMax() { return max; }
}