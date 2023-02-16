package com.ltz.controller;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShutdownController implements ApplicationContextAware {
   private ApplicationContext context;

   @GetMapping({"/shutdown"})
   public String shutDownContext() {
      ConfigurableApplicationContext ctx = (ConfigurableApplicationContext)this.context;
      ctx.close();
      return "Bye~";
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.context = applicationContext;
   }
}
