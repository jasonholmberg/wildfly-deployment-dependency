package com.app1.service;

import javax.inject.Inject;

public class SomeService {
  
  @Inject
  private SomeOtherService someOtherService;
  
  public String getSomething() {
    return "Something from App 1 ("+someOtherService.getSomethingOther()+")";
  }
  
}
