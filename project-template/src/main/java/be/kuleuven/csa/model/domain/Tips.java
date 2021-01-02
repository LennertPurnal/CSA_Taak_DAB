package be.kuleuven.csa.model.domain;

import java.util.HashMap;
import java.util.Map;

public class Tips {

    private String _id;
    private Map<String,String> tips;

    public Tips() {
        tips = new HashMap<String,String>();
        this._id = "TIPS";
    }

    public Map<String, String> getTips() {
        return tips;
    }

    public void setTips(Map<String, String> tips) {
        this.tips = tips;
    }

    public void setTip (String tip, String uitleg) {
        tips.put(tip,uitleg);
    }

}
