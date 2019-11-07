package com.my.rn.sound;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.my.rn.sound.RNSound.RNSoundModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RNSoundPackage implements ReactPackage {

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext context) {
        List<NativeModule> modules = new ArrayList<>();
        modules.add(new RNSoundModule(context));
        modules.add(new RNSpeedToText(context));
        return modules;
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext context) {
        return Collections.emptyList();
    }
}
