package com.app2.servlet;

import com.app1.service.SomeService;
import com.app3.service.App3SomeService;

import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("test")
public class App2Servlet extends HttpServlet {
  
  @Inject
  private SomeService someService;

  @Inject
  private App3SomeService app3SomeService;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    req.setAttribute("fromapp1", someService.getSomething());
    req.setAttribute("fromapp3", app3SomeService.getSomething());
    req.getRequestDispatcher("/WEB-INF/test.jsp").forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    doGet(req, resp);
  }

}
