package ltd.xiaomizha.xuyou.common.config.captcha;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.impl.LocalMemoryResourceStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaptchaResourceConfiguration {

    @Bean
    public ResourceStore resourceStore() {
        LocalMemoryResourceStore resourceStore = new LocalMemoryResourceStore();
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "images/1727906019.jpeg", "default"));
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "images/1727906020.jpeg", "default"));
        resourceStore.addResource(CaptchaTypeConstant.ROTATE, new Resource("classpath", "images/48540923dd54564e08cf2ab9a7e4668bd0584f48.jpeg", "default"));
        resourceStore.addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource("classpath", "images/dc54564e9258d10935c6b4882c6136b66c814da2.jpeg", "default"));
        return resourceStore;
    }

}
