package cn.protector.logic.entity;

public class PrizeInfo {
    private String title;
    private String content;
    private double prize;
    private int months;

    public PrizeInfo(String title, String content, double prize, int months) {
        this.title = title;
        this.content = content;
        this.prize = prize;
        this.months = months;
    }

    public int getMonths() {
        return months;
    }

    public void setMonths(int months) {
        this.months = months;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getPrize() {
        return prize;
    }

    public void setPrize(double prize) {
        this.prize = prize;
    }
}