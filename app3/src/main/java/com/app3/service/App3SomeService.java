package com.app3.service;

import javax.inject.Inject;

public class App3SomeService {
  
  @Inject
  private App3SomeOtherService someOtherService;
  
  public String getSomething() {
    return "Something from App 3 ("+someOtherService.getSomethingOther()+")";
  }
  
}
