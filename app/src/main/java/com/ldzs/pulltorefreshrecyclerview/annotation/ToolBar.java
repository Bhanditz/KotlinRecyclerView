package com.ldzs.pulltorefreshrecyclerview.annotation;

import android.support.annotation.StringRes;

import com.ldzs.pulltorefreshrecyclerview.R;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ToolBar {
    @StringRes int value() default R.string.app_name;
    boolean back() default true;
}