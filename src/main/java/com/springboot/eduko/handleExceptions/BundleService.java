package com.springboot.eduko.handleExceptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class BundleService {
    private final Master master;

    @Autowired
    public BundleService(Master master) {
        this.master = master;
    }

    public String getMessageEn(String key) {
        return master.messageSource().getMessage(key,null,new Locale("en"));
    }
    public String getMessageAr(String key) {
        return master.messageSource().getMessage(key,null,new Locale("ar"));
    }
    public Response getResponse(String key) {
        return new Response(getMessageEn(key),getMessageAr(key));
    }
}
