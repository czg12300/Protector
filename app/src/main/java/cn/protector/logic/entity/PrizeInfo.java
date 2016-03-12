package cn.protector.logic.entity;

public class PrizeInfo {
    String title;
    String content;
    double prize;

    public PrizeInfo(String title, String content, double prize) {
        this.title = title;
        this.content = content;
        this.prize = prize;
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