package gt.app.domain;

import gt.app.domain.customtype.PersistentEnum;

public enum Rating implements PersistentEnum {

    //store the value in DB so that calculation will be easy
    NEUTRAL(0), LIKE(1), DISKIKE(-1), LOVE(3), OFFENSIVE(-2);

    int value;

    Rating(int i) {
        this.value = i;
    }


    @Override
    public String getPersistedValue() {
        return String.valueOf(value);
    }
}
