package com.smartspend.backend.Config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.netty.util.internal.ObjectUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary(){
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name" , "dtgnumktz",
                "api_key" , "653997115744421",
                "api_secret" , "cEIJawMOZ8XbhaYvzjdbzCsgXwg"
        ));
    }
}
