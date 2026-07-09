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
    // 🌟 新增：讓劇情事件可以動態調整上限（例如：命運上限 +5）
    public void setMax(double newMax) {
        this.max = newMax;
        // 如果當前值因為上限縮水而超過了，就強制拉回最大值
        if (this.current > this.max) {
            this.current = this.max;
        }
    }

    public double getCurrent() { return current; }
    public double getMax() { return max; }
}
