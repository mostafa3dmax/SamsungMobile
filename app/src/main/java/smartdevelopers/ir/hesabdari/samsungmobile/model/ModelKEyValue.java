package smartdevelopers.ir.hesabdari.samsungmobile.model;

/**
 * Created by mostafa on 18/07/2018.
 */

public class ModelKEyValue {
    private String title;
    private String text;

    public ModelKEyValue(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
