package com.pbl.service;

import com.pbl.dao.QuotesDAO;
import com.pbl.dao.QuotesDAOImp;

public class QuoteService {
    private QuotesDAO quotesDAO;

    public QuoteService() {
      
        this.quotesDAO = new QuotesDAOImp();
    }

    
    public String getRandomQuote() {
        return quotesDAO.getRandomQuote();
    }
}
