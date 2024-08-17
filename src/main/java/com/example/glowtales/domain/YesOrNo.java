package com.example.glowtales.domain;


import lombok.Getter;

@Getter
public enum YesOrNo {
    YES(0),
    NO(1);

    private final int value;

    YesOrNo(int value) {
        this.value = value;
    }

    public static YesOrNo fromValue(int value) {
        for (YesOrNo yesOrNo : YesOrNo.values()) {
            if (yesOrNo.getValue() == value) {
                return yesOrNo;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
